package ua.nio.cs.ext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import sun.nio.cs.HistoricallyNamedCharset;

public class MappedCharset
    extends Charset
    implements HistoricallyNamedCharset {

  private InputStream charsetTableInputStream = null;
  private Map tableMapSingleByteToUnicode = null;
  private Map tableMapUnicodeToSingleByte = null;
  private boolean processCanonic     = false;
  private boolean processVisibleName = true;
  private boolean processAlias       = false;
  private String charsetVisibleName;

  private  String canonicalName = "UTF-8";
  private  String[] charsetAliases = null;

  public MappedCharset(InputStream charsetTableInputStream, String canonicalName, String charsetAliases[]) {
    super(canonicalName, charsetAliases);
    this.canonicalName = canonicalName;
    this.charsetAliases = charsetAliases;
    this.charsetTableInputStream = charsetTableInputStream;
    createCharsetTableMap();
  }

  public static CharsetParams getCharsetParams(InputStream charsetTableInputStream){
    boolean processAlias = false;
    boolean processCanonic = false;
    boolean processVisibleName = true;

    String canonicalName = "UTF-8";
    String charsetVisibleName = "UTF-8 Unicode";
    String[] charsetAliases = null;
    CharsetParams ret = new CharsetParams(canonicalName, charsetVisibleName, charsetAliases); ;

    if (charsetTableInputStream != null) {
      BufferedReader charsetTableBufferedReader = new BufferedReader(new
          InputStreamReader(
          charsetTableInputStream));
      String line = null;
      try {
        while ( (line = charsetTableBufferedReader.readLine()) != null) {
          if (!line.startsWith("#")) {

            if (processAlias) {
              processAlias = false;
              charsetAliases = parceAliases(line);
              break;
            }
            else
            if (processCanonic) {
              canonicalName = line;
              processCanonic = false;
              processAlias = true;
            }
            else
            if (processVisibleName) {
              charsetVisibleName = line;
              processVisibleName = false;
              processCanonic = true;
            }
          }
        }
//        if(charsetTableBufferedReader!=null)charsetTableBufferedReader.close();
//        charsetTableInputStream.reset();
        ret = new CharsetParams(canonicalName, charsetVisibleName, charsetAliases);
      }
      catch (IOException ex) {
        ex.printStackTrace();
        ret = null;
      }
    }
    return (ret);
  }

  private void createCharsetTableMap() {
    if (charsetTableInputStream == null) {
      return;
    }
    BufferedReader charsetTableBufferedReader = new BufferedReader(new
        InputStreamReader(
        charsetTableInputStream));
    tableMapSingleByteToUnicode = new HashMap();
    tableMapUnicodeToSingleByte = new HashMap();
    String line = null;
    try {
      while ( (line = charsetTableBufferedReader.readLine()) != null) {
        processTableLine(line);
      }
    }
    catch (IOException ex) {
      ex.printStackTrace();
      tableMapSingleByteToUnicode = null;
      tableMapUnicodeToSingleByte = null;
    }

  }

  /*
   CP 1125 (Ukrainian)           # name
   CP1125                        # canonical name
   "x-cp866-u", "ruscii", "1125" # aliases
   #
   # DOS Ukrainian (RUSCII, cp1125) to UNICODE translation table
   #
   # Based on cp866u_uni.tbl from lynx 2.8.2 distribution
   #       by porokh
   #
   0x80    0x0410  #CYRILLIC CAPITAL LETTER A
   0x81    0x0411  #CYRILLIC CAPITAL LETTER BE
   .........
   */

  private void processTableLine(String line) {
    if (line.startsWith("#")) {
      return;
    }
    if (processAlias) {
      processAlias = false;
      charsetAliases = parceAliases(line);
    }
    else
    if (processCanonic) {
      canonicalName = line;
      processCanonic = false;
      processAlias = true;
    }
    else
    if (processVisibleName) {
      charsetVisibleName = line;
      processVisibleName = false;
      processCanonic = true;
    }
    else {
      loadlineToMaps(line);
    }
  }

/*
  public String name(){
    super.name();
    return(canonicalName);
  }
*/

  private void loadlineToMaps(String line) {
    int  b = 0;
    char c = 0;
    StringTokenizer token = new StringTokenizer(line, " ");
    if (token.countTokens() > 2) {
      String sb = token.nextToken();
      String sc = token.nextToken();
      b = (byte) Integer.decode(sb).intValue();
      if(b<0) b+=256;
      c = (char) Integer.decode(sc).intValue();
      if (b > 0 && c > 0) {
        tableMapSingleByteToUnicode.put(new Integer(b), new Character(c));
        tableMapUnicodeToSingleByte.put(new Character(c), new Integer(b));
      }
    }
  }

  private static String[] parceAliases(String line) {
    String[] ret = null;
    StringTokenizer token = new StringTokenizer(line, ",");
    if (token.countTokens() > 0) {
      int i = 0;
      ret = new String[token.countTokens()];
      while (token.hasMoreTokens()) {
        String alias = token.nextToken().replace('"', ' ').trim();
        ret[i] = alias;
        i++;
      }
    }
    else {
      ret = new String[1];
      ret[0] = line.replace('"', ' ').trim();
    }
    return ret;
  }

  public boolean contains(Charset cs) {
    return cs.name().equalsIgnoreCase(canonicalName);
  }

  public CharsetDecoder newDecoder() {
    return new TableCharsetDecoder(this);
  }

  public CharsetEncoder newEncoder() {
    return new TableCharsetEncoder(this);
  }

  public String historicalName() {
    return canonicalName;
  }

  class TableCharsetEncoder
      extends CharsetEncoder {

    public TableCharsetEncoder(Charset cs) {
      super(cs, 1.0f, 1.0f);
    }

    /**
     * Encodes one or more characters into one or more bytes.
     *
     * <p> This method encapsulates the basic encoding loop, encoding as many
     * characters as possible until it either runs out of input, runs out of room
     * in the output buffer, or encounters a encoding error.  This method is
     * invoked by the {@link #encode encode} method, which handles result
     * interpretation and error recovery.
     *
     * <p> The buffers are read from, and written to, starting at their current
     * positions.  At most {@link Buffer#remaining in.remaining()} characters
     * will be read, and at most {@link Buffer#remaining out.remaining()}
     * bytes will be written.  The buffers' positions will be advanced to
     * reflect the characters read and the bytes written, but their marks and
     * limits will not be modified.
     *
     * <p> This method returns a {@link CoderResult} object to describe its
     * reason for termination, in the same manner as the {@link #encode encode}
     * method.  Most implementations of this method will handle encoding errors
     * by returning an appropriate result object for interpretation by the
     * {@link #encode encode} method.  An optimized implementation may instead
     * examine the relevant error action and implement that action itself.
     *
     * <p> An implementation of this method may perform arbitrary lookahead by
     * returning {@link CoderResult#UNDERFLOW} until it receives sufficient
     * input.  </p>
     *
     * @param  in
     *         The input character buffer
     *
     * @param  out
     *         The output byte buffer
     *
     * @return  A coder-result object describing the reason for termination
     */
    public CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
      if (tableMapSingleByteToUnicode == null || tableMapUnicodeToSingleByte == null) {
        return null;
      }
//      while (in.hasRemaining() && out.position() < out.limit()) {
      while (in.hasRemaining()) {
        char codedchar = in.get();
        byte codedbyte;

        if (!out.hasRemaining()) return CoderResult.OVERFLOW;
        codedbyte = (byte) codedchar;

//        if (codedchar < 0x0080 ) {
//          codedbyte = (byte) codedchar;
//        }
//        else {
        if (codedchar >= 0x0080 ) {
          Character codedChar = new Character(codedchar);
          Object codedObject = tableMapUnicodeToSingleByte.get(codedChar);
          Integer codedByte = codedObject == null ? null :
              (Integer) codedObject;
          codedbyte = codedByte.byteValue();
        }
        out.put( codedbyte );
      }
//      out.flip();
/*
      if (out.position() == out.limit()) {
        return CoderResult.OVERFLOW;
      }
*/
      return CoderResult.UNDERFLOW;
    }
  } //~TableCharsetEncoder

  class TableCharsetDecoder
      extends CharsetDecoder {

    public TableCharsetDecoder(Charset cs) {
      super(cs, 1.0f, 1.0f);
    }

    /**
     * Decodes one or more bytes into one or more characters.
     *
     * <p> This method encapsulates the basic decoding loop, decoding as many
     * bytes as possible until it either runs out of input, runs out of room
     * in the output buffer, or encounters a decoding error.  This method is
     * invoked by the {@link #decode decode} method, which handles result
     * interpretation and error recovery.
     *
     * <p> The buffers are read from, and written to, starting at their current
     * positions.  At most {@link Buffer#remaining in.remaining()} bytes
     * will be read, and at most {@link Buffer#remaining out.remaining()}
     * characters will be written.  The buffers' positions will be advanced to
     * reflect the bytes read and the characters written, but their marks and
     * limits will not be modified.
     *
     * <p> This method returns a {@link CoderResult} object to describe its
     * reason for termination, in the same manner as the {@link #decode decode}
     * method.  Most implementations of this method will handle decoding errors
     * by returning an appropriate result object for interpretation by the
     * {@link #decode decode} method.  An optimized implementation may instead
     * examine the relevant error action and implement that action itself.
     *
     * <p> An implementation of this method may perform arbitrary lookahead by
     * returning {@link CoderResult#UNDERFLOW} until it receives sufficient
     * input.  </p>
     *
     * @param  in
     *         The input byte buffer
     *
     * @param  out
     *         The output character buffer
     *
     * @return  A coder-result object describing the reason for termination
     */
    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
      if (tableMapSingleByteToUnicode == null || tableMapUnicodeToSingleByte == null) {
        return null;
      }
      int mark = in.position();
      char      codedchar=0;
      try {
      //while (in.hasRemaining() && out.position() < out.limit()) {
        while (in.hasRemaining()) {
          int codedbyte = in.get();

//          if (codedchar == '\r' || codedchar == '\n') {
//            continue;
//          }

          if(codedbyte<0) codedbyte+=256;
          codedchar = (char) codedbyte;
          if (codedbyte >= 0x80) {
              Integer codedByte = new Integer( codedbyte );
              Object codedObject = tableMapSingleByteToUnicode.get(codedByte);
              if(codedObject != null)
              {
                Character codedChar = (Character) codedObject;
                codedchar = codedChar.charValue();
              }
          }
          if (!out.hasRemaining())
            return CoderResult.OVERFLOW;
          mark++;
          out.put( codedchar );
//System.out.print("["+(codedbyte)+"]["+codedchar+"]");
          if (out.position() == out.limit()) {
            return CoderResult.OVERFLOW;
          }
        }
        return CoderResult.UNDERFLOW;
      }
      finally {
        in.position(mark);
//System.out.print("out ["+out.length()+"]");
      }
    }
  } //~TableCharsetDecoder

  public String getCharsetVisibleName() {
    return charsetVisibleName;
  }

  public String getCanonicalName() {
    return canonicalName;
  }

  public String[] getCharsetAliases() {
    return charsetAliases;
  }

  public String getCharsetAliasesString() {
    String ret = "";
    if (charsetAliases != null) {
      for (int i = 0; i < charsetAliases.length; i++) {
        ret += (i > 0 ? "," : "") + charsetAliases[i];
      }
    }
    return (ret);
  }

  public static class CharsetParams{
    String name;
    String aliases[];
    String visibleName;
    public CharsetParams(String name, String aliases[]){
      this(name, name, aliases);
    }
    public CharsetParams(String name, String visibleName, String aliases[]){
      this.name = name;
      this.aliases = aliases;
      this.visibleName = visibleName;
    }
    public String   getVisibleName(){return(visibleName);}
    public String   getName(){return(name);}
    public String[] getaliases(){return(aliases);}
  }

} //~MappedCharset

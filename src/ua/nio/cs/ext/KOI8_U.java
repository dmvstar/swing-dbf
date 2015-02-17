/* 
 *
 *  $Id: KOI8_U.java,v 0.0.1 2005/10/02 20:20 sdv Exp $
 *  
 *  Copyright (C) 2003-2004 Dmitry Starjinsky 
 *
 *  File :               KOI8_U.java
 *  Description :        
 *  Author's email :     dvstar@users.sourceforge.net   
 *  Author's Website :   
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package ua.nio.cs.ext;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

import sun.nio.cs.HistoricallyNamedCharset;
import sun.nio.cs.SingleByteDecoder;
import sun.nio.cs.SingleByteEncoder;

/**
 * Charset for Ukrainian KOI8-U.
 * <p>Title: </p>
 * Charset for Ukrainian KOI8-U.
 * <p>Description: </p>
 * Charset for Ukrainian KOI8-U.
 * Referenced classes of package ua.nio.cs.ext:
 * Реализация кодировки KOI8-U - расширение KOI8-R с поддержкой украинских
 * символов.
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author
 *         Andriy Rysin arysin@yahoo.com <br>
 *         Serhiy Brytskyy uranium@ukr.net <br>
 *         Dmitry Starjinsky dvstar@users.sourceforge.net
 * @version 0.0.1
 */
public class KOI8_U
    extends Charset
    implements HistoricallyNamedCharset {

  /**
   * Default constructor.
   */
  public KOI8_U() {
    //    super("KOI8-U", new String[] {
    //                  "KOI8_U", "koi8_u", "koi8-u",
    //                  "uk_UA.KOI8-U", "koi8u"}
    //         );
    super("KOI8-U", UkrainianCharsets.aliasesFor("KOI8-U"));
  }

  /**
   * Historical name of encoding.
   * @return String name
   */
  public String historicalName() {
    return "KOI8-U";
  }

  /**
   * Tells whether or not this charset contains the given charset.
   *
   * @return  <tt>true</tt> if, and only if, the given charset
   *          is contained in this charset
   */
  public boolean contains(Charset cs) {
    return cs instanceof KOI8_U;
  }

  /**
   * Constructs a new encoder for this charset. </p>
   *
   * @return  A new encoder for this charset
   *
   * @throws  UnsupportedOperationException
   *          If this charset does not support encoding
   */
  public CharsetEncoder newEncoder() {
    return new Encoder(this);
  }

  /**
   * Constructs a new decoder for this charset. </p>
   *
   * @return  A new decoder for this charset
   */
  public CharsetDecoder newDecoder() {
    return new Decoder(this);
  }

  public String getDecoderSingleByteMappings() {
    return Decoder.byteToCharTable;
  }

  public short[] getEncoderIndex1() {
    return Encoder.index1;
  }

  public String getEncoderIndex2() {
    return Encoder.index2;
  }

  /**
   * Decoder class for KOI8-U
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2005</p>
   * <p>Company: </p>
   * @author not attributable
   * @version 0.0.1
   */
  private static class Decoder
      extends SingleByteDecoder {

    private static final String byteToCharTable =

        "\u2500\u2502\u250C\u2510\u2514\u2518\u251C\u2524" + // 0x80 - 0x87
        "\u252C\u2534\u253C\u2580\u2584\u2588\u258C\u2590" + // 0x88 - 0x8F
        "\u2591\u2592\u2593\u2320\u25A0\u2219\u221A\u2248" + // 0x90 - 0x97
        "\u2264\u2265\u00A0\u2321\u00B0\u00B2\u00B7\u00F7" + // 0x98 - 0x9F
        "\u2550\u2551\u2552\u0451\u0454\u2554\u0456\u0457" + // 0xA0 - 0xA7
        "\u2557\u2558\u2559\u255A\u255B\u0491\u045E\u255E" + // 0xA8 - 0xAF
        "\u255F\u2560\u2561\u0401\u0404\u2563\u0406\u0407" + // 0xB0 - 0xB7
        "\u2566\u2567\u2568\u2569\u256A\u0490\u040E\u00A9" + // 0xB8 - 0xBF
        "\u044E\u0430\u0431\u0446\u0434\u0435\u0444\u0433" + // 0xC0 - 0xC7
        "\u0445\u0438\u0439\u043A\u043B\u043C\u043D\u043E" + // 0xC8 - 0xCF
        "\u043F\u044F\u0440\u0441\u0442\u0443\u0436\u0432" + // 0xD0 - 0xD7
        "\u044C\u044B\u0437\u0448\u044D\u0449\u0447\u044A" + // 0xD8 - 0xDF
        "\u042E\u0410\u0411\u0426\u0414\u0415\u0424\u0413" + // 0xE0 - 0xE7
        "\u0425\u0418\u0419\u041A\u041B\u041C\u041D\u041E" + // 0xE8 - 0xEF
        "\u041F\u042F\u0420\u0421\u0422\u0423\u0416\u0412" + // 0xF0 - 0xF7
        "\u042C\u042B\u0417\u0428\u042D\u0429\u0427\u042A" + // 0xF8 - 0xFF
        "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007" + // 0x00 - 0x07
        "\b\t\n\u000B\f\r\u000E\u000F" + // 0x08 - 0x0F
        "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017" + // 0x10 - 0x17
        "\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F" + // 0x18 - 0x1F
        "\u0020\u0021\"\u0023\u0024\u0025\u0026\'" + // 0x20 - 0x27
        "\u0028\u0029\u002A\u002B\u002C\u002D\u002E\u002F" + // 0x28 - 0x2F
        "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037" + // 0x30 - 0x37
        "\u0038\u0039\u003A\u003B\u003C\u003D\u003E\u003F" + // 0x38 - 0x3F
        "\u0040\u0041\u0042\u0043\u0044\u0045\u0046\u0047" + // 0x40 - 0x47
        "\u0048\u0049\u004A\u004B\u004C\u004D\u004E\u004F" + // 0x48 - 0x4F
        "\u0050\u0051\u0052\u0053\u0054\u0055\u0056\u0057" + // 0x50 - 0x57
        "\u0058\u0059\u005A\u005B\\\u005D\u005E\u005F" + // 0x58 - 0x5F
        "\u0060\u0061\u0062\u0063\u0064\u0065\u0066\u0067" + // 0x60 - 0x67
        "\u0068\u0069\u006A\u006B\u006C\u006D\u006E\u006F" + // 0x68 - 0x6F
        "\u0070\u0071\u0072\u0073\u0074\u0075\u0076\u0077" + // 0x70 - 0x77
        "\u0078\u0079\u007A\u007B\u007C\u007D\u007E\u007F"; // 0x78 - 0x7F

    /**
     * Constructor
     * @param cs Charset charset
     */
    public Decoder(Charset cs) {
      super(cs, byteToCharTable);
    }
  }

  /**
   * Encoder class for CP1125
   * <p>Title: </p>
   * <p>Description: </p>
   * <p>Copyright: Copyright (c) 2005</p>
   * <p>Company: </p>
   * @author not attributable
   * @version 0.0.1
   */
  private static class Encoder
      extends SingleByteEncoder {

    private final static String index2 =
        "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007" +
        "\b\t\n\u000B\f\r\u000E\u000F" +
        "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017" +
        "\u0018\u0019\u001A\u001B\u001C\u001D\u001E\u001F" +
        "\u0020\u0021\"\u0023\u0024\u0025\u0026\'" +
        "\u0028\u0029\u002A\u002B\u002C\u002D\u002E\u002F" +
        "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037" +
        "\u0038\u0039\u003A\u003B\u003C\u003D\u003E\u003F" +
        "\u0040\u0041\u0042\u0043\u0044\u0045\u0046\u0047" +
        "\u0048\u0049\u004A\u004B\u004C\u004D\u004E\u004F" +
        "\u0050\u0051\u0052\u0053\u0054\u0055\u0056\u0057" +
        "\u0058\u0059\u005A\u005B\\\u005D\u005E\u005F" +
        "\u0060\u0061\u0062\u0063\u0064\u0065\u0066\u0067" +
        "\u0068\u0069\u006A\u006B\u006C\u006D\u006E\u006F" +
        "\u0070\u0071\u0072\u0073\u0074\u0075\u0076\u0077" +
        "\u0078\u0079\u007A\u007B\u007C\u007D\u007E\u007F" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u009A\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u00BF\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u009C\u0000\u009D\u0000\u0000\u0000\u0000\u009E" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u009F" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u00B3\u0000\u0000\u00B4\u0000\u00B6\u00B7\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u00BE\u0000\u00E1" +
        "\u00E2\u00F7\u00E7\u00E4\u00E5\u00F6\u00FA\u00E9" +
        "\u00EA\u00EB\u00EC\u00ED\u00EE\u00EF\u00F0\u00F2" +
        "\u00F3\u00F4\u00F5\u00E6\u00E8\u00E3\u00FE\u00FB" +
        "\u00FD\u00FF\u00F9\u00F8\u00FC\u00E0\u00F1\u00C1" +
        "\u00C2\u00D7\u00C7\u00C4\u00C5\u00D6\u00DA\u00C9" +
        "\u00CA\u00CB\u00CC\u00CD\u00CE\u00CF\u00D0\u00D2" +
        "\u00D3\u00D4\u00D5\u00C6\u00C8\u00C3\u00DE\u00DB" +
        "\u00DD\u00DF\u00D9\u00D8\u00DC\u00C0\u00D1\u0000" +
        "\u00A3\u0000\u0000\u00A4\u0000\u00A6\u00A7\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u00AE\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u00BD" +
        "\u00AD\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0095" +
        "\u0096\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0097\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0098\u0099\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0093\u009B" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0080\u0000" +
        "\u0081\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0082\u0000\u0000\u0000\u0083\u0000" +
        "\u0000\u0000\u0084\u0000\u0000\u0000\u0085\u0000" +
        "\u0000\u0000\u0086\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0087\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0088\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0089\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u008A\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u00A0\u00A1" +
        "\u00A2\u00A4\u00A5\u00A6\u00A7\u00A8\u00A9\u00AA" +
        "\u00AB\u00AC\u00AD\u00AE\u00AF\u00B0\u00B1\u00B2" +
        "\u00B4\u00B5\u00B6\u00B7\u00B8\u00B9\u00BA\u00BB" +
        "\u00BC\u00BD\u00BE\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u008B\u0000" +
        "\u0000\u0000\u008C\u0000\u0000\u0000\u008D\u0000" +
        "\u0000\u0000\u008E\u0000\u0000\u0000\u008F\u0090" +
        "\u0091\u0092\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0094\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000\u0000\u0000" +
        "\u0000\u0000\u0000\u0000\u0000\u0000";

    private final static short index1[] = {
        0, 248, 248, 248, 503, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 734, 958, 248, 1214, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
        248, 248, 248, 248, 248, 248, 248, 248,
    };

    protected static int mask1 = 0xFF00;
    protected static int mask2 = 0xFF;
    protected static int shift = 8;

    /**
     * Constructor
     * @param cs Charset charset
     */
    public Encoder(Charset cs) {
      super(cs, index1, index2, mask1, mask2, shift);
    }
  }

}

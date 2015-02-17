package ua.nio.cs.ext;

import java.io.*;

public class TableCharsetTest {
  public static void main(String[] args) throws FileNotFoundException,
      IOException {

        javax.swing.JFileChooser fc = new javax.swing.JFileChooser(".");
        fc.showOpenDialog(null);
        if(fc.getSelectedFile()!=null)
        {
          java.io.File f = fc.getSelectedFile();
          java.io.FileInputStream fismc = new java.io.FileInputStream(f);
          MappedCharset mc = new MappedCharset( fismc , "CP1125", null);
          System.out.println("[TableCharset] getCharsetVisibleName()=["+ mc.getCharsetVisibleName()+"]");
          System.out.println("[TableCharset] getCanonicalName()=["+ mc.getCanonicalName()+"]");
          System.out.println("[TableCharset] getCharsetAliasesString()=["+ mc.getCharsetAliasesString()+"]");

          fc.showOpenDialog(null);
          f = fc.getSelectedFile();

          if( f!=null){
            FileInputStream fis = new FileInputStream(f);
            InputStreamReader sr = new InputStreamReader(fis, mc);
            char c[] = new char[fis.available()];
            sr.read(c);
            sr.close();
            fis.close();
            System.out.println("read=["+new String(c)+"]");
            FileOutputStream fos = new FileOutputStream(f.getName()+".new");
            OutputStreamWriter sw = new OutputStreamWriter(fos, mc);
            sw.write(c);
            sw.close();
            fos.close();
          }



          System.exit(0);
        }
        else

          System.exit(0);
  }
}

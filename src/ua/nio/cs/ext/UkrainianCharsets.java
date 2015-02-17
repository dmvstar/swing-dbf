/*
 *
 *  $Id: UkrainianCharsets.java,v 0.0.1 2005/10/02 20:20 sdv Exp $
 *
 *  Copyright (C) 2003-2004 Dmitry Starjinsky
 *
 *  File :               UkrainianCharsets.java
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

import java.lang.ref.SoftReference;
import sun.misc.VM;
import sun.nio.cs.AbstractCharsetProvider;

/**
 * Ukrainian charsets provider
 * <p>Title:</p>
 * Charset provider for Ukrainian charsets koi8-u cp1125 <br>
 * <p>Description:</p>
 *
 * Charset provider for Ukrainian charsets koi8-u cp1125 <br>
 * WARNING: Default charset KOI8-U not supported, using ISO-8859-1 instead <br>
 * WARNING: Default charset CP1125 not supported, using ISO-8859-1 instead <br>
 * <br>
 *
 * To edit this file open it using utf-8.
 *
 * For compile and run test use:<br>
 * Compile: javac -charset utf-8 UkrainianCharsets.java <br>
 * Run:     java UkrainianCharsets <br><br>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company:</p>
 * @author
 *         Serhiy Brytskyy uranium@ukr.net <br>
 *         Dmitry Starjinsky dvstar@users.sourceforge.net
 * @version 0.0.1
 */

public class UkrainianCharsets
    extends AbstractCharsetProvider {

  /**
   * Self instance.
   */
  static volatile SoftReference instance = null;

  /**
   * Initialized indicator.
   */
  private boolean initialized = false;

  /**
   * Print debug information.
   */
  private static boolean debug = false;
  private static boolean trace = false;

  /**
   * Default constructor.
   */
  public UkrainianCharsets() {
    // identify provider pkg name.
    super("ua.nio.cs.ext");

    if (debug) {
      System.out.println("UkrainianCharsets constructor");
      if (trace) {
        where();
      }
    }
    charset("KOI8-U", "KOI8_U", new String[] {"KOI8-U", "KOI8_U", "koi8_u",
            "koi8-u", "uk_UA.KOI8-U", "koi8u"});
    charset("CP1125", "CP1125", new String[] {"CP1125", "CP866A", "cp1125",
            "cp866a", "CP866-A", "cp866-a"});

    instance = new SoftReference(this);
  }

  /**
   * Initializing.
   */
    @Override
  protected void init() {

    if (initialized) {
      return;
    }
/*
    if (!VM.isBooted()) {
      return;
    }
*/
    initialized = true;
  }

  /**
   * Retrieves a charset aliases for the given charset name.
   * @param charsetName String charset name
   * @return String[] aliases
   */
  public static String[] aliasesFor(String charsetName) {
    if (debug) {
      System.out.println("UkrainianCharsets aliasesFor " + charsetName);
      if (trace) {
        where();
      }
    }
    SoftReference sr = instance;
    UkrainianCharsets sc = null;
    if (sr != null) {
      sc = (UkrainianCharsets) sr.get();
    }
    if (sc == null) {
      sc = new UkrainianCharsets();
      instance = new SoftReference(sc);
    }
    return sc.aliases(charsetName);
  }

  /**
   * Create exception to locate entry point.
   */
  protected static void where() {
    try {
      throw new RuntimeException();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Run test application.
   * @param args String[] params (not used)
   */

/*  moved to CharsetTest.java in packge charset.ukr
  public static void main(String args[]) {
    UkrainianCharsets uc = new UkrainianCharsets();
    java.util.Iterator iter = uc.charsets();

    boolean useGui = true;

    while (iter.hasNext()) {
      Object o = iter.next();
      System.out.print("A[" + (o != null ? o.toString() : "NULL") + "]");
    }
    System.out.println();

    printArray("KOI8-U", uc.aliasesFor("KOI8-U"));
    printArray("CP1125", uc.aliasesFor("CP1125"));

    uc.testWriter();

    Thread thread =
        new Thread(
        new Runnable() {
      public void run() {
        UkrainianCharsets uc = new UkrainianCharsets();
        uc.testWriter();
      }
    });

    if (useGui) {
      javax.swing.JFrame frame = new javax.swing.JFrame(
          "Test input Ukrainian charset");
      frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
      javax.swing.JTextField tf = new javax.swing.JTextField();
      frame.getContentPane().add(tf);
      frame.setVisible(true);
    }
    else {
      System.exit(0);
    }
  }
*/
}

/*
 javac -Xlint:unchecked UkrainianCharsets.java
    UkrainianCharsets.java:67: warning: [unchecked] unchecked call to SoftReference(T) as a member of the raw type java.lang.ref.SoftReference
    instance = new SoftReference(this);
                   ^
    UkrainianCharsets.java:102: warning: [unchecked] unchecked call to SoftReference(T) as a member of the raw type java.lang.ref.SoftReference
    instance = new SoftReference(sc);
                   ^
 2 warnings

  Charset
  This class also defines static methods for testing whether a particular charset is
  supported, for locating charset instances by name, and for constructing a map that
  contains every charset for which support is available in the current Java virtual machine.
 Support for new charsets can be added via the service-provider interface defined
  in the CharsetProvider class.

  CharsetProvider
 A charset provider is a concrete subclass of this class that has a zero-argument
  constructor and some number of associated charset implementation classes.
  Charset providers may be installed in an instance of the Java platform as extensions,
  that is, jar files placed into any of the usual extension directories.
 Providers may also be made available by adding them to the applet or application
  class path or by some other platform-specific means.
 Charset providers are looked up via the current thread's context class loader.

 http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4890726 KOI8-U
 http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4619777 Charset

 */

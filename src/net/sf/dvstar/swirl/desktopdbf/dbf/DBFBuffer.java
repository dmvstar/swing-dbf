/*
 * Library for displaing and manipulating of DBF files
 *
 * Copyright (C) 2009-2011 Dmytro Starzhynskyi (dvstar)
 * http://swirl.sourceforge.net/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.dvstar.swirl.desktopdbf.dbf;

import java.io.DataInput;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sdv
 */
public class DBFBuffer {

    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_RIGHT = 2;
    protected byte[] buf = null;
    protected int pos;

    /**
     * Reab 1 byte(s)
     * @return byte
     */
    public final byte readByte() {
        return buf[pos++];
    }

    public final short readShort() {
        short ret = buf[pos++];
        if (ret < 0) {
            ret += 256;
        }
        return ret;
    }

    /**
     * Read 2 byte(s)
     * @return int
     */
    public final int readInt() {
        byte[] b = buf; // a little bit optimization
        return (b[pos++] & 0xff) | ((b[pos++] & 0xff) << 8);
    }

    /**
     * Read 3 byte(s)
     * @return int
     */
    public final int readLongInt() {
        byte[] b = buf;
        return (b[pos++] & 0xff) | ((b[pos++] & 0xff) << 8)
                | ((b[pos++] & 0xff) << 16);
    }

    /**
     * Read 4 byte(s)
     * @return long
     */
    public final long readLong() {
        long ret = 0;
        if (pos < buf.length) { // 22.09.2005
            byte[] b = buf;
            ret = ((b[pos++] & 0xff))
                    | ((b[pos++] & 0xff) << 8)
                    | ((b[pos++] & 0xff) << 16)
                    | ((b[pos++] & 0xff) << 24);
        }
        return (ret);
    }

    public final String readString(String enc, int len) {
        int buflen = buf.length;
        String S = null;
        int i = 0;

        while (i < len && buf[i + pos] != 0) {
            i++;
        }

        if (pos < buflen) {
            //try
            {
                try {
                    S = new String(buf, pos, i, enc);
                    pos += (len); // update cursor
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(DBFBuffer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return S;
    }

    public final String readString(Charset enc, int len) {
        int buflen = buf.length;
        String S = null;
        int i = 0;

        if (buflen <= 0) {
            return "";
        }

        while (i < len && buf[i + pos] != 0) {
            i++;
        }

        if (pos < buflen) {
            //try
            {
                try {
                    byte bbb[] = new byte[i];
                    System.arraycopy(buf, pos, bbb, 0, i);
                    //ByteBuffer bb = ByteBuffer.wrap(ba, off, len);
                    //- gluk wery slooow on big arrays !
                    //S = new String(buf, pos, i, enc);
                    CharBuffer cbb = enc.decode(ByteBuffer.wrap(bbb));
                    //S = new String(bbb, 0, i, enc);
                    S = new String(cbb.array());
                    pos += (len); // update cursor
                } catch (Exception ex) {
                    Logger.getLogger(DBFBuffer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return S;
    }

    public static int readLittleEndianInt(DataInput in)
            throws IOException {

        int bigEndian = 0;
        for (int shiftBy = 0; shiftBy < 32; shiftBy += 8) {

            bigEndian |= (in.readUnsignedByte() & 0xff) << shiftBy;
        }

        return bigEndian;
    }

    public static short readLittleEndianShort(DataInput in)
            throws IOException {

        int low = in.readUnsignedByte() & 0xff;
        int high = in.readUnsignedByte();

        return (short) (high << 8 | low);
    }

    public static byte[] trimLeftSpaces(byte[] arr) {

        StringBuffer t_sb = new StringBuffer(arr.length);

        for (int i = 0; i < arr.length; i++) {

            if (arr[i] != ' ') {

                t_sb.append((char) arr[i]);
            }
        }

        return t_sb.toString().getBytes();
    }

    public static short littleEndian(short value) {

        short num1 = value;
        short mask = (short) 0xff;

        short num2 = (short) (num1 & mask);
        num2 <<= 8;
        mask <<= 8;

        num2 |= (num1 & mask) >> 8;

        return num2;
    }

    public static int littleEndian(int value) {

        int num1 = value;
        int mask = 0xff;
        int num2 = 0x00;

        num2 |= num1 & mask;

        for (int i = 1; i < 4; i++) {

            num2 <<= 8;
            mask <<= 8;
            num2 |= (num1 & mask) >> (8 * i);
        }

        return num2;
    }

    public static byte[] textPadding(String text, Charset characterSetName, int length) throws java.io.UnsupportedEncodingException {

        return textPadding(text, characterSetName, length, ALIGN_LEFT);
    }

    public static byte[] textPadding(String text, Charset characterSetName, int length, int alignment) throws java.io.UnsupportedEncodingException {

        return textPadding(text, characterSetName, length, alignment, (byte) ' ');
    }

    public static byte[] textPadding(String text, Charset characterSetName, int length, int alignment,
            byte paddingByte) throws java.io.UnsupportedEncodingException {

        if (text.length() >= length) {

            return text.substring(0, length).getBytes(characterSetName);
        }

        byte byte_array[] = new byte[length];
        Arrays.fill(byte_array, paddingByte);

        switch (alignment) {

            case ALIGN_LEFT:
                System.arraycopy(text.getBytes(characterSetName), 0, byte_array, 0, text.length());
                break;

            case ALIGN_RIGHT:
                int t_offset = length - text.length();
                System.arraycopy(text.getBytes(characterSetName), 0, byte_array, t_offset, text.length());
                break;
        }

        return byte_array;
    }

    public static byte[] doubleFormating(Double doubleNum, Charset characterSetName, int fieldLength, int sizeDecimalPart) throws java.io.UnsupportedEncodingException {

        int sizeWholePart = fieldLength - (sizeDecimalPart > 0 ? (sizeDecimalPart + 1) : 0);

        StringBuffer format = new StringBuffer(fieldLength);

        for (int i = 0; i < sizeWholePart; i++) {

            format.append("#");
        }

        if (sizeDecimalPart > 0) {

            format.append(".");

            for (int i = 0; i < sizeDecimalPart; i++) {

                format.append("0");
            }
        }

        DecimalFormat df = new DecimalFormat(format.toString());

        return textPadding(df.format(doubleNum.doubleValue()).toString(), characterSetName, fieldLength, ALIGN_RIGHT);
    }

    public static boolean contains(byte[] arr, byte value) {

        boolean found = false;
        for (int i = 0; i < arr.length; i++) {

            if (arr[i] == value) {

                found = true;
                break;
            }
        }

        return found;
    }

// write metods
    public final void writeBytes(byte[] b) {
        System.arraycopy(b, 0, buf, pos, b.length);
        pos += b.length;
    }

    public final void writeByte(byte b) {
        buf[pos++] = b;
        //pos++;
    }

    public final void writeInt(int i) {
        byte[] b = buf;
        b[pos++] = (byte) (i & 0xff);
        b[pos++] = (byte) (i >>> 8);
        //pos += 2;
    }

    public final void writeLongInt(int i) {
        byte[] b = buf;
        b[pos++] = (byte) (i & 0xff);
        b[pos++] = (byte) (i >>> 8);
        b[pos++] = (byte) (i >>> 16);
        //pos += 3;
    }

    public final void writeLong(long i) {
        byte[] b = buf;
        b[pos++] = (byte) (i & 0xff);
        b[pos++] = (byte) (i >>> 8);
        b[pos++] = (byte) (i >>> 16);
        b[pos++] = (byte) (i >>> 24);
        //pos += 4;
    }

    // Write null-terminated string
    public void writeString(String S, int len) {
        writeString(S, len, (byte) 0, null);
    }

    public void writeString(String S, int slen, byte fill, String encoding) {
        int len = S.length();
        if (len > slen) {
            len = slen;
        }
        for (int i = 0; i < len; i++) {
            buf[pos++] = (byte) S.charAt(i);
        }
        if (len < slen) {
            for (int i = len; i < slen; i++) {
                buf[pos++] = fill;
            }
        }
    }

    public final void writeString(String S) {
        int len = writeStringNoNull(S);
        buf[pos++] = 0;
//    sendlen += S.length() + 1;
        pos += len + 1;
    }

    public final void writeString(String S, String Encoding) throws java.io.UnsupportedEncodingException {
        int len = writeStringNoNull(S, Encoding);
        buf[pos++] = 0;
//    sendlen += S.length() + 1;
        pos += len + 1;
    }

    // Write string, with no termination
    public final int writeStringNoNull(String S) {
        int len = S.length();
        for (int i = 0; i < len; i++) {
            buf[pos++] = (byte) S.charAt(i);
        }
        return (len);
    }

    // Write a String using the specified character
    // encoding
    public final int writeStringNoNull(String S, String Encoding) throws java.io.UnsupportedEncodingException {
        int len = S.length();
        //\\ byte[] b = S.getBytes(Encoding);

        byte[] b = null;
        /*
        b = unicorn.sirius.utils.ByteConverter.StringToBytes(S, Encoding);
        if (b != null) {
        len = b.length;
        ensureCapacity(len);
        if (Driver.debug && Driver.level>7){
        System.out.println("[AnyBuffer][writeStringNoNull][<-] S=[" + S +
        "] pos=[" + pos + "] sendlen=[" + sendlen +
        "] buf=[" +
        buf.length + "][" + len + "]");
        System.out.println("[AnyBuffer] arraycopy(b, 0, buf, pos, len)");
        System.out.println("[AnyBuffer] arraycopy(" + b.length + ", 0, " +
        buf.length + ", " + pos + ", " + len + ")");
        }
        System.arraycopy(b, 0, buf, pos, len);
        pos += len;
        }
         *
         */
        return (len);
    }

    public void printArray(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            System.out.print("[" + b[i] + "]");
        }
        System.out.println();
    }
}

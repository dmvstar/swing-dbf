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

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class DBFField extends DBFBuffer {

    private static final String DEFAULT_SEPARATOR_STRUCT = ",";
    private static String separatorFieldStruct = DEFAULT_SEPARATOR_STRUCT;
    public static final byte TYPE_CHARACTER = 'C';
    public static final byte TYPE_DATE = 'D';
    public static final byte TYPE_FLOAT = 'F';
    public static final byte TYPE_NUMERIC = 'N';
    public static final byte TYPE_LOGICAL = 'L';
    public static final byte TYPE_MEMO = 'M';
    private static int len = 32;

    /**
     * @return the len
     */
    public static int getLen() {
        return len;
    }
//	unsigned char   type;           /* field type      11           */
    private String fieldName;
    private byte dataType; // int
//	long            locate;         /* field offset                 */
    public long locate;
//	unsigned char   dec;            /* знаки после точки   17       */
    private byte fieldLength; // int
    private byte decimalCount;
//	unsigned char   reserve[12];    /* reserved                     */
    public byte reserve[] = new byte[12];
//	unsigned char   typ1;           /* cx-type                      */
    public byte cxtype;
//	unsigned char   len1;           /* field length in stream       */
    public int slen;

    public DBFField(byte[] buf) {
        this.buf = buf;
        loadData();
    }

    public DBFField() {
        pos = 0;
        fieldName = "*";
        dataType = TYPE_CHARACTER; //'C';
        locate = 0;
        fieldLength = 1;
        decimalCount = 0;
    }

    private void loadData() {
        pos = 0;
        setFieldName(readString("cp866", 11));
        setDataType(readByte());
        locate = readLong();
        setFieldLength(readByte());
        setDecimalCount(readByte());
// System.out.println( this.toString() );
    }

    public byte[] getBytes() {
        buf = new byte[len];

        pos = 0;
        writeString(getFieldName(), 11); // 11 0-10
        writeByte( getDataType() );      // 1  11
        writeLong(locate);               // 4  12-15
        writeByte( getFieldLength() );      // 1  16
        writeByte( getDecimalCount() );  // 1  17
                                         // 32-18 18-31
        return buf;
    }


    public String formatDataStr(Object o) {
        String ret = new String(formatData(o));
        return ret;
    }

    public byte[] formatData(Object o) {
        byte c[] = new byte[this.getFieldLength()];

        if (o == null) {
            return arrayCopy(null, c, getDataType());
        }

        switch (this.getDataType()) {
            case DBFField.TYPE_CHARACTER: {
                String s = ((String) o); //.trim();
System.out.println("["+this.toString()+"]-["+s+"]");
                return arrayCopy(s.getBytes(), c, getDataType());
            }
            case DBFField.TYPE_FLOAT:
            case DBFField.TYPE_NUMERIC: {
                return arrayCopy(o.toString().getBytes(), c, getDataType());
            }
            case DBFField.TYPE_DATE: {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime((Date) o);
                byte[] dc;

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH) + 1;
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                dc = (""
                        + year
                        + (month < 10 ? "0" + month : "" + month)
                        + (day < 10 ? "0" + day : "" + day)).getBytes();

                return dc;
            }
        }

        return null;

    }

    private byte[] arrayCopy(byte[] source, byte[] dest, int type) {
        int sourceLen = 0;
        if (source != null) {
            sourceLen = source.length;
        }

        int dif = dest.length - sourceLen;

        if (dif < 0) {
            dif = 0;
        }

        int iniCopy = 0;

        if (type == DBFField.TYPE_CHARACTER) {
            iniCopy = 0;
        } else {
            iniCopy = dif;
        }

        for (int i = 0; i < dest.length; i++) {
            dest[i] = ' ';
        }

        for (int i = iniCopy, k = 0; (i < dest.length && k < sourceLen); i++, k++) {
            dest[i] = source[k];

        }

        return dest;
    }

    //PROF_ID,N,5,0|PROFESSION,C,30|DOC_SPR_ID,N,5,0|DOC_SPR,C,22|DOC_WHY,C,50|DOC_SERIAL,C,12|DOC_NUM,C,12|DOC_DATE,D
    public static DBFField makeDBFField(String item, int maxLen) {
        DBFField ret = new DBFField();

        if (item.indexOf(separatorFieldStruct) >= 0) {
            StringTokenizer parserItem = new StringTokenizer(item, separatorFieldStruct);
            if(parserItem.countTokens()>3) {
                ret.setFieldName(parserItem.nextToken());
                ret.setDataType( parserItem.nextToken().getBytes()[0] );
                ret.setFieldLength( Byte.parseByte( parserItem.nextToken()));
                ret.setDecimalCount( Byte.parseByte( parserItem.nextToken()));
            } else
            if(parserItem.countTokens()>2) {
                ret.setFieldName(parserItem.nextToken());
                ret.setDataType( parserItem.nextToken().getBytes()[0] );
                ret.setFieldLength( Byte.parseByte( parserItem.nextToken()));
                ret.setDecimalCount( (byte)0 );
            } else
            if(parserItem.countTokens()>1) {
                ret.setFieldName(parserItem.nextToken());
                ret.setDataType(parserItem.nextToken().getBytes()[0]);
                ret.setFieldLength((byte) maxLen);
                ret.setDecimalCount( (byte)0 );
            } else {
                ret.setFieldName(item);
                ret.setDataType( TYPE_CHARACTER );
                ret.setFieldLength((byte) maxLen);
                ret.setDecimalCount( (byte)0 );
            }
        } else {
            ret.setFieldName(item);
            ret.setDataType(TYPE_CHARACTER);
            ret.setFieldLength((byte) maxLen);
            ret.setDecimalCount( (byte)0 );
        }
        if( ret.getDataType( ) == TYPE_DATE ) {
            ret.setFieldLength((byte) 8);
        }

        return ret;
    }

    @Override
    public String toString() {
        return ("[" + getFieldName() + "][" + getDataType() + "][" + locate + "][" + getFieldLength() + "][" + getDecimalCount() + "]");
    }


    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName.toUpperCase();
    }

    /**
     * @return the dataType
     */
    public byte getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(byte dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the fieldLength
     */
    public byte getFieldLength() {
        return fieldLength;
    }

    /**
     * @param fieldLength the fieldLength to set
     */
    public void setFieldLength(byte fieldLength) {
        this.fieldLength = fieldLength;
    }

    /**
     * @return the decimalCount
     */
    public byte getDecimalCount() {
        return decimalCount;
    }

    /**
     * @param decimalCount the decimalCount to set
     */
    public void setDecimalCount(byte decimalCount) {
        this.decimalCount = decimalCount;
    }

    /**
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

}

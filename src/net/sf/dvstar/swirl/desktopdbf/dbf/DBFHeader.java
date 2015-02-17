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

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DBFHeader extends DBFBuffer {

    public static final byte ID_DBASE_III       = (byte)0x03;
    //public static final byte HEADER_TERMINATOR  = (byte)0x01;
    public static final byte HEADER_TERMINATOR  = (byte)0x0D;
    private static int len = 32;

    private ArrayList<Integer> maxColumnWidths = null;

    /**
     * @return the len
     */
    public static int getLen() {
        return len;
    }
    private Date date;
    private DBFField[] dbfFields;

    public DBFHeader(byte[] buf) {
        this.buf = buf;
        loadData();
    }

    public DBFHeader() {

    }


//	unsigned char   ver;            /* version - dBase 0x03         */
    private byte version;               /* 0 */
//	unsigned char   year;           /* file creation date  год      */
    byte year;                          /* 1 */
//	unsigned char   month;          /* file creation date  мес      */
    byte month;                         /* 2 */
//	unsigned char   day;            /* file creation date  день     */
    byte day;                           /* 3 */
//	unsigned short  lrec;           /* length of record             */
    private long recordCount;           /* 4-7   Количество записей в таблице */
    private int  headerLength;          /* 8-9 	 Количество байтов, занимаемых заголовком  Положение первой записи с данными*/
    private int  recordLength;          /* 10-11 Количество байтов, занимаемых записью */
//	char            reserve[19];    /* reserved                     */
    byte[] reserve01 = new byte[2];     /* 12-13 Зарезервированная область, заполнена нулями  */
    byte reserve02;                     /* 14 Флаг, указывающий на наличие незавершенной транзакции */
    private byte codePage;              /* 15 Флаг кодировки */
    byte[] reserve03 = new byte[12];    /* 16-27 Зарезервированная область для многопользовательского использования dBASE IV*/
    byte reserve04;                     /* 28 Флаг наличия MDX-файла: 01H - файл присутствует, 00H - файл отсутствует*/
    private byte langID;                /* 29 ID драйвера языка */
    byte[] reserve06 = new byte[2];     /* 30-31 Зарезервированная область, заполнена нулями */

//    byte[] reserve = new byte[20];
//	unsigned char   nfld;           /* number of fields (!)         */
    private byte fieldsCount;


//  Флаг находится по смещению 0x1D и для кириллицы принимает значения
//    0x65 - для DOS,
//    0xC9 - для Windows.

    /**
     * Load header from byte buffer
     */
    private void loadData() {
        pos = 0;
        version = readByte();
        year    = readByte();
        month   = readByte();
        day     = readByte();
        setRecordCount(readLong());
        setHeaderLength(readInt());
        setRecordLength(readInt());
        pos += 3; // skip rezerv
        setCodePage(readByte());
        pos += 13;// skip rezerv
        setLangID(readByte());
        pos += 2; // skip rezerv
        //setFieldCount(readByte());
        int hlen = getHeaderLength();
        int dlen = DBFHeader.getLen();
        int flen = DBFField.getLen();
        int cntf = (hlen - dlen) / flen;
        setFieldsCount((byte) ((getHeaderLength() - DBFHeader.getLen()) / DBFField.getLen()));
        setFieldsCount( (byte) cntf);
    }

    /**
     * Create byte array for header
     */
    public void makeData() throws DBFException {
        pos=0;
        int blen = (int) (DBFHeader.getLen() + (fieldsCount * DBFField.getLen()) + 1);
        buf = new byte[ blen ];

        writeByte( ID_DBASE_III );
        writeByte( year );
        writeByte( month );
        writeByte( day );
        writeLong( recordCount );
        writeInt( (int) (DBFHeader.getLen() + fieldsCount * DBFField.getLen()) +1);
        calculateRecordLength( getDbfFields() );
        int rlen = (int) recordLength;
        writeInt( rlen );
        pos += 3; // skip rezerv
        writeByte( getCodePage() );
        pos += 13;// skip rezerv
        writeByte( getLangID() );
        pos += 2; // skip rezerv
        // write field info
        for( int i=0; i<getDbfFields().length;i++){
            DBFField field = getDbfFields()[i];
//System.out.println("Field ["+i+"] "+field);
            byte fieldb[] = field.getBytes();
            writeBytes( fieldb );
        }
        // write field info
        writeByte( HEADER_TERMINATOR );
    }


    /**
     * @param version the version to set
     */
    public void setDate(Date date) {
        this.date = date;
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        this.year=  (byte)  (calendar.get(Calendar.YEAR) - 1900);
        this.month= (byte)  (calendar.get(Calendar.MONTH) + 1);
        this.day=   (byte)   calendar.get(Calendar.DAY_OF_MONTH);
    }

    public String getDate() {
        String ret = year +"/" +month+"/"+day;
        return ret;
    }


    /**
     * @return the version
     */
    public byte getVersion() {
        return version;
    }

    /**
     * @return the recordCount
     */
    public long getRecordCount() {
        return recordCount;
    }

    /**
     * @param recordCount the recordCount to set
     */
    public void setRecordCount(long recordCount) {
        this.recordCount = recordCount;
    }

    /**
     * @return the recordOffset
     */
    public int getHeaderLength() {
        return headerLength;
    }

    /**
     * @param recordOffset the recordOffset to set
     */
    public void setHeaderLength(int headerLength) {
        this.headerLength = headerLength;
    }

    /**
     * @return the recordLength
     */
    public int getRecordLength() {
        return recordLength;
    }

    /**
     * @param recordLength the recordLength to set
     */
    public void setRecordLength(int recordLength) {
        this.recordLength = recordLength;
    }

    /**
     * @return the fieldsCount
     */
    public byte getFieldsCount() {
        return fieldsCount;
    }

    /**
     * @param fieldCount the fieldCount to set
     */
    public void setFieldsCount(byte fieldsCount) {
        this.fieldsCount = fieldsCount;
    }

    /**
     * @return the codePage
     */
    public byte getCodePage() {
        return codePage;
    }

    /**
     * @param codePage the codePage to set
     */
    public void setCodePage(byte codePage) {
        this.codePage = codePage;
    }

    /**
     * @return the langID
     */
    public byte getLangID() {
        return langID;
    }

    /**
     * @param langID the langID to set
     */
    public void setLangID(byte langID) {
        this.langID = langID;
    }

    public void setFieldsData(DBFField[] dbfFields) {
        this.setDbfFields(dbfFields);
    }

    /**
     * Get the record length
     * @param dbfFields
     * @return
     */
    public void calculateRecordLength(DBFField[] dbfFields) {
        int ret = 1;
        for (int i=0; i<dbfFields.length; i++){
            ret += dbfFields[i].getFieldLength();
        }
        recordLength = ret;
    }

    /**
     * @return the dbfFields
     */
    public DBFField[] getDbfFields() {
        return dbfFields;
    }

    /**
     * @param dbfFields the dbfFields to set
     */
    public void setDbfFields(DBFField[] dbfFields) {
        this.dbfFields = dbfFields;
    }
/*
    void write(RandomAccessFile raf) throws IOException {
        raf.write( buf );
    }
*/
    void write(DataOutput outStream) throws IOException {
        outStream.write( buf, 0, buf.length );
    }

    @Override
    public String toString(){
        return "DBFHeader fieldsCount["+fieldsCount+
                "] recordCount["+recordCount+
                "] recordLength["+ recordLength +
                "] headerLength["+headerLength+"]";
    }

    public void calculateMaxColumnWidths(int start, ArrayList<ArrayList> sheet) {
        maxColumnWidths = new ArrayList();
        for(int i=start;i<sheet.size();i++){
            ArrayList<String> line = sheet.get(i);
            
            for(int j=0;j<line.size();j++) {
                int curW = 0;
                int newW = 0;
                if(getMaxColumnWidths().size()>j) {
                    curW = getMaxColumnWidths().get(j);
                    newW = Math.max(line.get(j).length(), curW);
                    getMaxColumnWidths().set(j, newW);
                } else {
//System.out.println("["+j+"]["+line.size()+"]["+line.get(j).length()+"]["+line.get(j)+"]");
                    newW = line.get(j).length();
                    getMaxColumnWidths().add(j, newW);
                }
            }
        }
    }

    public ArrayList<Integer> getMaxColumnWidths() {
        return maxColumnWidths;
    }

}



//typedef struct DbfHdr {
//	unsigned char   ver;            /* version - dBase 0x03         */
//	unsigned char   year;           /* file creation date  год      */
//	unsigned char   month;          /* file creation date  мес      */
//	unsigned char   day;            /* file creation date  день     */
//	long            nrec;           /* number of records            */
//	unsigned short  ofs;            /* first record offset          */
//	unsigned short  lrec;           /* length of record             */
//	char            reserve[19];    /* reserved                     */
//	unsigned char   nfld;           /* number of fields (!)         */
//} DbfHdr; 32 bytes

//typedef struct DbfFld {
//	unsigned char   name[11];       /* field name                   */
//	unsigned char   type;           /* field type                   */
//	long            locate;         /* field offset                 */
//	unsigned char   len;            /* field length                 */
//	unsigned char   dec;            /* знаки после точки            */
//	unsigned char   reserve[12];    /* reserved                     */
//	unsigned char   typ1;           /* cx-type                      */
//	unsigned char   len1;           /* field length in stream       */
//} DbfFld; 32 bytes



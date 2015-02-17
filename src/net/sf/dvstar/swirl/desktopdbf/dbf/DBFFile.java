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

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import ua.nio.cs.ext.CP1125;

/**
 * Base class for manipulating of DBF file
 * @author sdv
 */
public class DBFFile implements DBFFileInterface {

    protected final int END_OF_DATA = 0x1A;

    public static final int CHARSET_CP1125 = 1;
    public static final int CHARSET_KOI8U  = 2;
    public static final int CHARSET_CP1251 = 3;

    public static final int CHAR_W0 = 10;

    //Charset defaultCharset = new sun.nio.cs.IBM866();//    CP1125();
    private Charset defaultCharset = new CP1125();
    private File dbfFile = null;
    private boolean fileLoaded;
    /**
     * Data content of DBF file
     */
    protected DBFHeader       dbfHeader = null;
    protected DBFField[]      dbfFields = null;
    protected DBFRawData      dbfRawData = null;
    private ArrayList<DBFRecord> dbfRecords = new ArrayList();


    private DBFChangeMap changeMap = new DBFChangeMap();

    public DBFFile(File dbfFile) throws FileNotFoundException {
        this.dbfFile = dbfFile;
    }

    public File getFile() {
        return dbfFile;
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        //changeMap.put(new ChangeIndex(rowIndex, columnIndex), aValue);
        //changeMap.put("" + rowIndex + "x" + columnIndex, aValue);
        changeMap.addChangeIndex(rowIndex, columnIndex, aValue);
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Object ret = null;
        //ret = changeMap.get(new ChangeIndex(rowIndex, columnIndex));
        ret = changeMap.getValueAt(rowIndex, columnIndex);// get("" + rowIndex + "x" + columnIndex);
        return (ret);
    }

    @Override
    public boolean isFileLoaded() {
        return fileLoaded;
    }

    @Override
    public DBFChangeMap getDBFChangeMap() {
        return changeMap;
    }

    @Override
    public void setCharsetView(Charset charset) {
       defaultCharset = charset;
    }

    @Override
    public void setCharsetView(String charset) {
        if (charset.equals("CP1125")) {
            defaultCharset = new CP1125();
        } else {
            defaultCharset = Charset.forName(charset);
        }
    }

    @Override
    public Charset getCharsetView() {
        return defaultCharset;
    }

    @Override
    public DBFHeader getDBFHeader() {
        return getDbfHeader();
    }

    @Override
    public DBFField[] getDBFFields() {
        return getDbfFields();
    }

    @Override
    public DBFRawData getDBFRawData() {
        return getDbfRawData();
    }

    /**
     * @return the dbfHeader
     */
    public DBFHeader getDbfHeader() {
        return dbfHeader;
    }

    /**
     * @param dbfHeader the dbfHeader to set
     */
    public void setDbfHeader(DBFHeader dbfHeader) throws DBFException {
        this.dbfHeader = dbfHeader;
        this.dbfHeader.makeData();
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

    /**
     * @return the dbfRawData
     */
    public DBFRawData getDbfRawData() {
        return dbfRawData;
    }

    /**
     * @param dbfRawData the dbfRawData to set
     */
    public void setDbfRawData(DBFRawData dbfRawData) {
        this.dbfRawData = dbfRawData;
    }

    /**
     * @return the dbfRecords
     */
    public ArrayList<DBFRecord> getDbfRecords() {
        return dbfRecords;
    }

    /**
     * @param dbfRecords the dbfRecords to set
     */
    public void setDbfRecords(ArrayList<DBFRecord> dbfRecords) {
        this.dbfRecords = dbfRecords;
    }

}

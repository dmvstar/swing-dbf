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

/**
 *
 * @author sdv
 */
public class DBFRawData extends DBFBuffer {


    private DBFFileInterface  dbfFile;
    private DBFHeader         dbfHeader;
    private DBFField[]       dbfFields;
    int cellCount = 0;


    public DBFRawData(byte[] rawData, DBFFileInterface dbfFile) {
        this.buf = rawData;
        this.dbfFile   = dbfFile;
        this.dbfHeader = dbfFile.getDBFHeader();
        this.dbfFields = dbfFile.getDBFFields();
    }

/*
    public DBFHeader getDBFHeader() {
        return dbfHeader;
    }

    public DBFField[] getDBFFields() {
        return dbfFields;
    }
*/

    public String getValueAt(int rowIndex, int columnIndex) {
        String ret = "[" + rowIndex + "][" + columnIndex + "]";
        cellCount++;
        pos = (int) ((rowIndex * dbfHeader.getRecordLength()) + dbfFields[columnIndex].locate);
        //!!long tm_stt = System.nanoTime();
        //!!ret = readString("cp866", dbfFld[columnIndex].flen);
        ret = readString(dbfFile.getCharsetView(), dbfFields[columnIndex].getFieldLength());
        if (dbfFields[columnIndex].getDataType() == 'N') {
            ret = ret.trim();
        }
        //!!long tm_end = System.nanoTime();
        //!!System.out.println("readString time is " + (tm_end - tm_stt) + " ns " + cellCount);
        return (ret);
    }

}

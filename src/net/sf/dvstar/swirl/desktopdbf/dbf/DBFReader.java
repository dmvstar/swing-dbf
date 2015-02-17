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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dstarzhynskyi
 */
public class DBFReader extends DBFFile {

    private java.io.FileInputStream fis;

    public DBFReader(File dbfFile) throws FileNotFoundException {
        super(dbfFile);
        try {
            fis = new FileInputStream(dbfFile);

            byte[] buff = new byte[32];
            int count = fis.read(buff);

            dbfHeader = new DBFHeader(buff);
            dbfFields = new DBFField[dbfHeader.getFieldsCount() + 1];

            fillDbfFlds();

            count = fis.available();
            byte[] data = new byte[count];
            count = fis.read(data);

            dbfRawData = new DBFRawData(data, this);
            /*
            dbfHdr = dbfRawData.getDBFHeader();
            dbfFlds = dbfRawData.getDBFFields();
             */

        } catch (IOException ex) {
            Logger.getLogger(DBFFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Fill DBF Field info
     * @throws IOException
     */
    private void fillDbfFlds() throws IOException {
        getDbfFields()[0] = new DBFField();
        for (int i = 1; i < getDbfHeader().getFieldsCount() + 1; i++) {
            byte finfo[] = new byte[32];
            fis.read(finfo);
            getDbfFields()[i] = new DBFField(finfo);
            dbfFields[i].locate = getDbfFields()[i - 1].locate + getDbfFields()[i - 1].getFieldLength();
//System.out.println(getDbfFields()[i]);
        }
        int skipCount = getDbfHeader().getHeaderLength() - (DBFHeader.getLen() + DBFField.getLen() * getDbfHeader().getFieldsCount());
        fis.skip(skipCount);
    }
}

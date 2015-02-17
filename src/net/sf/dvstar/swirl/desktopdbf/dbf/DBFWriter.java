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
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 *
 * @author dstarzhynskyi
 */
public class DBFWriter extends DBFFile {

    RandomAccessFile randomFile = null; /* Open and append records to an existing DBF */

    private int recordCount;

//    private final FileOutputStream foStream;
    public DBFWriter(File dbfFile) throws FileNotFoundException {
        super(dbfFile);
        randomFile = new RandomAccessFile(dbfFile, "rw");
//        foStream = new FileOutputStream( dbfFile );
    }

    /**
    Writes the set data to the OutputStream.
     */
    public void write(OutputStream out)
            throws DBFException {


        try {

            this.getDbfHeader().setRecordCount(this.recordCount);
            this.randomFile.seek(0);
            this.getDbfHeader().write(this.randomFile);
            for (int i = 0; i < getDbfRecords().size(); i++) { /* iterate through records */
                this.getDbfRecords().get(i).writeRecord( this.randomFile );
            }
            this.randomFile.seek(randomFile.length());
            this.randomFile.writeByte(END_OF_DATA);
            this.randomFile.close();

        } catch (IOException e) {
            throw new DBFException(e.getMessage());
        }
    }

    public void write()
            throws DBFException {
        try {
            this.getDbfHeader().write(this.randomFile);

            for (int i = 0; i < getDbfRecords().size(); i++) { /* iterate through records */
                this.getDbfRecords().get(i).writeRecord( this.randomFile );
            }
            
            this.randomFile.seek(randomFile.length());
            this.randomFile.writeByte(END_OF_DATA);
            this.randomFile.close();
            
        } catch (IOException e) {
            throw new DBFException(e.getMessage());
        }


    }
}

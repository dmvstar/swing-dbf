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
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author sdv
 */
public class DBFRecord extends DBFBuffer {

    /**
     * DBF Header for file
     */
    private DBFHeader header = null;
    /**
     * Binary data array for record
     */
    private Object objectArray[] = null;
    /**
     * Record offset in file
     */
    private long   recordOffset = -1;
    /**
     * Charset for data
     */
    private final Charset charset;
    /**
     * Parameters for data manipulating
     */
    private final DBFDataModificator dataModificator;

    /**
    Add a record.
     */
    public DBFRecord(DBFHeader header, Charset charset, DBFDataModificator dataModificator) {
        this.header = header;
        this.charset = charset;
        this.dataModificator = dataModificator;
    }

    public void makeRecord(List values, boolean forceCast) throws DBFException {
        makeRecord(values.toArray(), forceCast);
    }

    public void makeRecord(Object[] values, boolean forceCastCheck)
            throws DBFException {

        if (this.header.getDbfFields() == null) {

            throw new DBFException("Fields should be set before adding records");
        }

        if (values == null) {

            throw new DBFException("Null cannot be added as row");
        }

        if (values.length != this.header.getDbfFields().length) {

            throw new DBFException("Invalid record. Invalid number of fields in row v[" + values.length + "] h[" + this.header.getDbfFields().length + "]");
        }

        for (int i = 0; i < this.header.getDbfFields().length; i++) {

            if (values[i] == null) {

                continue;
            }

            if (forceCastCheck) {
                switch (this.header.getDbfFields()[i].getDataType()) {
                    case 'C':
                        if (!(values[i] instanceof String)) {
                            throw new DBFException("Invalid value for field " + i);
                        }
                        break;

                    case 'L':
                        if (!(values[i] instanceof Boolean)) {
                            throw new DBFException("Invalid value for field " + i);
                        }
                        break;

                    case 'N':
                        if (!(values[i] instanceof Double)) {
                            throw new DBFException("Invalid value for field " + i);
                        }
                        break;

                    case 'D':
                        if (!(values[i] instanceof Date)) {
                            throw new DBFException("Invalid value for field " + i);
                        }
                        break;

                    case 'F':
                        if (!(values[i] instanceof Double)) {

                            throw new DBFException("Invalid value for field " + i);
                        }
                        break;
                }
            } else {
                try {
                switch (this.header.getDbfFields()[i].getDataType()) {
                    case 'L':
                        values[i] = Boolean.parseBoolean(values[i].toString());
                        break;

                    case 'N':
                        if (values[i].toString().length() == 0) {
                            values[i] = Double.parseDouble("0");
                        } else {
                            values[i] = Double.parseDouble( values[i].toString().replace(',', '.') );
                        }
                        break;

                    case 'D':
                        if (values[i].toString().length() == 0) {
                           values[i] = null;
                        } else
                           values[i] = new Date();
                        break;

                    case 'F':
                        if (values[i].toString().length() == 0) {
                            values[i] = Double.parseDouble("0");
                        } else {
                            values[i] = Double.parseDouble(values[i].toString().replace(',', '.'));
                        }
                        break;

                }
                } catch (Exception e) {
                    throw new DBFException( "Error making DBF record for field ["+this.header.getDbfFields()[i].getFieldName()+"] with value ["+values[i].toString()+"]", e);
                }
            }

            objectArray = values;
        }
    }

    public void writeRecord(DataOutput dataOutput)
            throws IOException {

//System.out.print("["+objectArray.length+"]["+this.header.getRecordLength()+"]-");
        dataOutput.write((byte) ' '); // delete flag
        for (int j = 0; j < this.header.getDbfFields().length; j++) { /* iterate throught fields */
//System.out.print("["+objectArray[j]+"]");
            switch (this.header.getDbfFields()[j].getDataType()) {

                case 'C':
                    if (objectArray[j] != null) {

                        String str_value = objectArray[j].toString();
                        if(dataModificator.getCharRepacerList() != null)
                            str_value = replaceChars( str_value );
                        dataOutput.write(DBFBuffer.textPadding(str_value, charset, this.header.getDbfFields()[j].getFieldLength()));
                    } else {

                        dataOutput.write(DBFBuffer.textPadding("", charset, this.header.getDbfFields()[j].getFieldLength()));
                    }

                    break;

                case 'D':
                    if (objectArray[j] != null && objectArray[j].toString().length()>0) {

                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.setTime((Date) objectArray[j]);
                        StringBuffer t_sb = new StringBuffer();
                        dataOutput.write(String.valueOf(calendar.get(Calendar.YEAR)).getBytes());
                        dataOutput.write(DBFBuffer.textPadding(String.valueOf(calendar.get(Calendar.MONTH) + 1), this.getCharset(), 2, DBFBuffer.ALIGN_RIGHT, (byte) '0'));
                        dataOutput.write(DBFBuffer.textPadding(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), this.getCharset(), 2, DBFBuffer.ALIGN_RIGHT, (byte) '0'));
                    } else {

                        dataOutput.write("        ".getBytes());
                    }

                    break;

                case 'F':

                    if (objectArray[j] != null) {

                        dataOutput.write(DBFBuffer.doubleFormating((Double) objectArray[j], this.getCharset(), this.header.getDbfFields()[j].getFieldLength(), this.header.getDbfFields()[j].getDecimalCount()));
                    } else {

                        dataOutput.write(DBFBuffer.textPadding("?", this.getCharset(), this.header.getDbfFields()[j].getFieldLength(), DBFBuffer.ALIGN_RIGHT));
                    }

                    break;

                case 'N':

                    if (objectArray[j] != null) {

                        dataOutput.write(
                                DBFBuffer.doubleFormating((Double) objectArray[j], this.getCharset(), this.header.getDbfFields()[j].getFieldLength(), this.header.getDbfFields()[j].getDecimalCount()));
                    } else {

                        dataOutput.write(
                                DBFBuffer.textPadding("?", this.getCharset(), this.header.getDbfFields()[j].getFieldLength(), DBFBuffer.ALIGN_RIGHT));
                    }

                    break;
                case 'L':

                    if (objectArray[j] != null) {

                        if ((Boolean) objectArray[j] == Boolean.TRUE) {

                            dataOutput.write((byte) 'T');
                        } else {

                            dataOutput.write((byte) 'F');
                        }
                    } else {

                        dataOutput.write((byte) '?');
                    }

                    break;

                case 'M':

                    break;

                default:
                    throw new DBFException("Unknown field type " + this.header.getDbfFields()[j].getDataType());
            }
        }	/* iterating through the fields */
// System.out.println();
    }

    private Charset getCharset() {
        return charset;
    }

    private String replaceChars(String origStr) {
        String ret = origStr;
        if(dataModificator.getCharRepacerList() != null ){
            for(int i=0;i<dataModificator.getCharRepacerList().size();i++) {
                ret = ret.replace(
                        dataModificator.getCharRepacerList().get(i).getChFr(),
                        dataModificator.getCharRepacerList().get(i).getChTo());
            }
        }
        return ret;
    }
}

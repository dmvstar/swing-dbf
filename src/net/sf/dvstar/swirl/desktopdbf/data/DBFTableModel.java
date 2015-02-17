/*
 * Application for displaing and manipulating of DBF and XLS files
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

package net.sf.dvstar.swirl.desktopdbf.data;

import java.io.FileNotFoundException;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFField;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFFile;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFHeader;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFRawData;

/**
 *
 * @author sdv
 */
public class DBFTableModel extends AbstractTableModel implements TableModel {

    private DBFHeader dbfHdr;
    protected DBFField[] dbfFlds;
    private DBFRawData dbfData = null;
    DBFFile dbfFileData;

    public DBFTableModel(DBFFile dbfFileData) throws FileNotFoundException {

        this.dbfFileData = dbfFileData;

        dbfHdr = dbfFileData.getDBFHeader();
        dbfFlds = dbfFileData.getDBFFields();// new DBFField[dbfHdr.nfld + 1];
        dbfData = dbfFileData.getDBFRawData();// new DBFRawData(data, loader);

    }

    @Override
    public int getColumnCount() {
        int ret = dbfHdr.getFieldsCount() + 1 + 1;

        return (ret);
    }

    @Override
    public int getRowCount() {
        int ret = (int) dbfHdr.getRecordCount();
        return (ret);
    }

    @Override
    public String getColumnName(int columnIndex) {
        String ret = "#";// №Column [" + columnIndex + "]";
        int col = columnIndex - 1;
        if (columnIndex > 0 && col < dbfHdr.getFieldsCount() + 1 && dbfFlds[col] != null) {
            ret = dbfFlds[col].getFieldName();
        }
        return (ret);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Class<?> ret = String.class;

        if (columnIndex > 0) {
            if (dbfFlds[columnIndex - 1].getDataType() == 'N') {
                ret = Integer.class;
            }
            //if(dbfFlds[columnIndex-1].type=='N') ret = Number.class;
        }
        return ret;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        boolean ret = false;

        if (columnIndex > 0) {
            ret = true;
        }
        return (ret);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        int col = columnIndex - 1;
        Object ret = rowIndex + 1;

        if (columnIndex > 0) {
            ret = dbfFileData.getValueAt(rowIndex, columnIndex);
            if (ret == null) {
                ret = dbfData.getValueAt(rowIndex, col);
            }
            if(col>0) {
//System.out.println(dbfFlds[col]+"-["+ret+"]");
                if(dbfFlds[col].getDataType() == DBFField.TYPE_CHARACTER ){
                    //ret = dbfFlds[col].formatDataStr(ret);
                    ret = ((String)ret).trim();
                }
            }
        }

        return (ret);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        int col = columnIndex;
        Object ret = getValueAt(rowIndex, col);
//System.out.println("["+ret.toString()+"]->["+aValue.toString()+"]");
        if( !ret.toString().equals(aValue.toString()) ) {
            dbfFileData.setValueAt(aValue, rowIndex, columnIndex);
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
    }
}

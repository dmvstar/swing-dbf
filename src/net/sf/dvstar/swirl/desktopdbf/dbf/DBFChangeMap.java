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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author dstarzhynskyi
 */
public class DBFChangeMap {

    private Map<ChangeIndex, Object> changeMap = new HashMap();

    public void addChangeIndex(ChangeIndex index, Object value) {
        changeMap.put(index, value);
    }

    public void addChangeIndex(int rowIndex, int columnIndex, Object aValue) {
        changeMap.put(new ChangeIndex(rowIndex, columnIndex), aValue);
    }

    public boolean isCellChanged(int row, int col) {
        return isCellChanged( new ChangeIndex(row, col));
    }

    public boolean isCellChanged(ChangeIndex index) {
        boolean ret = false;
        if(changeMap.get(index) != null) {
            ret = true;
        }
        return ret;
    }

    public Object getValueAt(int rowIndex, int columnIndex){
        return changeMap.get(new ChangeIndex(rowIndex, columnIndex));
    }


    public static class ChangeIndex {

        int row;
        int col;

        ChangeIndex(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 71 * hash + this.row;
            hash = 71 * hash + this.col;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ChangeIndex other = (ChangeIndex) obj;
            if (this.row != other.row) {
                return false;
            }
            if (this.col != other.col) {
                return false;
            }
            return true;
        }

        @Override
        public String toString(){
            return "[" + row + "][" + col + "]";
        }

    }

}

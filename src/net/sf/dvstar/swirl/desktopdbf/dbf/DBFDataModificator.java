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

package net.sf.dvstar.swirl.desktopdbf.dbf;

import java.util.ArrayList;

/**
 *
 * @author sdv
 */
public class DBFDataModificator {
    private ArrayList<CharRepacer> charRepacerList = null;

    public void makeCharRepacer(String strFr, String strTo){
        if(strFr.length()== strTo.length()) {
            charRepacerList = new ArrayList<CharRepacer>();
            for(int i=0;i<strFr.length();i++) {
                CharRepacer crpl = new CharRepacer(strFr.charAt(i), strTo.charAt(i));
                getCharRepacerList().add(crpl);
            }
        }
    }

    /**
     * @return the charRepacerList
     */
    public ArrayList<CharRepacer> getCharRepacerList() {
        return charRepacerList;
    }

    public static class CharRepacer {
        private char chFr;
        private char chTo;

        public CharRepacer(char chFr, char chTo){
            this.chFr = chFr;
            this.chTo = chTo;
        }

        /**
         * @return the chFr
         */
        public char getChFr() {
            return chFr;
        }
        public char getChTo() {
            return chTo;
        }

    }
}

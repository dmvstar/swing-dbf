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

import java.nio.charset.Charset;

/**
 *
 * @author dstarzhynskyi
 */
public interface DBFFileInterface {

    public DBFHeader    getDBFHeader();
    public DBFField[]   getDBFFields();
    public DBFChangeMap getDBFChangeMap();
    public DBFRawData   getDBFRawData();

    public boolean      isFileLoaded();
    public Charset      getCharsetView();
    public void         setCharsetView(String charset);
    public void         setCharsetView(Charset charset);

}

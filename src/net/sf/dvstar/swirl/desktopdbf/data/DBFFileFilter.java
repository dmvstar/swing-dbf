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

import java.io.File;
import javax.swing.filechooser.*;

public class DBFFileFilter extends FileFilter {

    public static String EXT_DBF = "dbf";
    public static String EXT_XLS = "xls";

    // Accept all directories and all gif, jpg, or tiff files.
    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals(EXT_DBF) || extension.equals(EXT_XLS)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static String changeExtension(File f, String newExtension) {
        return changeExtension(f, newExtension, null);
    }

    public static String changeExtension(File f, String newExtension, String prefix) {
        String ret = null;
        String ext = null;
        String s = f.getName();
        String par = f.getParent();
/*
System.out.println("getName "+f.getName());
System.out.println("getPath "+f.getPath());
System.out.println("getParent "+f.getParent());
System.out.println("getAbsolutePath "+f.getAbsolutePath());
*/
        String aprefix = "";
        int i = s.lastIndexOf('.');

        if(prefix != null){
            aprefix = prefix;
        }

        if (i > 0 && i < s.length() - 1) {
            if( newExtension.indexOf('.')>=0) {
                ext = s.substring(0, i) + aprefix + newExtension;
            }
            else {
                ext = s.substring(0, i) + aprefix + "." + newExtension;
            }
        }
        if(par !=null) {
            ret = par + File.separator+ext;
        } else {
            ret = ext;
        }
        return ret;
    }

    @Override
    public String getDescription() {
        return "DBF XLS files";
    }
}

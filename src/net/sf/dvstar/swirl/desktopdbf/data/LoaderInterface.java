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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 *
 * @author dstarzhynskyi
 */
public interface LoaderInterface {

    public static final int FYLE_TYPE_DBF = 1;
    public static final int FYLE_TYPE_XLS = 2;
    public static final int FYLE_TYPE_ODS = 3;


    public void     createDataPanel() throws FileNotFoundException, IOException;
    public JPanel   getDataPanel();
    public JTable   getDataTable();
    public boolean  isFileLoaded();
    public File     getFile();
    public int      getFileType();

    public String   getFileExt();
    public String   getCharsetViewDesc();
    public String   getCharsetEncodeDesc();
    public Charset  getCharsetView();
    public Charset  getCharsetEncode();
    public void     setCharsetView(Charset charset);
    public void     setCharsetEncode(Charset charset);
    public void     setCharsetViewDesc(String charset);
    public void     setCharsetEncodeDesc(String charset);

    public JFrame   getMainFrame();
}

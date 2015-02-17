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

import java.nio.charset.Charset;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 *
 * @author dstarzhynskyi
 */
public abstract class DataLoader implements LoaderInterface{

    protected JPanel dataPanel = null;
    protected JTable dataTable = null;

    protected String charsetEncodeDesc;
    protected String charsetViewDesc;
    protected Charset charsetEncode;
    protected Charset charsetView;
    protected String fileExt = "---";

    @Override
    public String getCharsetViewDesc() {
        return charsetViewDesc;
    }

    @Override
    public String getCharsetEncodeDesc() {
        return charsetEncodeDesc;
    }

    @Override
    public Charset getCharsetView() {
        return charsetView;
    }

    @Override
    public Charset getCharsetEncode() {
        return charsetEncode;
    }

    @Override
    public void setCharsetView(Charset charset) {
        this.charsetView = charset;
        this.setCharsetViewDesc(charset.displayName());
    }

    @Override
    public void setCharsetEncode(Charset charset) {
        this.charsetEncode = charset;
        this.setCharsetEncodeDesc(charset.displayName());
    }

    @Override
    public void setCharsetViewDesc(String charset) {
        this.charsetViewDesc = charset;
    }

    @Override
    public void setCharsetEncodeDesc(String charset) {
        this.charsetEncodeDesc = charset;
    }


}

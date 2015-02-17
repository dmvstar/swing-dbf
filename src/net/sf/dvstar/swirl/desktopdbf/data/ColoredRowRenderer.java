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

//d7e9d7 - светло зеленый
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Rectangle;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFChangeMap;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFField;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFFileInterface;

//d8d8d9 - светло серый
//
/**
 *
 * @author dstarzhynskyi
 */
public class ColoredRowRenderer extends DefaultTableCellRenderer {

    DateFormat dateFormatter = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
    NumberFormat numberFormatter = NumberFormat.getInstance();
    Color colorOdd = new Color(0xe1e1e1);
    DBFFileInterface dbfFile = null;
    private boolean coloredStruct = false;

    public ColoredRowRenderer(DBFFileInterface dbfFile) {
        this(dbfFile, false);
    }

    public ColoredRowRenderer(DBFFileInterface dbfFile, boolean coloredStruct) {
        this.dbfFile = dbfFile;
        this.coloredStruct = coloredStruct;
    }

    public ColoredRowRenderer() {
    }

    public static Color RGBtoHSV(int r, int g, int b){
        Color ret = Color.getHSBColor(r, g, b);
        float[] hsbvals = new float[3];
        hsbvals = Color.RGBtoHSB(r, g, b, hsbvals);
        ret = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]);
        return ret;
    }

    public static Rectangle getColumnBounds(JTable table, int column) {
        //checkColumn(table, column);

        Rectangle result = table.getCellRect(-1, column, true);
        Insets i = table.getInsets();

        result.y = i.top;
        result.height = table.getVisibleRect().height;//   table.getHeight() - i.top - i.bottom;

        return result;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        //  Code for data formatting
        Border selected = new LineBorder(Color.RED);

        Color currentColor = getForeground();

        setHorizontalAlignment(SwingConstants.LEFT);

        /*
        if (value instanceof Date) {
        setText(dateFormatter.format((Date) value));
        }

        if (value instanceof Number) {
        setHorizontalAlignment(SwingConstants.RIGHT);

        if (value instanceof Double) {
        setText(numberFormatter.format(((Number) value).floatValue()));
        }
        }
         */
        //  Code for highlighting

        if (!isSelected) {
            //    String type = (String) table.getModel().getValueAt(row, 0);
            setBackground(row % 2 == 0 ? null : colorOdd);
        }

        if (dbfFile != null) {

            DBFChangeMap changeMap = dbfFile.getDBFChangeMap();
            DBFField[] fields = dbfFile.getDBFFields();

            if (coloredStruct) {
                if (row <= fields.length) {
                    DBFField field = fields[row+1];
                    switch (field.getDataType()) {
                        case DBFField.TYPE_CHARACTER: {
                            setBackground(Color.lightGray);
                        }
                        break;
                        case DBFField.TYPE_DATE: {
                            setBackground(Color.gray);
                        }
                        break;
                        case DBFField.TYPE_NUMERIC: {
                            setBackground( Color.getHSBColor(255,252,193) );
                        }
                        break;
                        case DBFField.TYPE_FLOAT: {
                            setBackground(Color.cyan);
                        }
                        break;

                    }
                }
            }

            if (changeMap.isCellChanged(row, column)) {
                setForeground(Color.red);
            } else {
                setForeground(Color.black);
            }
        }

        /*
        if(table.isColumnSelected(column)){
        setBackground(colorOdd);
        //System.out.println("----getColumnBounds(table, column)"+getColumnBounds(table, column));
        table.repaint(   getColumnBounds(table, column)   );
        }
         */

        if (table.isRowSelected(row) && table.isColumnSelected(column)) {
            setBorder(selected);
        }

        return this;
    }
}

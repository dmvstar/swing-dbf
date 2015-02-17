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

import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

/**
 *
 * @author dstarzhynskyi
 */
public class ExcelTableModel extends AbstractTableModel implements TableModel {

    private Workbook workbook = null;

    private ArrayList<ArrayList> bookData = null;
    private ArrayList<ArrayList> sheetData = null;
    private ArrayList<Integer> maxRowWidths = null;
    private ArrayList<Integer> maxColumnWidths = null;
    private int maxRowWidth = 0;

    private int formattingConvention = 0;
    private DataFormatter formatter = null;
    private FormulaEvaluator evaluator = null;
    private int CHAR_W0 = 8;

    public ExcelTableModel(Workbook workbook) {
        this.workbook = workbook;
        this.evaluator = this.workbook.getCreationHelper().createFormulaEvaluator();
        this.formatter = new DataFormatter();

        convertToCSV();
    }

    @Override
    public int getRowCount() {
        int ret = 0;
        if (getBookData() != null && getBookData().size() > 0) {
            ret = getBookData().get(0).size();
        }
        return ret;
    }

    @Override
    public int getColumnCount() {
        return maxRowWidths.get(0).intValue();
    }
    private static char[] alphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    @Override
    public String getColumnName(int columnIndex) {
        String ret = "";
        if (columnIndex < alphabet.length) {
            ret = ret + alphabet[columnIndex];
        } else {
            int first = columnIndex % alphabet.length;
            int next = columnIndex / alphabet.length;
            ret = ret + alphabet[next - 1] + alphabet[first];
        }
        return ret+" ("+(columnIndex+1)+")";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object ret = "";
        //if (columnIndex == 0) {
        //    ret = ""+(rowIndex+1);
        //} else
        {
            int realCol = columnIndex;// - 1;
            if (getBookData() != null && getBookData().size() > 0) {
                sheetData = getBookData().get(0);

                if (sheetData.size() > rowIndex) {
                    ArrayList<String> rowData = sheetData.get(rowIndex);
                    if (rowData.size() > realCol) {
                        ret = rowData.get(realCol);
                    }
                }

            }
        }
        return ret;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
    }

    /**
     * Called to convert the contents of the currently opened workbook into
     * a CSV file.
     */
    private void convertToCSV() {
        Sheet sheet = null;
        Row row = null;
        int lastRowNum = 0;
        ArrayList<ArrayList> csvSheetData = null;
        this.bookData = new ArrayList<ArrayList>();
        maxRowWidths = new ArrayList<Integer>();
        this.maxColumnWidths = new ArrayList<Integer>();

//        System.out.println("Converting files contents to CSV format.");

        // Discover how many sheets there are in the workbook....
        int numSheets = this.workbook.getNumberOfSheets();

        // and then iterate through them.
        for (int i = 0; i < 1 /*numSheets*/; i++) { // !!!! Only ONE Sheet !!!!

            maxRowWidth = 0;
            // Get a reference to a sheet and check to see if it contains
            // any rows.
            sheet = this.workbook.getSheetAt(i);
            if (sheet.getPhysicalNumberOfRows() > 0) {

                // Note down the index number of the bottom-most row and
                // then iterate through all of the rows on the sheet starting
                // from the very first row - number 1 - even if it is missing.
                // Recover a reference to the row and then call another method
                // which will strip the data from the cells and build lines
                // for inclusion in the resylting CSV file.
                lastRowNum = sheet.getLastRowNum();
                csvSheetData = new ArrayList<ArrayList>();
                for (int j = 0; j <= lastRowNum; j++) {
                    row = sheet.getRow(j);
                    csvSheetData.add(this.rowToCSV(row));
                }
                this.getBookData().add(csvSheetData);
                this.maxRowWidths.add(new Integer(getMaxRowWidth()));
            }
        }
    }

    /**
     * Called to convert a row of cells into a line of data that can later be
     * output to the CSV file.
     *
     * @param row An instance of either the HSSFRow or XSSFRow classes that
     *            encapsulates information about a row of cells recovered from
     *            an Excel workbook.
     */
    private ArrayList rowToCSV(Row row) {
        Cell cell = null;
        int lastCellNum = 0;
        ArrayList<String> csvLine = new ArrayList<String>();

        // Check to ensure that a row was recovered from the sheet as it is
        // possible that one or more rows between other populated rows could be
        // missing - blank. If the row does contain cells then...
        if (row != null) {

            // Get the index for the right most cell on the row and then
            // step along the row from left to right recovering the contents
            // of each cell, converting that into a formatted String and
            // then storing the String into the csvLine ArrayList.
            lastCellNum = row.getLastCellNum();
            for (int i = 0; i < lastCellNum; i++) {
                cell = row.getCell(i);
                if (cell == null) {
                    csvLine.add("");
                } else {
                    if (cell.getCellType() != Cell.CELL_TYPE_FORMULA) {
                        String cellValue = this.formatter.formatCellValue(cell);
                        if(i+1>getMaxColumnWidths().size()) {
                            getMaxColumnWidths().add(new Integer(0));
                        }
//                        int newW = Math.max(cellValue.length() * CHAR_W0, curW * CHAR_W0);
                        int curW = getMaxColumnWidths().get(i);
                        int newW = Math.max(cellValue.length(), curW);
                        getMaxColumnWidths().set(i, newW);
                        csvLine.add(cellValue);
                    } else {
                        csvLine.add(this.formatter.formatCellValue(cell, this.evaluator));
                    }
                }
            }
            // Make a note of the index number of the right most cell. This value
            // will later be used to ensure that the matrix of data in the CSV file
            // is square.
            if (lastCellNum > this.getMaxRowWidth()) {
                this.maxRowWidth = lastCellNum;
            }
        }
        //this.csvData.add(csvLine);
        return csvLine;
    }

    public void setColumnWidths(JTable dataTable) {
//System.out.println(getMaxColumnWidths().size()+" "+getMaxColumnWidths());
//System.out.println(dataTable.getColumnModel().getColumnCount());
            for(int j=0; j<getMaxColumnWidths().size();j++) {
                dataTable.getColumnModel().getColumn(j).setPreferredWidth(getMaxColumnWidths().get(j)*CHAR_W0);
            }
    }

    /**
     * @return the bookData
     */
    public ArrayList<ArrayList> getBookData() {
        return bookData;
    }

    /**
     * @return the maxRowWidth
     */
    public int getMaxRowWidth() {
        return maxRowWidth;
    }

    /**
     * @return the maxColumnWidths
     */
    public ArrayList<Integer> getMaxColumnWidths() {
        return maxColumnWidths;
    }
}

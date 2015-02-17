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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JTable;

import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import net.sf.dvstar.swirl.desktopdbf.DesktopDBFView;
import net.sf.dvstar.swirl.desktopdbf.ExelExportDialog.ExportParamsResult;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFDataModificator;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFException;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFField;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFHeader;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFRecord;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFWriter;

import sun.nio.cs.MS1251;
import ua.nio.cs.ext.CP1125;

/**
 *
 * @author dstarzhynskyi
 */
public class XLSPanelLoader extends DataLoader implements LoaderInterface {

    private DesktopDBFView parent;
    private File fileXLS = null;
    private boolean isFileLoaded = false;
    private Workbook workbook = null;
    private ArrayList<ArrayList> csvData = null;
    private int maxRowWidth = 0;
    private int formattingConvention = 0;
    private DataFormatter formatter = null;
    private FormulaEvaluator evaluator = null;
    private String separator = DEFAULT_SEPARATOR;
    private static final String CSV_FILE_EXTENSION = ".csv";
    private static final String DEFAULT_SEPARATOR = "|";
    /**
     * Identifies that the CSV file should obey Excel's formatting conventions
     * with regard to escaping certain embedded characters - the field separator,
     * speech mark and end of line (EOL) character
     */
    public static final int EXCEL_STYLE_ESCAPING = 0;
    /**
     * Identifies that the CSV file should obey UNIX formatting conventions
     * with regard to escaping certain embedded characters - the field separator
     * and end of line (EOL) character
     */
    public static final int UNIX_STYLE_ESCAPING = 1;
    private int CHAR_W0 = 10;
    private Charset charset;
    private String prefixFile;
    private ExcelTableModel exelTableModel;
    private DBFHeader dbfHeader;
    private String outputFileName;
    private JList rowHeader = null;

    public XLSPanelLoader(DesktopDBFView parent, File fileToOpen) {
        this.fileXLS = fileToOpen;
        this.parent = parent;
        this.fileExt = "XLS";

        try {
            createDataPanel();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(XLSPanelLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XLSPanelLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Open an Excel workbook ready for conversion.
     *
     * @param file An instance of the File class that encapsulates a handle
     *        to a valid Excel workbook. Note that the workbook can be in
     *        either binary (.xls) or SpreadsheetML (.xlsx) format.
     * @throws java.io.FileNotFoundException Thrown if the file cannot be located.
     * @throws java.io.IOException Thrown if a problem occurs in the file system.
     * @throws org.apache.poi.openxml4j.exceptions.InvalidFormatException Thrown
     *         if invalid xml is found whilst parsing an input SpreadsheetML
     *         file.
     */
    private void openWorkbook(File file) throws FileNotFoundException,
            IOException, InvalidFormatException {
        FileInputStream fis = null;
        try {
            System.out.println("Opening workbook [" + file.getName() + "]");

            fis = new FileInputStream(file);

            // Open the workbook and then create the FormulaEvaluator and
            // DataFormatter instances that will be needed to, respectively,
            // force evaluation of forumlae found in cells and create a
            // formatted String encapsulating the cells contents.
            this.workbook = WorkbookFactory.create(fis);
            this.evaluator = this.workbook.getCreationHelper().createFormulaEvaluator();
            this.formatter = new DataFormatter();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
    }

    @Override
    public void createDataPanel() throws FileNotFoundException, IOException {
        if (fileXLS.exists()) {
            try {

                // Open the workbook
                this.openWorkbook(fileXLS);


                if (this.workbook != null) {

                    ListModel lm = new AbstractListModel() {

                        String headers[] = {"1", "2", "c", "d", "e", "f", "g", "h", "i"};

                        @Override
                        public int getSize() {
                            return workbook.getSheetAt(0).getPhysicalNumberOfRows();
                        }

                        @Override
                        public Object getElementAt(int index) {
                            return "" + (index + 1);
                        }
                    };

                    dataPanel = new JPanel();

                    dataPanel.setLayout(new BorderLayout());

                    exelTableModel = new ExcelTableModel(this.workbook);

                    dataTable = new JTable(exelTableModel);
                    exelTableModel.setColumnWidths(dataTable);
                    this.csvData = exelTableModel.getBookData();
                    this.maxRowWidth = exelTableModel.getMaxRowWidth();

                    /*
                    rowHeader = new JList(lm);
                    rowHeader.setFixedCellWidth(50);
                    rowHeader.setFixedCellHeight(dataTable.getRowHeight()
                    + dataTable.getRowMargin() - 1);
                    //                           + table.getIntercellSpacing().height);
                    rowHeader.setCellRenderer(new RowHeaderRenderer(dataTable) );
                     */
                    rowHeader = new JListRowHeader(lm, dataTable, 50, new RowHeaderRenderer(dataTable));

                    dataTable.addMouseListener(new MousePopupListener());
                    dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    JScrollPane scroll = new JScrollPane(getDataTable());
                    scroll.setRowHeaderView(rowHeader);
                    scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
                    dataPanel.add(scroll);



                    TableCellRenderer custom = new ColoredRowRenderer();
                    getDataTable().setDefaultRenderer(Object.class, custom);
                    getDataTable().setDefaultRenderer(Number.class, custom);

                }

            } catch (InvalidFormatException ex) {
                Logger.getLogger(XLSPanelLoader.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * Called to actually save the data recovered from the Excel workbook
     * as a CSV file.
     *
     * @param file An instance of the File class that encapsulates a handle
     *             referring to the CSV file.
     * @throws java.io.FileNotFoundException Thrown if the file cannot be found.
     * @throws java.io.IOException Thrown to indicate and error occurred in the
     *                             underylying file system.
     */
    public void printCSVFile()
            throws FileNotFoundException, IOException {
        ArrayList<String> line = null;
        ArrayList<ArrayList> sheet = null;
        StringBuffer buffer = null;
        String csvLineElement = null;


        System.out.println("Print the CSV data");

        // Step through the elements of the ArrayList that was used to hold
        // all of the data recovered from the Excel workbooks' sheets, rows
        // and cells.
        for (int i = 0; i < this.csvData.size(); i++) {
            buffer = new StringBuffer();
            System.out.println(this.csvData.size());

            // Get an element from the ArrayList that contains the data for
            // the workbook. This element will itself be an ArrayList
            // containing Strings and each String will hold the data recovered
            // from a single cell. The for() loop is used to recover elements
            // from this 'row' ArrayList one at a time and to write the Strings
            // away to a StringBuffer thus assembling a single line for inclusion
            // in the CSV file. If a row was empty or if it was short, then
            // the ArrayList that contains it's data will also be shorter than
            // some of the others. Therefore, it is necessary to check within
            // the for loop to ensure that the ArrayList contains data to be
            // processed. If it does, then an element will be recovered and
            // appended to the StringBuffer.
            sheet = this.csvData.get(i);
            for (int k = 0; k < sheet.size(); k++) {
                line = sheet.get(k);
//System.out.println(line);
                for (int j = 0; j < this.maxRowWidth; j++) {
                    if (line.size() > j) {
                        csvLineElement = line.get(j);
                        if (csvLineElement != null) {
                            buffer.append(this.escapeEmbeddedCharacters(
                                    csvLineElement));
                        }
                    }
                    if (j < (this.maxRowWidth - 1)) {
                        buffer.append(this.separator);
                    }
                }
                buffer.append(System.getProperty("line.separator", "\n"));
                // Once the line is built, write it away to the CSV file.
                //bw.write(buffer.toString().trim());
            }
            System.out.println(buffer.toString().trim());

            // Condition the inclusion of new line characters so as to
            // avoid an additional, superfluous, new line at the end of
            // the file.
            //if(i < (this.csvData.size() - 1)) {
            //    bw.newLine();
            //}
        }

    }

    /**
     * Called to actually save the data recovered from the Excel workbook
     * as a CSV file.
     *
     * @param file An instance of the File class that encapsulates a handle
     *             referring to the CSV file.
     * @throws java.io.FileNotFoundException Thrown if the file cannot be found.
     * @throws java.io.IOException Thrown to indicate and error occurred in the
     *                             underylying file system.
     */
    public void saveCSVFile(File file) throws FileNotFoundException, IOException {
        saveCSVFile(file, null);
    }

    public void saveCSVFile(File file, Charset charset)
            throws FileNotFoundException, IOException {
        //FileWriter fw = null;
        //BufferedWriter bw = null;
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;

        ArrayList<ArrayList> sheet = null;
        ArrayList<String> line = null;
        StringBuffer buffer = null;
        String csvLineElement = null;
        try {


            System.out.println("Saving the CSV file [" + file.getName() + "]");

            // Open a writer onto the CSV file.
            fos = new FileOutputStream(file);
            if (charset != null) {
                osw = new OutputStreamWriter(fos, charset);
            } else {
                osw = new OutputStreamWriter(fos);
            }

//            fw = new FileWriter(file);
//            bw = new BufferedWriter(fw);

            // Step through the elements of the ArrayList that was used to hold
            // all of the data recovered from the Excel workbooks' sheets, rows
            // and cells.
            for (int i = 0; i < this.csvData.size(); i++) {
                buffer = new StringBuffer();

                // Get an element from the ArrayList that contains the data for
                // the workbook. This element will itself be an ArrayList
                // containing Strings and each String will hold the data recovered
                // from a single cell. The for() loop is used to recover elements
                // from this 'row' ArrayList one at a time and to write the Strings
                // away to a StringBuffer thus assembling a single line for inclusion
                // in the CSV file. If a row was empty or if it was short, then
                // the ArrayList that contains it's data will also be shorter than
                // some of the others. Therefore, it is necessary to check within
                // the for loop to ensure that the ArrayList contains data to be
                // processed. If it does, then an element will be recovered and
                // appended to the StringBuffer.
                sheet = this.csvData.get(i);
                for (int k = 0; k < sheet.size(); k++) {
                    line = sheet.get(k);
                    for (int j = 0; j < this.maxRowWidth; j++) {
                        if (line.size() > j) {
                            csvLineElement = line.get(j);
                            if (csvLineElement != null) {
                                buffer.append(this.escapeEmbeddedCharacters(
                                        csvLineElement));
                            }
                        }
                        if (j < (this.maxRowWidth - 1)) {
                            buffer.append(this.separator);
                        }
                    }
                    buffer.append(System.getProperty("line.separator", "\n"));
                    //osw.write(System.getProperty("line.separator", "\n"));

                    // Once the line is built, write it away to the CSV file.
                    //bw.write(buffer.toString().trim());

                    // Condition the inclusion of new line characters so as to
                    // avoid an additional, superfluous, new line at the end of
                    // the file.
                    if (i < (this.csvData.size() - 1)) {
                        //bw.newLine();
                        //osw.write(System.getProperty("line.separator", "\n"));
                        buffer.append(System.getProperty("line.separator", "\n"));
                    }
                }
                osw.write(buffer.toString().trim());
            }
        } finally {
//            if(bw != null) {
//                bw.flush();
//                bw.close();
//            }
            if (osw != null) {
                osw.flush();
                osw.close();
                fos.close();
            }
        }
    }

    /**
     * Checks to see whether the field - which consists of the formatted
     * contents of an Excel worksheet cell encapsulated within a String - contains
     * any embedded characters that must be escaped. The method is able to
     * comply with either Excel's or UNIX formatting conventions in the
     * following manner;
     *
     * With regard to UNIX conventions, if the field contains any embedded
     * field separator or EOL characters they will each be escaped by prefixing
     * a leading backspace character. These are the only changes that have yet
     * emerged following some research as being required.
     *
     * Excel has other embedded character escaping requirements, some that emerged
     * from empirical testing, other through research. Firstly, with regards to
     * any embedded speech marks ("), each occurrence should be escaped with
     * another speech mark and the whole field then surrounded with speech marks.
     * Thus if a field holds <em>"Hello" he said</em> then it should be modified
     * to appear as <em>"""Hello"" he said"</em>. Furthermore, if the field
     * contains either embedded separator or EOL characters, it should also
     * be surrounded with speech marks. As a result <em>1,400</em> would become
     * <em>"1,400"</em> assuming that the comma is the required field separator.
     * This has one consequence in, if a field contains embedded speech marks
     * and embedded separator characters, checks for both are not required as the
     * additional set of speech marks that should be placed around ay field
     * containing embedded speech marks will also account for the embedded
     * separator.
     *
     * It is worth making one further note with regard to embedded EOL
     * characters. If the data in a worksheet is exported as a CSV file using
     * Excel itself, then the field will be surounded with speech marks. If the
     * resulting CSV file is then re-imports into another worksheet, the EOL
     * character will result in the original simgle field occupying more than
     * one cell. This same 'feature' is replicated in this classes behaviour.
     *
     * @param field An instance of the String class encapsulating the formatted
     *        contents of a cell on an Excel worksheet.
     * @return A String that encapsulates the formatted contents of that
     *         Excel worksheet cell but with any embedded separator, EOL or
     *         speech mark characters correctly escaped.
     */
    private String escapeEmbeddedCharacters(String field) {
        StringBuffer buffer = null;

        // If the fields contents should be formatted to confrom with Excel's
        // convention....
        if (this.formattingConvention == EXCEL_STYLE_ESCAPING) {

            // Firstly, check if there are any speech marks (") in the field;
            // each occurrence must be escaped with another set of spech marks
            // and then the entire field should be enclosed within another
            // set of speech marks. Thus, "Yes" he said would become
            // """Yes"" he said"
            if (field.contains("\"")) {
                buffer = new StringBuffer(field.replaceAll("\"", "\\\"\\\""));
                buffer.insert(0, "\"");
                buffer.append("\"");
            } else {
                // If the field contains either embedded separator or EOL
                // characters, then escape the whole field by surrounding it
                // with speech marks.
                buffer = new StringBuffer(field);
                if ((buffer.indexOf(this.separator)) > -1
                        || (buffer.indexOf("\n")) > -1) {
                    buffer.insert(0, "\"");
                    buffer.append("\"");
                }
            }
            return (buffer.toString().trim());
        } // The only other formatting convention this class obeys is the UNIX one
        // where any occurrence of the field separator or EOL character will
        // be escaped by preceding it with a backslash.
        else {
            if (field.contains(this.separator)) {
                field = field.replaceAll(this.separator, ("\\\\" + this.separator));
            }
            if (field.contains("\n")) {
                field = field.replaceAll("\n", "\\\\\n");
            }
            return (field);
        }
    }

    @Override
    public JPanel getDataPanel() {
        return dataPanel;
    }

    @Override
    public JTable getDataTable() {
        return dataTable;
    }

    @Override
    public boolean isFileLoaded() {
        boolean ret = false;
        if (this.workbook != null) {
            ret = true;
        }
        return ret;
    }

    @Override
    public File getFile() {
        return fileXLS;
    }

    @Override
    public JFrame getMainFrame() {
        return parent.getFrame();
    }

    public void exportData(ExportParamsResult result) throws FileNotFoundException, IOException, DBFException  {

        System.out.println(result);

        switch (result.fileExportEncode) {
            case ExportParamsResult.ENCODE_UTF8: {
                charset = null;
                prefixFile = ExportParamsResult.ENCODE_DEF_UTF8;
            }
            break;
            case ExportParamsResult.ENCODE_CP1125: {
                charset = new CP1125();
                prefixFile = ExportParamsResult.ENCODE_DEF_CP1125;
            }
            break;
            case ExportParamsResult.ENCODE_CP1251: {
                charset = new MS1251();
                prefixFile = ExportParamsResult.ENCODE_DEF_CP1251;
            }
            break;
        }


        switch (result.fileExportType) {


            case ExportParamsResult.EXPORT_TYPE_CSV: {

                printCSVFile();
                System.out.println(getFile());
                System.out.println(DBFFileFilter.changeExtension(getFile(), "csv"));
                System.out.println(DBFFileFilter.changeExtension(getFile(), ".csv"));


                if (charset != null) {
                    saveCSVFile(new File(DBFFileFilter.changeExtension(getFile(), "csv", prefixFile)), charset);
                } else {
                    saveCSVFile(new File(DBFFileFilter.changeExtension(getFile(), "csv", prefixFile)));
                }

            }
            break;
            case ExportParamsResult.EXPORT_TYPE_DBF: {

                saveDBFFile(workbook, result);

            }
            break;

        }
    }

    /**
     * Save current data to DBF file
     * @param workbook current workbook
     * @param exportParams export params from dialog
     */
    private void saveDBFFile(Workbook workbook, ExportParamsResult exportParams) throws DBFException {

        ArrayList<ArrayList> sheet = null;
        ArrayList<String> line = null;

        String csvLineElement = null;

        DBFField dbfFields[] = null;

        try {


            System.out.println("Saving the DBF file [" + getFile().getName() + "]");

            DBFDataModificator dataModificator = new DBFDataModificator();
            if (exportParams.replaceChar) {
                dataModificator.makeCharRepacer(exportParams.replaceCharFrom, exportParams.replaceCharTo);
            }

            // Step through the elements of the ArrayList that was used to hold
            // all of the data recovered from the Excel workbooks' sheets, rows
            // and cells.
            for (int i = 0; i < 1 /*this.csvData.size()*/; i++) {

                // Get an element from the ArrayList that contains the data for
                // the workbook. This element will itself be an ArrayList
                // containing Strings and each String will hold the data recovered
                // from a single cell. The for() loop is used to recover elements
                // from this 'row' ArrayList one at a time and to write the Strings
                // away to a StringBuffer thus assembling a single line for inclusion
                // in the CSV file. If a row was empty or if it was short, then
                // the ArrayList that contains it's data will also be shorter than
                // some of the others. Therefore, it is necessary to check within
                // the for loop to ensure that the ArrayList contains data to be
                // processed. If it does, then an element will be recovered and
                // appended to the StringBuffer.

                sheet = this.csvData.get(i);
                dbfHeader = new DBFHeader();

                if (exportParams.skipLine > sheet.size() || exportParams.structLine > sheet.size()) {
                    return;
                } // !!! or exception !!!

                int start = 0;
                if (exportParams.structLine > 0) {
                    start = exportParams.structLine;
                    if (exportParams.skipLine > exportParams.structLine) {
                        start = exportParams.skipLine;
                    }

                    dbfHeader.calculateMaxColumnWidths(start, sheet);

                    line = sheet.get(exportParams.structLine - 1);
                    int fcount = line.size();
                    dbfFields = new DBFField[fcount];
                    for (int f = 0; f < dbfFields.length; f++) {
                        int maxColWidth = 0;
                        if (f < dbfHeader.getMaxColumnWidths().size()) {
                            maxColWidth = dbfHeader.getMaxColumnWidths().get(f);
                        }
                        dbfFields[f] = DBFField.makeDBFField(line.get(f), maxColWidth);
                    }
                }

                Calendar cal = Calendar.getInstance();

                dbfHeader.setDate(cal.getTime());
                dbfHeader.setFieldsCount((byte) dbfFields.length);
                dbfHeader.calculateRecordLength(dbfFields);
                dbfHeader.setRecordCount(sheet.size() - start);
                dbfHeader.setFieldsData(dbfFields);

                System.out.println(dbfHeader);

                setOutputFileName(DBFFileFilter.changeExtension(fileXLS, "dbf"));

                System.out.println(getOutputFileName());

                DBFWriter dbfWriter = new DBFWriter(new File(getOutputFileName()));
                dbfWriter.setDbfHeader(dbfHeader);

                for (int k = start; k < sheet.size(); k++) {
                    line = sheet.get(k);

                    DBFRecord dbfRecord = new DBFRecord(dbfHeader, charset, dataModificator);

//System.out.println("Fill ["+line.size()+"]["+dbfHeader.getFieldsCount()+"]["+(dbfHeader.getFieldsCount()-line.size())+"]");
                    int addCount = dbfHeader.getFieldsCount() - line.size();
                    if (line.size() < dbfHeader.getFieldsCount()) {
                        // fill empty cells
                        for (int f = 0; f < addCount; f++) {
                            line.add("");
                        }
                    }

//System.out.println("Fill ["+line.size()+"]["+dbfHeader.getFieldsCount()+"]["+(dbfHeader.getFieldsCount()-line.size())+"]");
//System.out.println("Fill k["+line+"]");

                    dbfRecord.makeRecord(line, false);
                    dbfWriter.getDbfRecords().add(dbfRecord);

                }
                dbfWriter.write();
            }
//        } catch (DBFException e) {
//            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
        }


    }

    private void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    @Override
    public int getFileType() {
        return FYLE_TYPE_XLS;
    }

    @Override
    public String getFileExt() {
        return fileExt;
    }

    /**
     * Popup listener
     */
    class MousePopupListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            checkPopup(e);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            checkPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            checkPopup(e);
        }

        private void checkPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                //parent.getPopupXLSPanel().show(dataPanel, e.getX()+dataTable.getTableHeader().getHeight(), e.getY());
                parent.getPopupXLSPanel().show(dataTable, e.getX(), e.getY());
            }
        }
    }

    /**
     * Implemen row header for opened Excel file
     */
    class RowHeaderRenderer extends JLabel implements ListCellRenderer {

        public RowHeaderRenderer(JTable table) {
            JTableHeader header = table.getTableHeader();
            setOpaque(true);
            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
            setHorizontalAlignment(CENTER);
            setForeground(header.getForeground());
            setBackground(header.getBackground());
            setFont(header.getFont());
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    /**
     * List for row header with renderer
     */
    class JListRowHeader extends JList {

        private final JTable table;

        public JListRowHeader(ListModel model, JTable table, int fixedCellWidth, ListCellRenderer listCellRenderer) {
            super(model);
            this.table = table;
            this.setFixedCellWidth(fixedCellWidth);
            this.setFixedCellHeight(this.table.getRowHeight()
                    + this.table.getRowMargin() - 1);
            //                           + table.getIntercellSpacing().height);
            this.setCellRenderer(listCellRenderer);
        }

        @Override
        public void updateUI() {
            super.updateUI();
            if (this.table != null) {
                this.setFixedCellHeight(this.table.getRowHeight() + this.table.getRowMargin() - 1);
            }
        }
    }
}

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
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import net.sf.dvstar.swirl.desktopdbf.DesktopDBFView;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFField;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFFile;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFReader;

/**
 *
 * @author Dima
 */
public class DBFPanelLoader extends DataLoader implements LoaderInterface /*implements DBFFileInterface*/ {

    private static final int CHAR_W0 = 10;
    private File fileDBF = null;
    private boolean isFileLoaded = false;
    private DBFReader dbfFileData = null;
    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(net.sf.dvstar.swirl.desktopdbf.DesktopDBFApp.class).getContext().getResourceMap(DesktopDBFView.class);
    DesktopDBFView parent;
    //private JTable tableDBF;

    public DBFPanelLoader(DesktopDBFView parent, File selectedFile) {
        this.fileDBF = selectedFile;
        this.parent = parent;
        this.fileExt = "DBF";
        try {
            loadFile();
            createDataPanel();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DBFPanelLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DBFPanelLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean loadFile() {
        isFileLoaded = true;
        return (isFileLoaded);
    }

    @Override
    public boolean isFileLoaded() {
        return (isFileLoaded);
    }
    private DBFTableModel modelDBF = null;

    @Override
    public void createDataPanel() throws FileNotFoundException, IOException {

        if (isIsFileLoaded()) {
            try {

                dataPanel = new JPanel();
                dataPanel.setLayout(new BorderLayout());

                dbfFileData = new DBFReader(fileDBF);
                modelDBF = new DBFTableModel(dbfFileData);
                dataTable = new JTable(modelDBF);


                int www0 = 10;
                if (modelDBF.getRowCount() < 100) {
                    www0 = www0 * 2;
                } else if (modelDBF.getRowCount() < 1000) {
                    www0 = www0 * 3;
                } else if (modelDBF.getRowCount() < 10000) {
                    www0 = www0 * 4;
                } else {
                    www0 = www0 * 5;
                }

                getDataTable().addMouseListener(new MousePopupListener());

                //tableDBF.getColumnModel().getColumn(i+1).setPreferredWidth(www);

                TableCellRenderer custom = new ColoredRowRenderer(dbfFileData);
                getDataTable().setDefaultRenderer(Object.class, custom);
                getDataTable().setDefaultRenderer(Number.class, custom);

                /*
                for (int i = 0; i < modelDBF.getDbfFlds().length; i++) {
                DBFField dbfFld[]=modelDBF.getDbfFlds();
                int www = Math.max(dbfFld[i].name.length() * CHAR_W0, dbfFld[i].flen * CHAR_W0);
                www = Math.min(64 * CHAR_W0, www);
                tableDBF.getColumnModel().getColumn(i + 1).setPreferredWidth(www);
                }
                 */

                for (int i = 0; i < dbfFileData.getDBFFields().length; i++) {
                    DBFField dbfFld[] = dbfFileData.getDBFFields();
                    int www = Math.max(dbfFld[i].getFieldName().length() * CHAR_W0, dbfFld[i].getFieldLength() * CHAR_W0);
                    www = Math.min(64 * CHAR_W0, www);
                    getDataTable().getColumnModel().getColumn(i + 1).setPreferredWidth(www);
                }

                getDataTable().setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                TableColumn colorColumn = getDataTable().getColumn(getDataTable().getColumnName(0));
                DefaultTableCellRenderer colorColumnRenderer = new DefaultTableCellRenderer();
                colorColumnRenderer.setBackground(new Color(0xe1e1e1));
                colorColumn.setCellRenderer(colorColumnRenderer);

                getDataTable().getColumnModel().getColumn(0).setResizable(true);
                getDataTable().getColumnModel().getColumn(0).setPreferredWidth(www0);

                JScrollPane scroll = new JScrollPane(getDataTable());
                scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

//                System.out.println("tableDBF.getSize()=" + getDataTable().getSize());
//                System.out.println("tableDBF.getPreferredSize()=" + getDataTable().getPreferredSize());

                dataPanel.add(scroll);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(DBFPanelLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean isIsFileLoaded() {
        return isFileLoaded;
    }

    public DBFTableModel getModelDBF() {
        return modelDBF;
    }

    public DBFFile getDBFFile() {
        return dbfFileData;
    }

    @Override
    public JPanel getDataPanel() {
        return dataPanel;
    }

    @Override
    public void setCharsetView(Charset charset) {
        this.charsetView = charset;
        this.setCharsetViewDesc(charset.displayName());
        if(dbfFileData != null){
            dbfFileData.setCharsetView(charset);
        }
    }

    @Override
    public void setCharsetEncode(Charset charset) {
        this.charsetEncode = charset;
        this.setCharsetEncodeDesc(charset.displayName());
    }

    @Override
    public void setCharsetViewDesc(String charset) {
        this.charsetViewDesc = charset;
        if(dbfFileData != null){
            dbfFileData.setCharsetView(charset);
        }
    }

    @Override
    public void setCharsetEncodeDesc(String charset) {
        this.charsetEncodeDesc = charset;
    }


    /**
     * @return the tableDBF
     */
    @Override
    public JTable getDataTable() {
        return dataTable;
    }

    @Override
    public JFrame getMainFrame() {
        return parent.getFrame();
    }

    @Override
    public File getFile() {
        return fileDBF;
    }

    @Override
    public int getFileType() {
        return FYLE_TYPE_DBF;
    }


    @Override
    public String getFileExt() {
        return fileExt;
    }


    /**
     *
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
//                parent.getPopupXLSPanel().show(dataPanel, e.getX(), e.getY() +tableDBF.getTableHeader().getHeight());
                parent.getPopupDBFPanel().show(dataTable, e.getX(), e.getY());
            }
        }
    }
}

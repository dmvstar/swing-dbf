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

package net.sf.dvstar.swirl.desktopdbf;

import net.sf.dvstar.swirl.desktopdbf.data.DBFPanelLoader;
import java.awt.Color;
import java.awt.Component;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import net.sf.dvstar.swirl.desktopdbf.data.ColoredRowRenderer;
import org.jdesktop.application.Action;
import net.sf.dvstar.swirl.desktopdbf.data.DataLoader;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFFile;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

public class DesktopDBFInfo extends javax.swing.JDialog {
    ResourceMap globalResourceMap = null;


    public DesktopDBFInfo(DataLoader parent, DBFPanelLoader loader) {
        super(parent.getMainFrame());
long tms = System.currentTimeMillis();
long tme = tms;

tme = System.currentTimeMillis();       
System.out.println(" [DesktopDBFInfo][constructor][1] = "+(tme-tms)+"ms");
tms = tme;

        initComponents();
tme = System.currentTimeMillis();       
System.out.println(" [DesktopDBFInfo][constructor][2] = "+(tme-tms)+"ms");
tms = tme;
        globalResourceMap = Application.getInstance(DesktopDBFApp.class).getContext().getResourceMap(DesktopDBFInfo.class);
        this.setIconImage(globalResourceMap.getImageIcon("MainFrame.icon").getImage());

        getRootPane().setDefaultButton(closeButton);
tme = System.currentTimeMillis();       
System.out.println(" [DesktopDBFInfo][constructor][3] = "+(tme-tms)+"ms");
tms = tme;

        loadStruct(loader);

tme = System.currentTimeMillis();       
System.out.println(" [DesktopDBFInfo][constructor][9] = "+(tme-tms)+"ms");
tms = tme;
        
        
    }

    private void loadStruct(DBFPanelLoader loader) {
        Vector data  = new Vector();
        Vector names = new Vector();

        DBFFile dbfFileData = loader.getDBFFile();

        names.add("N");             // 5
        names.add("Field Name");    // 65
        names.add("Type");          // 10
        names.add("Len");           // 10
        names.add("Dec");           // 10
        
        int count = dbfFileData.getDBFFields().length;// loader.getModelDBF().getDbfData().getDbfFld().length;

        for (int i = 1; i < count; i++) {
            Vector item = new Vector();

            item.add(i);
/*
            item.add(loader.getModelDBF().getDbfData().getDbfFld()[i].name);
            item.add(String.format("%c", loader.getModelDBF().getDbfData().getDbfFld()[i].type));
            item.add(loader.getModelDBF().getDbfData().getDbfFld()[i].flen);
            item.add(loader.getModelDBF().getDbfData().getDbfFld()[i].dec);
*/
            item.add(dbfFileData.getDBFFields()[i].getFieldName());
            item.add(String.format("%c", dbfFileData.getDBFFields()[i].getDataType()));
            item.add(dbfFileData.getDBFFields()[i].getFieldLength());
            item.add(dbfFileData.getDBFFields()[i].getDecimalCount());


            data.add(item);
        }
        DefaultTableModel model = new DefaultTableModel( data, names );
        jtInfoDBF.setModel(model);
        TableCellRenderer custom = new ColoredRowRenderer(dbfFileData, true);
        jtInfoDBF.setDefaultRenderer(String.class, custom);                                                
        jtInfoDBF.setDefaultRenderer(Object.class, custom);                                                
        jtInfoDBF.setDefaultRenderer(Number.class, custom);
        int www = jtInfoDBF.getPreferredSize().width;
        jtInfoDBF.getColumnModel().getColumn(0).setMinWidth(www * 5 / 100);
        jtInfoDBF.getColumnModel().getColumn(1).setMinWidth(www * 65 / 100);
        jtInfoDBF.getColumnModel().getColumn(2).setMinWidth(www * 10 / 100);
        jtInfoDBF.getColumnModel().getColumn(3).setMinWidth(www * 10 / 100);
        jtInfoDBF.getColumnModel().getColumn(4).setMinWidth(www * 10 / 100);

        TableColumn colorColumn = jtInfoDBF.getColumn(jtInfoDBF.getColumnName(0)); 
        DefaultTableCellRenderer colorColumnRenderer = new DefaultTableCellRenderer(); 
        colorColumnRenderer.setBackground( new Color(0xe1e1e1) ); 
        colorColumn.setCellRenderer(colorColumnRenderer);

        lbFileName.setText( dbfFileData.getFile().getName() );
//        lbFieldCount.setText(""+dbfFileData.getDBFFields().length);
        lbFieldCount.setText(""+dbfFileData.getDBFHeader().getFieldsCount() );
        lbRecordCount.setText(""+dbfFileData.getDBFHeader().getRecordCount());
        lbDateCreate.setText(""+dbfFileData.getDBFHeader().getDate());

        lbCodePage.setText(  ""+dbfFileData.getDBFHeader().getCodePage()+"/"+dbfFileData.getDBFHeader().getLangID()  );
        lbRecordLength.setText(  ""+dbfFileData.getDBFHeader().getRecordLength()  );

        
//        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(desktopdbf.DesktopDBFApp.class).getContext().getResourceMap(DesktopDBFInfo.class);
//        lbInfo.setText(resourceMap.getString("lbInfo.text")+" "+loader.getFileName().getName());
        
        /*
        jtInfoDBF.setDefaultRenderer(Object.class, new
CustomTableCellRenderer());
        */
        
/*        
System.out.println( "jtInfoDBF="    +   jtInfoDBF.getSize() );
System.out.println( "0 = " + jtInfoDBF.getColumnModel().getColumn(0).getPreferredWidth() );
System.out.println( "1 = " + jtInfoDBF.getColumnModel().getColumn(1).getPreferredWidth() );
System.out.println( "2 = " + jtInfoDBF.getColumnModel().getColumn(2).getPreferredWidth() );
System.out.println( "3 = " + jtInfoDBF.getColumnModel().getColumn(3).getPreferredWidth() );
System.out.println( "4 = " + jtInfoDBF.getColumnModel().getColumn(4).getPreferredWidth() );
*/
    }
    
    
    public class CustomTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*            
            if(column==0)   cell.setBackground(Color.gray);
            else            cell.setBackground(Color.white);
*/ 
/*            
            if (value instanceof Integer) {
                Integer amount = (Integer) value;
            
                 
                if (amount.intValue() < 0) {
                    cell.setBackground(Color.red);
                // You can also customize the Font and Foreground this way
                // cell.setForeground();
                // cell.setFont();
                } else {
                    cell.setBackground(Color.white);
                }

            } 
*/
            return cell;
        }
    }
    

    @Action
    public void closeDialog() {
        //setVisible(false);
        dispose();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        plTop = new javax.swing.JPanel();
        lbInfo = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lbRecordCount = new javax.swing.JLabel();
        lbFieldCount = new javax.swing.JLabel();
        lbDateCreate = new javax.swing.JLabel();
        lbFileName = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lbCodePage = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lbRecordLength = new javax.swing.JLabel();
        plWork = new javax.swing.JPanel();
        scrollDBF = new javax.swing.JScrollPane();
        jtInfoDBF = new javax.swing.JTable();
        plButtons = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setName("infoDBF"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(net.sf.dvstar.swirl.desktopdbf.DesktopDBFApp.class).getContext().getResourceMap(DesktopDBFInfo.class);
        plTop.setBackground(resourceMap.getColor("plTop.background")); // NOI18N
        plTop.setName("plTop"); // NOI18N
        plTop.setPreferredSize(new java.awt.Dimension(100, 120));

        lbInfo.setFont(resourceMap.getFont("lbInfo.font")); // NOI18N
        lbInfo.setForeground(resourceMap.getColor("lbInfo.foreground")); // NOI18N
        lbInfo.setText(resourceMap.getString("lbInfo.text")); // NOI18N
        lbInfo.setName("lbInfo"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        lbRecordCount.setText(resourceMap.getString("lbRecordCount.text")); // NOI18N
        lbRecordCount.setName("lbRecordCount"); // NOI18N

        lbFieldCount.setText(resourceMap.getString("lbFieldCount.text")); // NOI18N
        lbFieldCount.setName("lbFieldCount"); // NOI18N

        lbDateCreate.setText(resourceMap.getString("lbDateCreate.text")); // NOI18N
        lbDateCreate.setName("lbDateCreate"); // NOI18N

        lbFileName.setText(resourceMap.getString("lbFileName.text")); // NOI18N
        lbFileName.setName("lbFileName"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        lbCodePage.setText(resourceMap.getString("lbCodePage.text")); // NOI18N
        lbCodePage.setName("lbCodePage"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        lbRecordLength.setText(resourceMap.getString("lbRecordLength.text")); // NOI18N
        lbRecordLength.setName("lbRecordLength"); // NOI18N

        org.jdesktop.layout.GroupLayout plTopLayout = new org.jdesktop.layout.GroupLayout(plTop);
        plTop.setLayout(plTopLayout);
        plTopLayout.setHorizontalGroup(
            plTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(plTopLayout.createSequentialGroup()
                .addContainerGap()
                .add(plTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(lbInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 197, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1)
                    .add(plTopLayout.createSequentialGroup()
                        .add(plTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(plTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lbFieldCount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(lbDateCreate, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(plTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, lbRecordCount, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                    .add(lbFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                    .add(plTopLayout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lbCodePage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE))
                    .add(plTopLayout.createSequentialGroup()
                        .add(jLabel5)
                        .add(49, 49, 49)
                        .add(lbRecordLength, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)))
                .addContainerGap())
        );
        plTopLayout.setVerticalGroup(
            plTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(plTopLayout.createSequentialGroup()
                .addContainerGap()
                .add(plTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbFileName)
                    .add(lbInfo))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(plTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lbRecordCount)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(plTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(lbFieldCount)
                    .add(jLabel5)
                    .add(lbRecordLength))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(plTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(lbDateCreate)
                    .add(jLabel4)
                    .add(lbCodePage))
                .add(25, 25, 25))
        );

        getContentPane().add(plTop, java.awt.BorderLayout.PAGE_START);

        plWork.setName("plWork"); // NOI18N
        plWork.setLayout(new java.awt.BorderLayout());

        scrollDBF.setName("scrollDBF"); // NOI18N

        jtInfoDBF.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jtInfoDBF.setName("jtInfoDBF"); // NOI18N
        scrollDBF.setViewportView(jtInfoDBF);

        plWork.add(scrollDBF, java.awt.BorderLayout.CENTER);

        getContentPane().add(plWork, java.awt.BorderLayout.CENTER);

        plButtons.setName("plButtons"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(net.sf.dvstar.swirl.desktopdbf.DesktopDBFApp.class).getContext().getActionMap(DesktopDBFInfo.class, this);
        closeButton.setAction(actionMap.get("closeDialog")); // NOI18N
        closeButton.setIcon(resourceMap.getIcon("closeButton.icon")); // NOI18N
        closeButton.setText(resourceMap.getString("closeButton.text")); // NOI18N
        closeButton.setName("closeButton"); // NOI18N
        closeButton.setOpaque(false);
        closeButton.setPreferredSize(new java.awt.Dimension(82, 29));

        org.jdesktop.layout.GroupLayout plButtonsLayout = new org.jdesktop.layout.GroupLayout(plButtons);
        plButtons.setLayout(plButtonsLayout);
        plButtonsLayout.setHorizontalGroup(
            plButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, plButtonsLayout.createSequentialGroup()
                .addContainerGap(371, Short.MAX_VALUE)
                .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(19, 19, 19))
        );
        plButtonsLayout.setVerticalGroup(
            plButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, plButtonsLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(plButtons, java.awt.BorderLayout.SOUTH);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-520)/2, (screenSize.height-351)/2, 520, 351);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTable jtInfoDBF;
    private javax.swing.JLabel lbCodePage;
    private javax.swing.JLabel lbDateCreate;
    private javax.swing.JLabel lbFieldCount;
    private javax.swing.JLabel lbFileName;
    private javax.swing.JLabel lbInfo;
    private javax.swing.JLabel lbRecordCount;
    private javax.swing.JLabel lbRecordLength;
    private javax.swing.JPanel plButtons;
    private javax.swing.JPanel plTop;
    private javax.swing.JPanel plWork;
    private javax.swing.JScrollPane scrollDBF;
    // End of variables declaration//GEN-END:variables
}



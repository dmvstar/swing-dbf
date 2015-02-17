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
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.jdesktop.application.Action;
import net.sf.dvstar.swirl.desktopdbf.data.DataLoader;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

public class CharReplace extends javax.swing.JDialog {
    ResourceMap globalResourceMap = null;


    public CharReplace(DataLoader parent, DBFPanelLoader loader) {

        super(parent.getMainFrame());
        initComponents();
        globalResourceMap = Application.getInstance(DesktopDBFApp.class).getContext().getResourceMap(DesktopDBFInfo.class);
        this.setIconImage(globalResourceMap.getImageIcon("MainFrame.icon").getImage());
        getRootPane().setDefaultButton(closeButton);
        
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

        plWork = new javax.swing.JPanel();
        scrollDBF = new javax.swing.JScrollPane();
        tblCharMap = new javax.swing.JTable();
        plTop = new javax.swing.JPanel();
        lbInfo = new javax.swing.JLabel();
        plButtons = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        closeButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setName("infoDBF"); // NOI18N

        plWork.setName("plWork"); // NOI18N
        plWork.setLayout(new java.awt.BorderLayout());

        scrollDBF.setName("scrollDBF"); // NOI18N

        tblCharMap.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"І", "{"},
                {"і", "}"},
                {"Ї", "^"},
                {"ї", "~"},
                {"Є", "["},
                {"є", "]"}
            },
            new String [] {
                "Char From", "Char To"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblCharMap.setName("tblCharMap"); // NOI18N
        scrollDBF.setViewportView(tblCharMap);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(net.sf.dvstar.swirl.desktopdbf.DesktopDBFApp.class).getContext().getResourceMap(CharReplace.class);
        tblCharMap.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblCharMap.columnModel.title0")); // NOI18N
        tblCharMap.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblCharMap.columnModel.title1")); // NOI18N

        plWork.add(scrollDBF, java.awt.BorderLayout.CENTER);

        plTop.setBackground(resourceMap.getColor("plTop.background")); // NOI18N
        plTop.setName("plTop"); // NOI18N
        plTop.setPreferredSize(new java.awt.Dimension(100, 48));

        lbInfo.setFont(resourceMap.getFont("lbInfo.font")); // NOI18N
        lbInfo.setForeground(resourceMap.getColor("lbInfo.foreground")); // NOI18N
        lbInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbInfo.setText(resourceMap.getString("lbInfo.text")); // NOI18N
        lbInfo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lbInfo.setName("lbInfo"); // NOI18N

        org.jdesktop.layout.GroupLayout plTopLayout = new org.jdesktop.layout.GroupLayout(plTop);
        plTop.setLayout(plTopLayout);
        plTopLayout.setHorizontalGroup(
            plTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(plTopLayout.createSequentialGroup()
                .addContainerGap()
                .add(lbInfo, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                .addContainerGap())
        );
        plTopLayout.setVerticalGroup(
            plTopLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(plTopLayout.createSequentialGroup()
                .addContainerGap()
                .add(lbInfo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        plWork.add(plTop, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(plWork, java.awt.BorderLayout.CENTER);

        plButtons.setName("plButtons"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(net.sf.dvstar.swirl.desktopdbf.DesktopDBFApp.class).getContext().getActionMap(CharReplace.class, this);
        closeButton.setAction(actionMap.get("closeDialog")); // NOI18N
        closeButton.setIcon(resourceMap.getIcon("closeButton.icon")); // NOI18N
        closeButton.setText(resourceMap.getString("closeButton.text")); // NOI18N
        closeButton.setToolTipText(resourceMap.getString("closeButton.toolTipText")); // NOI18N
        closeButton.setName("closeButton"); // NOI18N
        closeButton.setOpaque(false);
        closeButton.setPreferredSize(new java.awt.Dimension(82, 29));

        closeButton1.setAction(actionMap.get("closeDialog")); // NOI18N
        closeButton1.setIcon(resourceMap.getIcon("closeButton1.icon")); // NOI18N
        closeButton1.setText(resourceMap.getString("closeButton1.text")); // NOI18N
        closeButton1.setToolTipText(resourceMap.getString("closeButton1.toolTipText")); // NOI18N
        closeButton1.setName("closeButton1"); // NOI18N
        closeButton1.setOpaque(false);
        closeButton1.setPreferredSize(new java.awt.Dimension(82, 29));

        org.jdesktop.layout.GroupLayout plButtonsLayout = new org.jdesktop.layout.GroupLayout(plButtons);
        plButtons.setLayout(plButtonsLayout);
        plButtonsLayout.setHorizontalGroup(
            plButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, plButtonsLayout.createSequentialGroup()
                .addContainerGap()
                .add(closeButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 68, Short.MAX_VALUE)
                .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(19, 19, 19))
        );
        plButtonsLayout.setVerticalGroup(
            plButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, plButtonsLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(plButtonsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(closeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(closeButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        getContentPane().add(plButtons, java.awt.BorderLayout.SOUTH);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-349)/2, (screenSize.height-351)/2, 349, 351);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JButton closeButton1;
    private javax.swing.JLabel lbInfo;
    private javax.swing.JPanel plButtons;
    private javax.swing.JPanel plTop;
    private javax.swing.JPanel plWork;
    private javax.swing.JScrollPane scrollDBF;
    private javax.swing.JTable tblCharMap;
    // End of variables declaration//GEN-END:variables
}


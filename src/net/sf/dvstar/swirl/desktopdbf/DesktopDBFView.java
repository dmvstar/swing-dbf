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
/*
 * Main GUI
 * DesktopDBFView.java
 */
package net.sf.dvstar.swirl.desktopdbf;

import net.sf.dvstar.swirl.desktopdbf.data.DBFFileFilter;
import net.sf.dvstar.swirl.desktopdbf.data.DBFPanelLoader;
import java.awt.Dialog.ModalityType;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import javax.swing.AbstractAction;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.table.AbstractTableModel;
import net.sf.dvstar.swirl.desktopdbf.data.ColoredRowRenderer;
import org.jdesktop.application.Task;
import net.sf.dvstar.swirl.desktopdbf.data.DataLoader;
import net.sf.dvstar.swirl.desktopdbf.data.XLSPanelLoader;
import net.sf.dvstar.swirl.desktopdbf.dbf.DBFException;
import org.jdesktop.application.Application;
import org.jdesktop.application.session.PropertySupport;
import sun.nio.cs.MS1251;
import ua.nio.cs.ext.CP1125;
import ua.nio.cs.ext.KOI8_U;

/**
 * The application's main frame.
 */
public class DesktopDBFView extends FrameView {
// Possible Look & Feels

    private static final String mac =
            "com.sun.java.swing.plaf.mac.MacLookAndFeel";
    private static final String metal =
            "javax.swing.plaf.metal.MetalLookAndFeel";
    private static final String motif =
            "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    private static final String windows =
            "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    private static final String plastic =
            "com.jgoodies.looks.plastic.PlasticXPLookAndFeel";
    private static final String jtatoo =
            "com.jtattoo.plaf.smart.SmartLookAndFeel";
    private String currentLookAndFeel = metal;
    DesktopDBFApp mainApplication;
    private String fileName;
    private String configFilePath = System.getProperty("user.home") + "/.DesktopDBF/config.xml";
    private String configFileDir = System.getProperty("user.home") + "/.DesktopDBF/";
    private JPopupMenu popupDBFPanel = new JPopupMenu();
    private JPopupMenu popupXLSPanel = new JPopupMenu();
    private final ResourceMap globalResourceMap;
    private final String messageFormat = "File:[%s] Locale:[%s] Charset:[%s]->[%s]";

    private FileHandler fileTxt;
    private SimpleFormatter formatterTxt;
    private Logger globalLogger;



    public DesktopDBFView(DesktopDBFApp app) {
        super(app);

        this.mainApplication = app;

        prepareLogger();

        File fileConfig = new File(configFilePath);
        if (fileConfig.exists()) {
            Properties propConf = new Properties();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(fileConfig);
                propConf.loadFromXML(fis);
                currentLookAndFeel = propConf.getProperty("lookAndFeel", currentLookAndFeel);
                updateLookAndFeel();
                if (fis != null) {
                    fis.close();
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DesktopDBFView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DesktopDBFView.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
            }
        }

        initComponents();

        tpView.addChangeListener(new ChangeListener() {
            // This method is called whenever the selected tab changes

            @Override
            public void stateChanged(ChangeEvent evt) {
                JTabbedPane pane = (JTabbedPane) evt.getSource();
                // Get current tab
                int sel = pane.getSelectedIndex();
                DataLoader dataLoader = loadedFileMap.get(sel);
                switch (dataLoader.getFileType()) {
                    case DataLoader.FYLE_TYPE_DBF: {
                        miServiceStruct.setEnabled(true);


                    }
                    break;
                    case DataLoader.FYLE_TYPE_XLS: {
                        miServiceStruct.setEnabled(false);
                        
                    }
                    break;
                }
                String mess = String.format(messageFormat, dataLoader.getFileExt(), localeName, dataLoader.getCharsetViewDesc(), dataLoader.getCharsetEncodeDesc());
                statusMessageLabel.setText(mess);
            }
        });

        //tpView.setOpaque(true);

        globalResourceMap = Application.getInstance(DesktopDBFApp.class).getContext().getResourceMap(DesktopDBFView.class);
        this.getFrame().setIconImage(globalResourceMap.getImageIcon("DesktopDBFView.MainFrame.icon").getImage());

        initAddMenus();

        if (DesktopDBFApp.getArgs().length > 0) {
            for (int i = 0; i < DesktopDBFApp.getArgs().length; i++) {
                fileName = DesktopDBFApp.getArgs()[i];
                open().run();
            }
        }


        fileName = null;

        String mess = String.format(messageFormat, fileTypeDesc, localeName, charsetName, charsetName);
        int sel = tpView.getSelectedIndex();
        if (sel >= 0) {
            DataLoader dataLoader = loadedFileMap.get(sel);
            mess = String.format(messageFormat, dataLoader.getFileExt(), localeName, dataLoader.getCharsetViewDesc(), dataLoader.getCharsetEncodeDesc());
            statusMessageLabel.setText(mess);
        }

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();

        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });

        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = DesktopDBFApp.getApplication().getMainFrame();
            aboutBox = new DesktopDBFAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        DesktopDBFApp.getApplication().show(aboutBox);
    }

    /**
     * Stores the current L&F, and calls updateLookAndFeel, below
     */
    public void setLookAndFeel(String laf) {
        //if (!currentLookAndFeel.equals(laf))
        {
            currentLookAndFeel = laf;
            /* The recommended way of synchronizing state between multiple
             * controls that represent the same command is to use Actions.
             * The code below is a workaround and will be replaced in future
             * version of SwingSet2 demo.
             */
            String lafName = null;
            if (laf.equals(mac)) {
                lafName = "Macintosh";
            }
            if (laf.equals(metal)) {
                lafName = "Java";
            }
            if (laf.equals(motif)) {
                lafName = "Motif";
            }
            if (laf.equals(windows)) {
                lafName = "Windows";
            }
            if (laf.equals(plastic)) {
                lafName = "Plastic";
            }
            if (laf.equals(jtatoo)) {
                lafName = "Jtatoo";
            }
            //themesMenu.setEnabled(laf == metal);
            updateLookAndFeel();

            for (int i = 0; i < lafMenu.getItemCount(); i++) {
                JMenuItem item = lafMenu.getItem(i);
                if (item.getText().equals(lafName)) {
                    item.setSelected(true);
                } else {
                    item.setSelected(false);
                }
            }
        }
    }

    /**
     * Sets the current L&F on each demo module
     */
    private void updateLookAndFeel() {
        try {
            Properties propConf = new Properties();

            File fileConfig = new File(configFilePath);
            File configDir = new File(configFileDir);
            if (!configDir.exists()) {
                configDir.mkdirs();
                FileOutputStream fos = new FileOutputStream(fileConfig);
                propConf.setProperty("lookAndFeel", currentLookAndFeel);
                propConf.storeToXML(fos, null);
                fos.close();
            } else {
            }

            if (fileConfig.exists()) {
                FileInputStream fis = new FileInputStream(fileConfig);
                propConf.loadFromXML(fis);
                propConf.setProperty("lookAndFeel", currentLookAndFeel);
                FileOutputStream fos = new FileOutputStream(fileConfig);
                propConf.storeToXML(fos, null);
                fis.close();
                fos.close();
            }
            //updateUI();
            UIManager.setLookAndFeel(currentLookAndFeel);
            SwingUtilities.updateComponentTreeUI(this.getFrame() /*app.getMainFrame()*/);
            //app.getMainFrame().pack();
        } catch (Exception ex) {
            System.out.println("Failed loading L&F: " + currentLookAndFeel);
            System.out.println(ex);
        }
    }

    /**
     * @return the popupDBFPanel
     */
    public JPopupMenu getPopupDBFPanel() {
        return popupDBFPanel;
    }

    /**
     * @return the popupDBFPanel
     */
    public JPopupMenu getPopupXLSPanel() {
        return popupXLSPanel;
    }

    private void setCharsetViewMenu(JMenu menuCharset, ResourceMap resourceMap) {

        JMenuItem item;

        item = new JMenuItem(resourceMap.getString("Application.menuService.Charset.cp1125.text"));
        item.addActionListener(new CharsetViewMenuListener("CP1125"));
        menuCharset.add(item);

        item = new JMenuItem(resourceMap.getString("Application.menuService.Charset.koi8.text"));
        item.addActionListener(new CharsetViewMenuListener("KOI8-U"));
        menuCharset.add(item);

        item = new JMenuItem(resourceMap.getString("Application.menuService.Charset.win.text"));
        item.addActionListener(new CharsetViewMenuListener("CP1251"));
        menuCharset.add(item);

    }

    private void setCharsetEncodeMenu(JMenu menuPopEncoCharset, ResourceMap resourceMap) {
        JMenuItem item;

        item = new JMenuItem(resourceMap.getString("Application.menuService.Charset.cp1125.text"));
        item.addActionListener(new CharsetEncodeMenuListener(new CP1125()));
        menuPopEncoCharset.add(item);

        item = new JMenuItem(resourceMap.getString("Application.menuService.Charset.koi8.text"));
        item.addActionListener(new CharsetEncodeMenuListener(new KOI8_U()));
        menuPopEncoCharset.add(item);

        item = new JMenuItem(resourceMap.getString("Application.menuService.Charset.win.text"));
        item.addActionListener(new CharsetEncodeMenuListener(new MS1251()));
        menuPopEncoCharset.add(item);

    }

    private void prepareLogger() {
        globalLogger = Logger.getLogger(DesktopDBFView.class.getName());
        try {
            fileTxt = new FileHandler(configFileDir+"main.log", true);
            formatterTxt = new SimpleFormatter();
            fileTxt.setFormatter(formatterTxt);
            globalLogger.addHandler(fileTxt);
            globalLogger.setUseParentHandlers(false);
            Logger.getLogger(DesktopDBFView.class.getName()).addHandler(fileTxt);
        } catch (IOException ex) {
            Logger.getLogger(DesktopDBFView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(DesktopDBFView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class ChangeLookAndFeelAction extends AbstractAction {

        DesktopDBFView swingset;
        String laf;

        protected ChangeLookAndFeelAction(DesktopDBFView swingset, String laf) {
            //super("ChangeTheme");
            this.swingset = swingset;
            this.laf = laf;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            swingset.setLookAndFeel(laf);
        }
    }

    /**
     * A utility function that layers on top of the LookAndFeel's
     * isSupportedLookAndFeel() method. Returns true if the LookAndFeel
     * is supported. Returns false if the LookAndFeel is not supported
     * and/or if there is any kind of error checking if the LookAndFeel
     * is supported.
     *
     * The L&F menu will use this method to detemine whether the various
     * L&F options should be active or inactive.
     *
     */
    protected boolean isAvailableLookAndFeel(String laf) {
        try {
            Class lnfClass = Class.forName(laf);
            LookAndFeel newLAF = (LookAndFeel) (lnfClass.newInstance());
            return newLAF.isSupportedLookAndFeel();
        } catch (Exception e) { // If ANYTHING weird happens, return false
            return false;
        }
    }

    /**
     * Creates a JRadioButtonMenuItem for the Look and Feel menu
     */
    public JMenuItem createLafMenuItem(JMenu menu, String label, String mnemonic,
            String accessibleDescription, String laf) {
        JMenuItem mi = menu.add(new JMenuItem((label)));

        //mi.setMnemonic(getMnemonic(mnemonic));
        //mi.getAccessibleContext().setAccessibleDescription(getString(accessibleDescription));
        mi.addActionListener(new ChangeLookAndFeelAction(this, laf));

        mi.setEnabled(isAvailableLookAndFeel(laf));

        return mi;
    }

    private void initAddMenus() {

        JMenuItem item, mi;
        ImageIcon menuIcon;

        JMenu menuPopViewCharset = new JMenu();
        JMenu menuPopEncoCharset = new JMenu();

        ResourceMap resourceMap = Application.getInstance(net.sf.dvstar.swirl.desktopdbf.DesktopDBFApp.class).getContext().getResourceMap(DesktopDBFView.class);

        menuPopViewCharset.setText(resourceMap.getString("Application.menuService.Charset.text")); // NOI18N
        menuPopViewCharset.setName("menuCharset"); // NOI18N

        item = new JMenuItem();

        item.setText(resourceMap.getString("Application.menuService.Struct.text"));
        menuIcon = globalResourceMap.getImageIcon("DesktopDBFView.PopupDBFPanel.struct.icon");
        item.setIcon(menuIcon);
        item.addActionListener(new PopupDBFActionListener(PopupDBFActionListener.POPUP_DBFINFO));
        getPopupDBFPanel().add(item);

        setCharsetViewMenu(menuCharset, resourceMap);
        setCharsetViewMenu(menuPopViewCharset, resourceMap);

        menuPopEncoCharset.setText(resourceMap.getString("Application.menuService.menuEncode.text")); // NOI18N

        setCharsetEncodeMenu(menuEncode, resourceMap);
        setCharsetEncodeMenu(menuPopEncoCharset, resourceMap);

        getPopupDBFPanel().add(menuPopViewCharset);
        getPopupDBFPanel().add(menuPopEncoCharset);


        item = new JMenuItem(resourceMap.getString("Application.menuService.Locale.uk.text"));
        item.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/dvstar/swirl/desktopdbf/resources/img/flags/uk_UA.png")));
        item.addActionListener(new LocaleMenuListener("uk_UA"));
        menuLocale.add(item);
        item = new JMenuItem(resourceMap.getString("Application.menuService.Locale.ru.text"));
        item.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/dvstar/swirl/desktopdbf/resources/img/flags/ru_RU.png")));
        item.addActionListener(new LocaleMenuListener("ru_RU"));
        menuLocale.add(item);
        item = new JMenuItem(resourceMap.getString("Application.menuService.Locale.en.text"));
        item.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sf/dvstar/swirl/desktopdbf/resources/img/flags/en_US.png")));
        item.addActionListener(new LocaleMenuListener("en_US"));
        menuLocale.add(item);



        item = new JMenuItem();
        menuIcon = globalResourceMap.getImageIcon("DesktopDBFView.PopupXLSPanel.export.icon");

        item.setIcon(menuIcon);
        item.setText(resourceMap.getString("Application.menuService.Export.text"));
        item.addActionListener(new PopupXLSActionListener(PopupXLSActionListener.POPUP_XLS_EXPORT));
        getPopupXLSPanel().add(item);


        mi = createLafMenuItem(lafMenu, "Java", "LafMenu.java_mnemonic",
                "LafMenu.java_accessible_description", metal);
        mi.setSelected(true); // this is the default l&f

        UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();

        for (int counter = 0; counter < lafInfo.length; counter++) {
            String className = lafInfo[counter].getClassName();
            if (className.equals(motif)) {
                createLafMenuItem(lafMenu, "Motif", "LafMenu.motif_mnemonic",
                        "LafMenu.motif_accessible_description", motif);
            } else if (className.equals(windows)) {
                createLafMenuItem(lafMenu, "Windows", "LafMenu.windows_mnemonic",
                        "LafMenu.windows_accessible_description", windows);
            }
        }

        mi = createLafMenuItem(lafMenu, "Plastic", "LafMenu.java_mnemonic", "Plastic", plastic);
        mi = createLafMenuItem(lafMenu, "JTatoo", "LafMenu.java_mnemonic", "JTatoo", jtatoo);


    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        tpView = new javax.swing.JTabbedPane();
        mainToolBar = new javax.swing.JToolBar();
        btOpen = new javax.swing.JButton();
        btClose = new javax.swing.JButton();
        btExit = new javax.swing.JButton();
        tbNavigate = new javax.swing.JToolBar();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu menuFile = new javax.swing.JMenu();
        fileOpen = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        fileSave = new javax.swing.JMenuItem();
        fileSaveAs = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        fileClose = new javax.swing.JMenuItem();
        fileCloseAll = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        menuService = new javax.swing.JMenu();
        miServiceStruct = new javax.swing.JMenuItem();
        miServiceExport = new javax.swing.JMenuItem();
        menuCharset = new javax.swing.JMenu();
        menuEncode = new javax.swing.JMenu();
        menuLocale = new javax.swing.JMenu();
        lafMenu = new javax.swing.JMenu();
        javax.swing.JMenu menuHelp = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        tpView.setName("tpView"); // NOI18N
        mainPanel.add(tpView, java.awt.BorderLayout.CENTER);

        mainToolBar.setRollover(true);
        mainToolBar.setName("mainToolBar"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(net.sf.dvstar.swirl.desktopdbf.DesktopDBFApp.class).getContext().getActionMap(DesktopDBFView.class, this);
        btOpen.setAction(actionMap.get("open")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(net.sf.dvstar.swirl.desktopdbf.DesktopDBFApp.class).getContext().getResourceMap(DesktopDBFView.class);
        btOpen.setText(resourceMap.getString("Application.menuFile.Open.text")); // NOI18N
        btOpen.setFocusable(false);
        btOpen.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btOpen.setMaximumSize(new java.awt.Dimension(102, 29));
        btOpen.setMinimumSize(new java.awt.Dimension(72, 29));
        btOpen.setName("btOpen"); // NOI18N
        btOpen.setPreferredSize(new java.awt.Dimension(72, 29));
        mainToolBar.add(btOpen);

        btClose.setAction(actionMap.get("closeCurrent")); // NOI18N
        btClose.setText(resourceMap.getString("Application.menuFile.Close.text")); // NOI18N
        btClose.setFocusable(false);
        btClose.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btClose.setMaximumSize(new java.awt.Dimension(102, 29));
        btClose.setMinimumSize(new java.awt.Dimension(72, 29));
        btClose.setName("btClose"); // NOI18N
        btClose.setPreferredSize(new java.awt.Dimension(72, 29));
        mainToolBar.add(btClose);

        btExit.setAction(actionMap.get("quit")); // NOI18N
        btExit.setText(resourceMap.getString("Application.menuFile.Exit.text")); // NOI18N
        btExit.setFocusable(false);
        btExit.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btExit.setMaximumSize(new java.awt.Dimension(102, 29));
        btExit.setMinimumSize(new java.awt.Dimension(72, 29));
        btExit.setName("btExit"); // NOI18N
        btExit.setPreferredSize(new java.awt.Dimension(72, 29));
        mainToolBar.add(btExit);

        mainPanel.add(mainToolBar, java.awt.BorderLayout.PAGE_START);

        tbNavigate.setRollover(true);
        tbNavigate.setEnabled(false);
        tbNavigate.setName("tbNavigate"); // NOI18N

        jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
        jButton2.setEnabled(false);
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setLabel(resourceMap.getString("jButton2.label")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbNavigate.add(jButton2);

        jButton3.setIcon(resourceMap.getIcon("jButton3.icon")); // NOI18N
        jButton3.setEnabled(false);
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setName("jButton3"); // NOI18N
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbNavigate.add(jButton3);

        jButton4.setIcon(resourceMap.getIcon("jButton4.icon")); // NOI18N
        jButton4.setEnabled(false);
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setName("jButton4"); // NOI18N
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbNavigate.add(jButton4);

        jButton5.setIcon(resourceMap.getIcon("jButton5.icon")); // NOI18N
        jButton5.setEnabled(false);
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setName("jButton5"); // NOI18N
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbNavigate.add(jButton5);

        jButton6.setIcon(resourceMap.getIcon("jButton6.icon")); // NOI18N
        jButton6.setEnabled(false);
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setName("jButton6"); // NOI18N
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tbNavigate.add(jButton6);

        mainPanel.add(tbNavigate, java.awt.BorderLayout.SOUTH);

        menuBar.setName("menuBar"); // NOI18N

        menuFile.setIcon(resourceMap.getIcon("menuFile.icon")); // NOI18N
        menuFile.setText(resourceMap.getString("Application.menuFile.text")); // NOI18N
        menuFile.setName("menuFile"); // NOI18N

        fileOpen.setAction(actionMap.get("open")); // NOI18N
        fileOpen.setText(resourceMap.getString("Application.menuFile.Open.text")); // NOI18N
        fileOpen.setName("fileOpen"); // NOI18N
        menuFile.add(fileOpen);

        jSeparator1.setName("jSeparator1"); // NOI18N
        menuFile.add(jSeparator1);

        fileSave.setIcon(resourceMap.getIcon("fileSave.icon")); // NOI18N
        fileSave.setText(resourceMap.getString("Application.menuFile.fileSave.text")); // NOI18N
        fileSave.setToolTipText(resourceMap.getString("fileSave.toolTipText")); // NOI18N
        fileSave.setName("fileSave"); // NOI18N
        menuFile.add(fileSave);

        fileSaveAs.setAction(actionMap.get("saveas")); // NOI18N
        fileSaveAs.setIcon(resourceMap.getIcon("fileSaveAs.icon")); // NOI18N
        fileSaveAs.setText(resourceMap.getString("Application.menuFile.fileSaveAs.text")); // NOI18N
        fileSaveAs.setName("fileSaveAs"); // NOI18N
        menuFile.add(fileSaveAs);

        jSeparator3.setName("jSeparator3"); // NOI18N
        menuFile.add(jSeparator3);

        fileClose.setAction(actionMap.get("closeCurrent")); // NOI18N
        fileClose.setText(resourceMap.getString("Application.menuFile.Close.text")); // NOI18N
        fileClose.setName("fileClose"); // NOI18N
        menuFile.add(fileClose);

        fileCloseAll.setAction(actionMap.get("closeAll")); // NOI18N
        fileCloseAll.setText(resourceMap.getString("Application.menuFile.CloseAll.text")); // NOI18N
        fileCloseAll.setName("fileCloseAll"); // NOI18N
        menuFile.add(fileCloseAll);

        jSeparator2.setName("jSeparator2"); // NOI18N
        menuFile.add(jSeparator2);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setText(resourceMap.getString("Application.menuFile.Exit.text")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        menuFile.add(exitMenuItem);

        menuBar.add(menuFile);

        menuService.setText(resourceMap.getString("Application.menuService.text")); // NOI18N

        miServiceStruct.setAction(actionMap.get("showDBFInfo")); // NOI18N
        miServiceStruct.setText(resourceMap.getString("Application.menuService.Struct.text")); // NOI18N
        miServiceStruct.setName("miServiceStruct"); // NOI18N
        menuService.add(miServiceStruct);

        miServiceExport.setAction(actionMap.get("exportTo")); // NOI18N
        miServiceExport.setText(resourceMap.getString("Application.menuService.Export.text")); // NOI18N
        miServiceExport.setName("miServiceExport"); // NOI18N
        menuService.add(miServiceExport);

        menuCharset.setText(resourceMap.getString("Application.menuService.Charset.text")); // NOI18N
        menuCharset.setName("menuCharset"); // NOI18N
        menuService.add(menuCharset);

        menuEncode.setText(resourceMap.getString("Application.menuService.menuEncode.text")); // NOI18N
        menuEncode.setName("menuEncode"); // NOI18N
        menuService.add(menuEncode);

        menuLocale.setText(resourceMap.getString("Application.menuService.Locale.text")); // NOI18N
        menuLocale.setName("menuLocale"); // NOI18N
        menuService.add(menuLocale);

        lafMenu.setText(resourceMap.getString("Application.menuService.Plaf.text")); // NOI18N
        lafMenu.setName("lafMenu"); // NOI18N
        menuService.add(lafMenu);

        menuBar.add(menuService);

        menuHelp.setText(resourceMap.getString("Application.menuHelp.text")); // NOI18N
        menuHelp.setName("menuHelp"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setIcon(resourceMap.getIcon("aboutMenuItem.icon")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("Application.menuHelp.About.text")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        menuHelp.add(aboutMenuItem);

        menuBar.add(menuHelp);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusMessageLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 481, Short.MAX_VALUE)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusMessageLabel)
                    .add(statusAnimationLabel)
                    .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
        setToolBar(mainToolBar);
    }// </editor-fold>//GEN-END:initComponents
    DBFPanelLoader dbfLoader;
    XLSPanelLoader xlsLoader;
    ArrayList<DataLoader> loadedFileMap = new ArrayList();

    private class LoadFileTask extends org.jdesktop.application.Task<Object, Void> {

        LoadFileTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to SendEmailTaskTask fields, here.
            super(app);
            //System.out.println("Starting thread to load file..." + fileToLoad);
        }

        @Override
        protected Void doInBackground() {
            try {
                for (int i = 0; i < 10; i++) {
                    setMessage("Working... [" + i + "]");
                    Thread.sleep(150L);
                    setProgress(i, 0, 9);
                }
                Thread.sleep(150L);
            } catch (InterruptedException ignore) {
            }
            return null;
        }

        @Override
        protected void finished() {
            setMessage("Done.");
        }
    }

    @Action
    public final Task open() {
        return new OpenTask(getApplication());
    }

    private class OpenTask extends org.jdesktop.application.Task<Object, Void> {

        OpenTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to OpenTask fields, here.
            super(app);
            //new LoadFileTask(org.jdesktop.application.Application.getInstance (desktopdbf.DesktopDBFApp.class));
        }

        @Override
        protected Object doInBackground() throws FileNotFoundException {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.

            btOpen.setEnabled(false);
            File fileToOpen = null;
            String lastDir = ".";

//System.out.println( String.format("Open file doInBackground :[%s]", fileName)  );

            if (fileName == null) {

                try {
                    DBFFileFilter filter = new DBFFileFilter();
                    JFileChooser jfileChooser = new JFileChooser();

                    Properties propConf = new Properties();

                    File fileConfig = new File(configFilePath);

                    if (fileConfig.exists()) {

                        FileInputStream fis = new FileInputStream(fileConfig);
                        propConf.loadFromXML(fis);
                        fis.close();

                        lastDir = propConf.getProperty("lastDir", ".");

                    }

                    Logger.getLogger(DesktopDBFView.class.getName()).log(Level.SEVERE, ("Last dir for load is : " + lastDir));

                    jfileChooser.setCurrentDirectory(new File(lastDir));

                    //Property prop = mainApplication.getContext().getSessionStorage().getProperty( JFileChooser.class );
                    
                    PropertySupport prop = (PropertySupport) mainApplication.getContext().getSessionStorage().getProperty( JFileChooser.class );
                    
                    //prop = new Property();
                    jfileChooser.setFileFilter(filter);
                    int showResult = jfileChooser.showOpenDialog(tpView);

                    fileToOpen = jfileChooser.getSelectedFile();

                    if (fileToOpen != null) {
                        fileName = jfileChooser.getSelectedFile().getName();
                    }

                    if (showResult == JFileChooser.APPROVE_OPTION && fileName != null) {

                        fileName = jfileChooser.getSelectedFile().getName();

//!!!!!!!!!!!                        mainApplication.getContext().getSessionStorage().putProperty(JFileChooser.class, prop);
                        
                        propConf.setProperty("lastDir", jfileChooser.getCurrentDirectory().getPath());

                        File configDir = new File(configFileDir);
                        if (!configDir.exists()) {
                            configDir.mkdirs();
                        }

                        FileOutputStream fos = new FileOutputStream(System.getProperty("user.home") + "/.desktopDBF/config.xml");
                        propConf.storeToXML(fos, null);
                        fos.close();


                        if (DBFFileFilter.getExtension(fileToOpen).equals(DBFFileFilter.EXT_DBF)) {
                            dbfLoader = new DBFPanelLoader(DesktopDBFView.this, fileToOpen);
                            dbfLoader.setCharsetEncodeDesc(charsetName);
                            dbfLoader.setCharsetViewDesc(charsetName);
                            if (dbfLoader.isFileLoaded()) {
                                loadedFileMap.add(dbfLoader);
                                ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/net/sf/dvstar/swirl/desktopdbf/resources/img/16x16/application-vnd.ms-access.png"));
                                tpView.addTab(fileName, icon, dbfLoader.getDataPanel());
                                tpView.setBackgroundAt(tpView.getTabCount() - 1, ColoredRowRenderer.RGBtoHSV(153, 255, 204));
//                                tpView.setForegroundAt(tpView.getTabCount() - 1, ColoredRowRenderer.RGBtoHSV(153, 255, 204));
                                tpView.setSelectedIndex(tpView.getTabCount() - 1);
                                miServiceStruct.setEnabled(true);
                            }
                        } else if (DBFFileFilter.getExtension(fileToOpen).equals(DBFFileFilter.EXT_XLS)) {
                            xlsLoader = new XLSPanelLoader(DesktopDBFView.this, fileToOpen);
                            xlsLoader.setCharsetEncodeDesc(charsetName);
                            xlsLoader.setCharsetViewDesc(charsetName);
                            if (xlsLoader.isFileLoaded()) {
                                loadedFileMap.add(xlsLoader);
                                ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/net/sf/dvstar/swirl/desktopdbf/resources/img/16x16/application-vnd.ms-excel.png"));
                                tpView.addTab(fileName, icon, xlsLoader.getDataPanel());
                                tpView.setBackgroundAt(tpView.getTabCount() - 1, ColoredRowRenderer.RGBtoHSV(255, 204, 204));
//                                tpView.setForegroundAt(tpView.getTabCount() - 1, ColoredRowRenderer.RGBtoHSV(255, 204, 204));
                                tpView.setSelectedIndex(tpView.getTabCount() - 1);
                            }
                        }

                    }
                    fileName = null;
                    btOpen.setEnabled(true);

                } catch (IOException ex) {
                    Logger.getLogger(DesktopDBFView.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                fileToOpen = new File(fileName);
                if (fileName != null) {

                    if (DBFFileFilter.getExtension(fileToOpen).equals(DBFFileFilter.EXT_DBF)) {

                        dbfLoader = new DBFPanelLoader(DesktopDBFView.this, fileToOpen);

                        if (dbfLoader.isFileLoaded()) {
                            dbfLoader.setCharsetEncodeDesc(charsetName);
                            dbfLoader.setCharsetViewDesc(charsetName);
                            loadedFileMap.add(dbfLoader);
                            ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/net/sf/dvstar/swirl/desktopdbf/resources/img/16x16/application-vnd.ms-access.png"));
                            tpView.addTab(fileToOpen.getName(), icon, dbfLoader.getDataPanel());
                            tpView.setBackgroundAt(tpView.getTabCount() - 1, ColoredRowRenderer.RGBtoHSV(153, 255, 204));
//                            tpView.setForegroundAt(tpView.getTabCount() - 1, ColoredRowRenderer.RGBtoHSV(153, 255, 204));
                            tpView.setSelectedIndex(tpView.getTabCount() - 1);
                            miServiceStruct.setEnabled(true);
                        }
                    } else if (DBFFileFilter.getExtension(fileToOpen).equals(DBFFileFilter.EXT_XLS)) {
                        xlsLoader = new XLSPanelLoader(DesktopDBFView.this, fileToOpen);
                        if (xlsLoader.isFileLoaded()) {
                            xlsLoader.setCharsetEncodeDesc(charsetName);
                            xlsLoader.setCharsetViewDesc(charsetName);
                            loadedFileMap.add(xlsLoader);
                            ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/net/sf/dvstar/swirl/desktopdbf/resources/img/16x16/application-vnd.ms-excel.png"));
                            tpView.addTab(fileToOpen.getName(), icon, xlsLoader.getDataPanel());
                            tpView.setBackgroundAt(tpView.getTabCount() - 1, ColoredRowRenderer.RGBtoHSV(255, 204, 204));
//                            tpView.setForegroundAt(tpView.getTabCount() - 1, ColoredRowRenderer.RGBtoHSV(255, 204, 204));
                            tpView.setSelectedIndex(tpView.getTabCount() - 1);
                        }
                    }

                }
                fileName = null;
                btOpen.setEnabled(true);
            }


            return dbfLoader;  // return your result
        }

        @Override
        protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().

            btOpen.setEnabled(true);

        }
    }

    /**
     * Show export dialog and export Ecxel data to extern file
     */
    @Action
    public void showXLSExport() {

        ExelExportDialog dialog = new ExelExportDialog();
        JFrame mainFrame = DesktopDBFApp.getApplication().getMainFrame();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);
        dialog.setVisible(true);

        if (dialog.getOk()) {
            DataLoader loader = loadedFileMap.get(tpView.getSelectedIndex());
            if (loader instanceof XLSPanelLoader) {
                XLSPanelLoader xlsLoaderA = (XLSPanelLoader) loader;
                try {

                    xlsLoaderA.exportData(dialog.getResult());

                    if (dialog.getResult().openExportFile) {


                        fileName = xlsLoaderA.getOutputFileName();

                        //open().run();

                        File exportedFile = new File(fileName);
                        dbfLoader = new DBFPanelLoader(DesktopDBFView.this, exportedFile);

                        loadedFileMap.add(dbfLoader);
                        ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/net/sf/dvstar/swirl/desktopdbf/resources/img/16x16/application-vnd.ms-access.png"));
                        tpView.addTab(exportedFile.getName(), icon, dbfLoader.getDataPanel());
                        tpView.setBackgroundAt(tpView.getTabCount() - 1, ColoredRowRenderer.RGBtoHSV(255, 255, 204));
//                        tpView.setForegroundAt(tpView.getTabCount() - 1, ColoredRowRenderer.RGBtoHSV(255, 255, 204));
                        tpView.setSelectedIndex(tpView.getTabCount() - 1);
                        fileName = null;

                    }
                } catch (DBFException ex) {
                    Logger.getLogger(DesktopDBFView.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(this.getFrame(), ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(DesktopDBFView.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(DesktopDBFView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Action
    public void closeAll() {

        tpView.removeAll();
        loadedFileMap.clear();
        miServiceStruct.setEnabled(false);

    }

    @Action
    public void closeCurrent() {

        loadedFileMap.remove(tpView.getSelectedIndex());
        tpView.remove(tpView.getSelectedIndex());

        if (tpView.getComponentCount() == 0) {
            miServiceStruct.setEnabled(false);
        }
    }

    /**
     * Display information about DBF file structure
     */
    @Action
    public void showDBFInfo() {
        JDialog dbfInfo;

//long tms = System.currentTimeMillis();
//long tme = tms;

        JFrame mainFrame = DesktopDBFApp.getApplication().getMainFrame();
//tme = System.currentTimeMillis();       
//System.out.println("[DesktopDBFView] DesktopDBFApp.getApplication().getMainFrame() = "+(tme-tms)+"ms");

//tms = tme;
        dbfInfo = new DesktopDBFInfo(loadedFileMap.get(tpView.getSelectedIndex()), (DBFPanelLoader) loadedFileMap.get(tpView.getSelectedIndex()));
//tme = System.currentTimeMillis();       
//System.out.println("[DesktopDBFView] new DesktopDBFInfo = "+(tme-tms)+"ms");

//tms = tme;
        dbfInfo.setName(null); // No Caused by: java.lang.StackOverflowError for java.beans.XMLEncoder
        dbfInfo.setLocationRelativeTo(mainFrame);
//tme = System.currentTimeMillis();       
//System.out.println("[DesktopDBFView] setLocationRelativeTo(mainFrame) = "+(tme-tms)+"ms");

        DesktopDBFApp.getApplication().show(dbfInfo);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btClose;
    private javax.swing.JButton btExit;
    private javax.swing.JButton btOpen;
    private javax.swing.JMenuItem fileClose;
    private javax.swing.JMenuItem fileCloseAll;
    private javax.swing.JMenuItem fileOpen;
    private javax.swing.JMenuItem fileSave;
    private javax.swing.JMenuItem fileSaveAs;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JMenu lafMenu;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JToolBar mainToolBar;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuCharset;
    private javax.swing.JMenu menuEncode;
    private javax.swing.JMenu menuLocale;
    private javax.swing.JMenu menuService;
    private javax.swing.JMenuItem miServiceExport;
    private javax.swing.JMenuItem miServiceStruct;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JToolBar tbNavigate;
    private javax.swing.JTabbedPane tpView;
    // End of variables declaration//GEN-END:variables
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    private String localeName = "uk_UA";
    private String charsetName = "CP1125";
    private String fileTypeDesc = "";

    class LocaleMenuListener implements ActionListener {

        String locale;

        public LocaleMenuListener(String locale) {
            this.locale = locale;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            localeName = locale;
            int sel = tpView.getSelectedIndex();
            if (sel >= 0) {
                DataLoader dataLoader = loadedFileMap.get(sel);
                statusMessageLabel.setText(String.format(messageFormat, dataLoader.getFileExt(), localeName, dataLoader.getCharsetViewDesc(), dataLoader.getCharsetEncodeDesc()));
            } else {
                statusMessageLabel.setText(String.format(messageFormat, "", localeName, "", ""));
            }
        }
    }

    class CharsetViewMenuListener implements ActionListener {

        String charset;

        public CharsetViewMenuListener(String charset) {
            this.charset = charset;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            charsetName = charset;
            int sel = tpView.getSelectedIndex();
            if (sel >= 0) {
                DataLoader dataLoader = loadedFileMap.get(sel);
                dataLoader.setCharsetViewDesc(charset);
                statusMessageLabel.setText(String.format(messageFormat, dataLoader.getFileExt(), localeName, dataLoader.getCharsetViewDesc(), dataLoader.getCharsetEncodeDesc()));
            } else {
                statusMessageLabel.setText(String.format(messageFormat, "", localeName, "", ""));
            }
            //statusMessageLabel.setText("Locale :" + localeName + " Charset :" + charsetName);
            ((AbstractTableModel) dbfLoader.getDataTable().getModel()).fireTableDataChanged();
            dbfLoader.getDataTable().repaint();
        }
    }

    class CharsetEncodeMenuListener implements ActionListener {

        Charset charset;

        public CharsetEncodeMenuListener(Charset charset) {
            this.charset = charset;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            //dbfLoader.getDBFFile().setCharset(charset);
            int sel = tpView.getSelectedIndex();
            if (sel >= 0) {
                DataLoader dataLoader = loadedFileMap.get(sel);
                dataLoader.setCharsetEncode(charset);
                statusMessageLabel.setText(String.format(messageFormat, dataLoader.getFileExt(), localeName, dataLoader.getCharsetViewDesc(), dataLoader.getCharsetEncodeDesc()));
            } else {
                statusMessageLabel.setText(String.format(messageFormat, "", localeName, "", ""));
            }
            //statusMessageLabel.setText("Locale :" + localeName + " Charset :" + charsetName);
            ((AbstractTableModel) dbfLoader.getDataTable().getModel()).fireTableDataChanged();
            dbfLoader.getDataTable().repaint();
        }
    }

    class PopupDBFActionListener implements ActionListener {

        public static final int POPUP_DBFINFO = 1;
        private int popupID;

        public PopupDBFActionListener(int popupID) {
            this.popupID = popupID;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (popupID) {
                case POPUP_DBFINFO: {
                    showDBFInfo();
                }
            }
        }
    }

    class PopupXLSActionListener implements ActionListener {

        public static final int POPUP_XLS_EXPORT = 1;
        private int popupID;

        public PopupXLSActionListener(int popupID) {
            this.popupID = popupID;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (popupID) {
                case POPUP_XLS_EXPORT: {
                    showXLSExport();
                }
            }
        }
    }

    @Action
    public void saveReg() {
    }

    @Action
    public void saveAs() {
    }

    @Action
    public void exportTo() {

        int sel = tpView.getSelectedIndex();
        if (sel < 0) {
            return;
        }

        DataLoader dataLoader = loadedFileMap.get(sel);
        switch (dataLoader.getFileType()) {
            case DataLoader.FYLE_TYPE_DBF: {

            }
            break;
            case DataLoader.FYLE_TYPE_XLS: {
                showXLSExport();
            }
            break;
            default: break;
        }

    }
}

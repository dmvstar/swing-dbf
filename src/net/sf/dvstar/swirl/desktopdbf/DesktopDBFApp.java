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
 * DesktopDBFApp.java
 */

package net.sf.dvstar.swirl.desktopdbf;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class DesktopDBFApp extends SingleFrameApplication {

    public static void setArgs(String[] vargs) {
        args = vargs;
    }
    
    public static String[] getArgs() {
        return(args);
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new DesktopDBFView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of DesktopDBFApp
     */
    public static DesktopDBFApp getApplication() {
        return Application.getInstance(DesktopDBFApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        setArgs(args);
        launch(DesktopDBFApp.class, args);
    }
    
    static String[] args;            
    
}

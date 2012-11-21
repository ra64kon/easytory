package de.easytory;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import de.easytory.gui.Gui;


/*
    Easytory - the easy repository
    Copyright (C) 2012, Ralf Konwalinka

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
public class Start 
{
    private static final double version = 0.102;
    private static final String databaseName = "Easytory";
    //private static final String iconFileName = "easytory.png";
    private static final String logFileName = "easytory.log";
    
    /**
     * @param args
     */
    public static void main(String[] args) 
    {
        Gui gui = new Gui();
        //Image icon = new ImageIcon(iconFileName).getImage();  
        Image icon = new ImageIcon(gui.getClass().getResource("/res/easytory.png")).getImage();
        if (icon!=null) gui.setIconImage(icon);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        gui.setSize(900, 600);
        gui.setLocationRelativeTo(gui.getParent());
        gui.setVisible(true);
        //Logger.getInstance().log("Starting Easytory");
    }
    
    public static String getVersion(){return "V" + version;}
    public static String getDatabaseName(){return databaseName;}
    public static String getLogFileName(){return logFileName;}
    
}
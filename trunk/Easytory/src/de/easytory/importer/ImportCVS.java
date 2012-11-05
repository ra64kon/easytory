package de.easytory.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;

import de.easytory.gui.Gui;
import de.easytory.main.Controller;
import de.easytory.main.Thing;
import de.easytory.main.Value;
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

public class ImportCVS
{
    private Controller controller;
    private Gui gui;

    public ImportCVS(Controller controller, Gui gui)
    {
        this.controller = controller;
        this.gui = gui;
    }

    /**
     * 
     */
    public String importCSV(File file)
    {
        Vector<String> headLine = new Vector<String>();
        String entityName="";
        try
        {
            BufferedReader buf  = new BufferedReader(new FileReader(file));
            String line = null;
            int row=0;
            int cols=0; 
            int newRows=0;
            int editRows=0;
            while((line = buf.readLine()) != null)
            {
                gui.showStatus("Importing line " + row); 
                if (row==0) // HeadLine
                {
                    entityName = readCSVHeadLine(line, headLine);
                    cols = headLine.size();
                }
                else
                {
                    int function = readCSVLine(line, headLine, entityName, cols);
                    if (function==1) newRows++;
                    if (function==2) editRows++;
                }
                row++;
            }
            buf.close();
            return "Read " + row + " rows, " + newRows + " items added, " + editRows + " rows changed.";
        }
        catch(Exception e)
        {
            gui.showStatus("Import error: " + e.getMessage()); 
            return "Import error: " + e.getMessage();   
        }    
    }
    
    private int readCSVLine(String line, Vector<String> headLine, String entityName, int cols)
    {
        Thing thing = new Thing("",entityName,false);
         // StringTokenizer don't work for empty items
        int col = 0;
        String[] tokens;
        tokens = line.split(";");
        boolean thingExists=false;
        for(int i =0; i < tokens.length ; i++)
        {
            if (col==0) // ThingName
            {
                String thingName = trimString(tokens[i]);
                Thing existingThing = controller.getThing(thingName, entityName);
                if (existingThing==null) 
                {
                    thing = new Thing(thingName, entityName,false); 
                }
                else 
                {
                    thing = existingThing;
                    thingExists=true;
                }
            }
            else if (col<cols) // Values
            {
                String thingName = trimString(tokens[i]);
                Value v = new Value(headLine.get(col), thingName, thing.getId(),"");
                if (!controller.existValue(v.getName(),v.getValue(),v.getThingId())) thing.addValue(v);
            }
            col++;
        }
        
        if (!thingExists)
        {
            controller.addThing(thing);
            return 1;
        }
        else
        {
            controller.updateThing(thing, true);
            return 2;    
        }
    }    
    
    private String readCSVHeadLine(String line, Vector<String> headLine)
    {
        StringTokenizer stHead = new StringTokenizer(line,";");
        while (stHead.hasMoreTokens())
        {
            headLine.add(trimString(stHead.nextToken()));
        }
        String entityName = headLine.firstElement();
        return entityName;
    }
    
    private String trimString(String text)
    {
       String s = text.trim();
       if (s.length()>99) s = s.substring(0, 99);
       return s;
    }
    
}

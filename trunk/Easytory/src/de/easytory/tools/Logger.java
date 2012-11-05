package de.easytory.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import de.easytory.Start;

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
public class Logger 
{
	static Logger myInstance; 
	
	/**
	 * Singleton
	 */
	private Logger()
	{

	}
	
	public synchronized static Logger getInstance()
	{
		if (myInstance==null) myInstance = new Logger();
		return myInstance;
	}
	  
	public void log(String text)
	{
	    try 
	    {
	       File file = new File(Start.getLogFileName());
	       Date date = new Date();
	       String version = Start.getVersion();
	       Writer writer = new FileWriter(file ,true);
	       
	       writer.write(version + ": " + date.toString() + ":");
	       writer.write(System.getProperty("line.separator"));
	       writer.write(text);
	       writer.write(System.getProperty("line.separator"));
	       for (StackTraceElement trace : new Throwable().getStackTrace())
	       {
	    	   if (trace.getLineNumber()>0)
	    	   {
	    		   writer.write(trace.toString());
	    		   writer.write(System.getProperty("line.separator"));
	    	   }
	       }
	       writer.write("--------------------------------------------------------------------------------");
	       writer.write(System.getProperty("line.separator"));
	       
	       writer.flush();
	       writer.close();
	    } 
	    catch (IOException e) 
	    {
	    	e.printStackTrace();
	    }
	}

}

package de.easytory.filesystem;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

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
public class EasytoryFile 
{
	private String fileName;
	private String shortFileName;
	private boolean isDirectory;
	
	private int fileSize = 0;
	private Date creationTime = new Date();
	private Date lastAccessTime = new Date();
	private Date lastWriteTime = new Date();

	
	public EasytoryFile(String fileName, boolean isDirectory) 
	{
		this.fileName = fileName;
		this.isDirectory = isDirectory;
	}
	
	public boolean isDirectory()
	{
		return isDirectory;
	}
	
	/*
	 * List filenames in this directory
	 */
	public Iterator<String> list()
	{
		return (new Vector<String>().iterator());
	}
	
	public long get64bitCreationTime()
	{
		return (creationTime.getTime() + 11644473600000L) * 10000;
	}
	
	public byte[] getFileData()
	{
		// crate RDF or Read File from original Filesystem
		return null;
	}
}

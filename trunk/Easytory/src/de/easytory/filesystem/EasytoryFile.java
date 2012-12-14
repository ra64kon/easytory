package de.easytory.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
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
	private boolean isDirectory;
	private long fileNumber;
	private int fileSize = 0;
	private Date lastModified = new Date();
	
	private byte[] binaryContentCache = new byte[0]; 
	
	private Vector<EasytoryFile> children = new Vector<EasytoryFile>();

	
	public EasytoryFile(String fileName, boolean isDirectory, long fileNumber) 
	{
		this.fileName = fileName;
		this.isDirectory = isDirectory;
		if (!isDirectory) this.fileSize = getLocalFileSize(fileName);
		File file = new File(fileName);
		if (file.exists()) this.lastModified = new Date(file.lastModified());
		this.fileNumber = fileNumber;
	}
	
	public void addChild(EasytoryFile child)
	{
		children.add(child);
	}
	
	public boolean isDirectory()
	{
		return isDirectory;
	}
	
	/*
	 * List filenames in this directory
	 */
	public Iterator<EasytoryFile> list()
	{
		if (isDirectory)
		{
			return children.iterator();
		}
		else
		{
			Vector<EasytoryFile> list = new Vector<EasytoryFile>();
			list.add(this);
			return list.iterator();
		}
	}
	
	public long get64bitLastModified()
	{
		return (lastModified.getTime() + 11644473600000L) * 10000;
	}
	
	public byte[] getBinaryContent(int from, int to) throws FileNotFoundException, IOException
	{
		// create RDF or Read File from original Filesystem
		
		if (binaryContentCache.length==0) binaryContentCache = readLocalFile(fileName);
		int contentLength = binaryContentCache.length;
		if (to > contentLength) to = contentLength;
		if (to <= from) throw new IOException("Wrong parameter to=" + to + ", from=" + from + ", content-length=" + contentLength);
		byte[] requestedContent = Arrays.copyOfRange(binaryContentCache, from, to);
		if (to == contentLength) 
		{
			binaryContentCache = new byte[0]; // clear cache
			System.out.println("Clear content cache: " + fileName);
		}
		return requestedContent;
	}
	
	private byte[] readLocalFile(String fileName) throws FileNotFoundException, IOException  
	{
		File file = new File(fileName);
		long size = file.length();
		byte[] bin = new byte[(int)size];
		FileInputStream fis = new FileInputStream(file);
		fis.read(bin);
        if(fis!=null) fis.close();
        return bin;
	}
	
	private int getLocalFileSize(String fileName)
	{
		File file = new File(fileName);
		return (int)file.length();
	}
	
	public String getFileName() 
	{
		return fileName;
	}

	public String getShortFileName() 
	{
		int to = fileName.length();
		if (to>8) to = 8;
		return fileName.toUpperCase().substring(0, to);
	}

		
	public int getFileSize() 
	{
		return fileSize;
	}

	public long getFileNumber() 
	{
		return fileNumber;
	}

}

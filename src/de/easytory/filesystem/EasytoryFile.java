package de.easytory.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import org.alfresco.jlan.server.filesys.FileAttribute;
import org.alfresco.jlan.server.filesys.FileInfo;

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
	//private static final String slash = "\\";
	private String fileName;
	private String path;
	private boolean isDirectory;
	private int fileNumber;
	private int fileSize = 0;
	private Date lastModified = new Date();
	
	private byte[] binaryContentCache = new byte[0]; 
	
	private Vector<EasytoryFile> children = new Vector<EasytoryFile>();

	
	public EasytoryFile(String fileName, String path, boolean isDirectory, int fileNumber) 
	{
		this.fileName = fileName;
		this.isDirectory = isDirectory;
		if (!isDirectory) this.fileSize = getLocalFileSize(fileName);
		File file = new File(fileName);
		if (file.exists()) this.lastModified = new Date(file.lastModified());
		this.fileNumber = fileNumber;
		this.path = path;
		//if (!path.endsWith(slash)) path = path + slash;
	}
	
	public void getJLanFileInfo(FileInfo info) 
	{
		//FileInfo info = new FileInfo();
		info.setFileName(fileName);
		info.setPath(path);  
		info.setFileId(fileNumber);
		info.setModifyDateTime(getLastModified());
        info.setCreationDateTime(getLastModified());
		info.setChangeDateTime(getLastModified());
		long alloc = (getFileSize() + 512L) & 0xFFFFFFFFFFFFFE00L;
		info.setAllocationSize(alloc);
		int fattr = 0;				
        fattr += FileAttribute.ReadOnly;
		if (isDirectory()) fattr += FileAttribute.Directory;
		info.setFileAttributes(fattr);
		//return info;
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
	public Vector<EasytoryFile> list()
	{
		if (isDirectory)
		{
			return children;
		}
		else
		{
			Vector<EasytoryFile> list = new Vector<EasytoryFile>();
			list.add(this);
			return list;
		}
	}
	
	public long getLastModified()
	{
		return lastModified.getTime();
	}
	
	/*
	public long get64bitLastModified()
	{
		return (lastModified.getTime() + 11644473600000L) * 10000;
	}*/
	
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

	public int getFileNumber() 
	{
		return fileNumber;
	}


	public String getPath() 
	{
		return path;
	}

}

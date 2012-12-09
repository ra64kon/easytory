package de.easytory.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import net.decasdev.dokan.DokanOperationException;
import net.decasdev.dokan.WinError;

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
public class EasytoryFilesystem 
{
	private ConcurrentHashMap<String, EasytoryFile> fileMap = new ConcurrentHashMap<String, EasytoryFile>();

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
	}

	public void createFilesystem()
	{
		// create Root
		// create Entity Dirs
	}
	
	
	public byte[] readFile(String filename)
	{
		// 1. get file from HashMap
		// 2. read content from EasytoryFile
		
		
		return null;
	}
	
	private byte[] readLocalFile(String filename) throws FileNotFoundException, IOException  
	{
		File file = new File(filename);
		long size = file.length();
		byte[] bin = new byte[(int)size];
		FileInputStream fis = new FileInputStream(file);
		fis.read(bin);
        if(fis!=null) fis.close();
        return bin;
	}
	
}

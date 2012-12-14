package de.easytory.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import de.easytory.main.Controller;
import de.easytory.main.Entity;
import de.easytory.main.Thing;

import net.decasdev.dokan.Dokan;

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
	private static String mountPoint = "V:\\";
	private ConcurrentHashMap<String, EasytoryFile> fileMap = new ConcurrentHashMap<String, EasytoryFile>();
	private long fileIndexCounter = 1;
	private Controller controller = new Controller();

	public static void main(String[] args) 
	{
		EasytoryFilesystem easytoryFilesystem = new EasytoryFilesystem();
		easytoryFilesystem.createFilesystem();
		String result = new WindowsAdapter(easytoryFilesystem).mount(mountPoint);
		System.out.println(result);
	}
	
	public static void unmount() 
	{
		Dokan.removeMountPoint(mountPoint);
		System.exit(0);
	}

	public void createFilesystem()
	{
		EasytoryFile root = createRoot();
		Iterator<Entity> entities = controller.getEntities().iterator();
		while (entities.hasNext())
		{
			Entity e = entities.next();
			EasytoryFile entity = createFile(root, e.getName(), true);
			Iterator<Thing> items = controller.getThingsByEntity(e.getName()).iterator();
			while (items.hasNext())
			{
				Thing t = items.next();
				createFile(entity, t.getName(), true);
			}
		}
	}

	private EasytoryFile createRoot() 
	{
		EasytoryFile root = new EasytoryFile("", true, fileIndexCounter); 
		fileMap.put("\\", root); 
		fileIndexCounter++;
		return root;
	}

	private EasytoryFile createFile(EasytoryFile parentDir, String fileName, boolean isDirectory) 
	{
		String parentFileName = parentDir.getFileName();
		if (!parentFileName.endsWith("\\")) parentFileName = parentFileName + "\\";
		String newFileName = parentFileName + fileName;
		EasytoryFile newFile = new EasytoryFile(fileName, isDirectory, fileIndexCounter); 
		fileMap.put(newFileName, newFile); 
		parentDir.addChild(newFile);
		fileIndexCounter++;
		System.out.println("Create: " + newFileName);
		return newFile;
	}
	
	public EasytoryFile getFileInformation(String fileName) throws FileNotFoundException
	{
		EasytoryFile file = fileMap.get(fileName);
		if (file != null) 
		{
			System.out.println("getInfo: " + file.getFileName());
			return file;
		}
		else
		{
			throw new FileNotFoundException("File '" + fileName + "' not found.");
		}
	}
	
	public Iterator<EasytoryFile> findFiles(String fileName) 
	{
		EasytoryFile file = fileMap.get(fileName);
		if (file != null) 
		{
			return file.list();
		}
		else
		{
			return new Vector<EasytoryFile>().iterator();
		}
	}
	
	public byte[] readFile(String fileName, int from, int to) throws FileNotFoundException, IOException
	{
		EasytoryFile file = fileMap.get(fileName);
		if (file != null) 
		{
			return file.getBinaryContent(from, to);
		}
		else
		{
			throw new FileNotFoundException("File '" + fileName + "' not found.");
		}
	}

	
}

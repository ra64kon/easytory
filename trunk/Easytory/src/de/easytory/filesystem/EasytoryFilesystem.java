package de.easytory.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import de.easytory.main.Controller;
import de.easytory.main.Entity;
import de.easytory.main.FilterListItem;
import de.easytory.main.Thing;

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
	//private static final String slash = Character.toString(java.io.File.separatorChar);
	private static final String slash = "\\";
	private ConcurrentHashMap<String, EasytoryFile> fileMap = new ConcurrentHashMap<String, EasytoryFile>();
	private int fileIndexCounter = 1;
	private Controller controller = new Controller();

	
	/**
	 * please refactor me!
	 */
	public void createFilesystem()
	{
		EasytoryFile root = createRoot();
		Iterator<Entity> entities = controller.getEntities().iterator();
		while (entities.hasNext()) // all entities
		{
			Entity e = entities.next();
			EasytoryFile entity = createFile(root, e.getName(), true);
						
			Vector<FilterListItem> filterList = controller.getFilter(e.getName());
			Collections.sort(filterList);
			Iterator<FilterListItem> filterIter = filterList.iterator();
			
			EasytoryFile all; // Create all items
			if (filterIter.hasNext()) all = createFile(entity, "All" , true); else all = entity; 
			Iterator<Thing> items = controller.getThingsByEntity(e.getName()).iterator();
			while (items.hasNext())
			{
				Thing t = items.next();
				createFile(all, t.getName(), true);
			}
			
			while (filterIter.hasNext()) // filter criteria
			{
				FilterListItem fi = filterIter.next();
				EasytoryFile subEntity = createFile(entity, e.getName() + " by " + fi.getValueName(), true);
				createSubValue(e, subEntity, fi);
				String lastFilterEntity = fi.getValueName();
				while (filterIter.hasNext()) 
				{
					FilterListItem fiNext = filterIter.next();
					if (!lastFilterEntity.equals(fiNext.getValueName())) //next subEntity
					{
						subEntity = createFile(entity, e.getName() + " by " + fiNext.getValueName(), true);
					}
					createSubValue(e, subEntity, fiNext);
					lastFilterEntity = fiNext.getValueName();
				}
			}
		}
	}

	private void createSubValue(Entity e, EasytoryFile subEntity, FilterListItem fiNext) 
	{
		EasytoryFile subValue = createFile(subEntity , fiNext.getValue(), true);
		LinkedList<FilterListItem> search = new LinkedList<FilterListItem>();
		search.add(fiNext);
		Iterator<Thing> ti = controller.getFilteredThings(search, e.getName()).iterator();
		while (ti.hasNext())
		{
			Thing t = ti.next();
			createFile(subValue, t.getName(), true);
		}
	}
	
	private EasytoryFile createRoot() 
	{
		EasytoryFile root = new EasytoryFile("", slash, true, fileIndexCounter); 
		//EasytoryFile root = new EasytoryFile(slash, slash, true, fileIndexCounter); 
		fileMap.put(slash, root); 
		fileIndexCounter++;
		return root;
	}

	private EasytoryFile createFile(EasytoryFile parentDir, String fileName, boolean isDirectory) 
	{
		String parentFileName = parentDir.getPath() + parentDir.getFileName();
		if (!parentFileName.endsWith(slash)) parentFileName = parentFileName + slash;
		String newFileName = parentFileName + fileName;
		EasytoryFile newFile = new EasytoryFile(fileName,parentFileName, isDirectory, fileIndexCounter); 
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
			System.out.println("File '" + fileName + "' not found.");
			throw new FileNotFoundException("File '" + fileName + "' not found.");
		}
	}
	
	/*
	 * 0 = file not exist
	 * 1 = file
	 * 2 = directory
	 */
	public int fileExists(String fileName) 
	{
		if (!fileMap.containsKey(fileName)) return 0;
		if (fileMap.get(fileName).isDirectory()) return 2; else return 1;
	}
	
	public Vector<EasytoryFile> findFiles(String fileName) 
	{
		EasytoryFile file = fileMap.get(fileName);
		if (file != null) 
		{
			return file.list();
		}
		else
		{
			return new Vector<EasytoryFile>();
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

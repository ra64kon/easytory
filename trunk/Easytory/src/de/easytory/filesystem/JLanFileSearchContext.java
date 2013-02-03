package de.easytory.filesystem;
import java.util.Vector;

import org.alfresco.jlan.server.filesys.FileInfo;
import org.alfresco.jlan.server.filesys.SearchContext;

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
public class JLanFileSearchContext extends SearchContext 
{
	private Vector<EasytoryFile> fileList;
	private int fileListSize;
	private int cursor = 0;
	
	public JLanFileSearchContext(Vector<EasytoryFile> findFiles) 
	{
		fileList = findFiles;
		fileListSize = findFiles.size();
	}

	@Override
	public int getResumeId() 
	{
		System.out.println("SearchContext:getResumeId: " + cursor);
		return cursor;
	}

	@Override
	public boolean hasMoreFiles() 
	{
		System.out.println("SearchContext:hasMoreFiles: " + (cursor<fileListSize));
		return (cursor<fileListSize);
	}

	@Override
	public boolean nextFileInfo(FileInfo info) 
	{
		if (cursor<fileListSize)
		{
			EasytoryFile eFile = fileList.elementAt(cursor);
			System.out.println("SearchContext:nextFileInfo " + eFile.getFileName());
			cursor++;
			eFile.getJLanFileInfo(info);
			return true;
		}
		else
		{
			System.out.println("SearchContext:nextFileInfo cursor<=fileListSize");
			return false;
		}
	}

	@Override
	public String nextFileName() 
	{
		System.out.println("SearchContext:nextFileName");
		if (cursor<fileListSize)
		{
			String filename = fileList.elementAt(cursor).getFileName();
			cursor++;
			return filename;
		}
		else
		{
			System.err.println("JLanFileSearchContext:nextFileName cursor>=fileListSize");
			return null;
		}
	}

	@Override
	public boolean restartAt(int resumeId) 
	{
		System.out.println("restartAt: " + resumeId);
		if (cursor<fileListSize)
		{
			cursor = resumeId;
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public boolean restartAt(FileInfo info) 
	{
		System.err.println("JLanFileSearchContext:restartAt(FileInfo) not implemented yet!");
		return false;
	}

}

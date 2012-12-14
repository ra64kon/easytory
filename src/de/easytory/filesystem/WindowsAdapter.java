package de.easytory.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import net.decasdev.dokan.ByHandleFileInformation;
import net.decasdev.dokan.Dokan;
import net.decasdev.dokan.DokanDiskFreeSpace;
import net.decasdev.dokan.DokanFileInfo;
import net.decasdev.dokan.DokanOperationException;
import net.decasdev.dokan.DokanOptions;
import net.decasdev.dokan.DokanOptionsMode;
import net.decasdev.dokan.DokanVolumeInformation;
import net.decasdev.dokan.FileAttribute;
import net.decasdev.dokan.Win32FindData;
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
public class WindowsAdapter implements net.decasdev.dokan.DokanOperations
{
	private final static int volumeSerialNumber = 8888;
	private EasytoryFilesystem easytoryFilesystem;
		
	public WindowsAdapter(EasytoryFilesystem easytoryFilesystem)
	{
		this.easytoryFilesystem = easytoryFilesystem;
	}
	
	public String mount(String driveLetter)
	{
		DokanOptions dokanOptions = new DokanOptions();
        dokanOptions.mountPoint = driveLetter;
        dokanOptions.threadCount = 1;
        dokanOptions.optionsMode = DokanOptionsMode.Mode.REMOVABLE_DRIVE.getValue() + DokanOptionsMode.Mode.KEEP_ALIVE.getValue();
        try 
        {
			int result = Dokan.mount(dokanOptions, this);
			return "Mount result: " + result;
		} 
        catch (Exception e) 
		{
			return "Mount error: " + e.getMessage();
		}
	}
	
	@Override
	public long onCreateFile(String fileName, int desiredAccess, int shareMode, int creationDisposition, int flagsAndAttributes, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long onOpenDirectory(String fileName, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onCreateDirectory(String fileName, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCleanup(String fileName, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCloseFile(String fileName, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int onReadFile(String fileName, ByteBuffer buffer, long offset, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		int from = (int)offset;
		try 
		{
			byte[] bin = easytoryFilesystem.readFile(fileName, from, buffer.capacity());
			int size = bin.length; if (size == 0) return 0;
			buffer.put(bin, from, size);  // hope thats correct :-/
			return size;
		} 
		catch (FileNotFoundException e) 
		{
			throw new DokanOperationException(WinError.ERROR_FILE_NOT_FOUND);
		} 
		catch (IOException e) 
		{
			throw new DokanOperationException(WinError.ERROR_READ_FAULT);
		}
	}

	@Override
	public int onWriteFile(String fileName, ByteBuffer buffer, long offset, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		throw new DokanOperationException(WinError.ERROR_WRITE_FAULT);
	}

	@Override
	public void onFlushFileBuffers(String fileName, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public ByHandleFileInformation onGetFileInformation(String fileName, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		try 
		{
			EasytoryFile file = easytoryFilesystem.getFileInformation(fileName);
			int fileAttribute = FileAttribute.FILE_ATTRIBUTE_NORMAL;
			long fileIndex = file.getFileNumber();
			int fileSize = file.getFileSize();
			long lastAccessTime = file.get64bitLastModified();
			long creationTime = file.get64bitLastModified();
			long lastWriteTime = file.get64bitLastModified();
			if (file.isDirectory()) fileAttribute |= FileAttribute.FILE_ATTRIBUTE_DIRECTORY;
			fileAttribute |= FileAttribute.FILE_ATTRIBUTE_READONLY;
			return new ByHandleFileInformation(fileAttribute, creationTime, lastAccessTime, lastWriteTime, volumeSerialNumber, fileSize, 1, fileIndex);
		} 
		catch (FileNotFoundException e) 
		{
			throw new DokanOperationException(WinError.ERROR_FILE_NOT_FOUND);
		}
	}

	@Override
	public Win32FindData[] onFindFiles(String pathName, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		ArrayList<Win32FindData> files = new ArrayList<Win32FindData>();
		Iterator<EasytoryFile> fileList = easytoryFilesystem.findFiles(pathName);
		while (fileList.hasNext())
		{
			EasytoryFile file = fileList.next();
			String fileName = file.getFileName();
			String shortFileName = file.getShortFileName();
			int fileAttribute = FileAttribute.FILE_ATTRIBUTE_NORMAL;
			int fileSize = file.getFileSize();
			long lastAccessTime = file.get64bitLastModified();
			long creationTime = file.get64bitLastModified();
			long lastWriteTime = file.get64bitLastModified();
			if (file.isDirectory()) fileAttribute |= FileAttribute.FILE_ATTRIBUTE_DIRECTORY;
			fileAttribute |= FileAttribute.FILE_ATTRIBUTE_READONLY;
			Win32FindData fileData = new Win32FindData(fileAttribute, creationTime, lastAccessTime, lastWriteTime, fileSize, 0, 0, fileName, shortFileName);
			files.add(fileData);
		}
		return files.toArray(new Win32FindData[0]);
	}

	@Override
	public Win32FindData[] onFindFilesWithPattern(String pathName,String searchPattern, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		ArrayList<Win32FindData> files = new ArrayList<Win32FindData>();
		return files.toArray(new Win32FindData[0]);
	}

	@Override
	public void onSetFileAttributes(String fileName, int fileAttributes, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetFileTime(String fileName, long creationTime, long lastAccessTime, long lastWriteTime, DokanFileInfo fileInfo) throws DokanOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeleteFile(String fileName, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeleteDirectory(String fileName, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMoveFile(String existingFileName, String newFileName,boolean replaceExisiting, DokanFileInfo fileInfo) throws DokanOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetEndOfFile(String fileName, long length,DokanFileInfo fileInfo) throws DokanOperationException 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLockFile(String fileName, long byteOffset, long length, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUnlockFile(String fileName, long byteOffset, long length,DokanFileInfo fileInfo) throws DokanOperationException 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public DokanDiskFreeSpace onGetDiskFreeSpace(DokanFileInfo fileInfo) throws DokanOperationException 
	{
		DokanDiskFreeSpace free = new DokanDiskFreeSpace();
        free.totalNumberOfBytes = 1000000;
        free.freeBytesAvailable = 500000;
        free.totalNumberOfFreeBytes = free.freeBytesAvailable;
        return free;
	}

	@Override
	public DokanVolumeInformation onGetVolumeInformation(String volumeName, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		DokanVolumeInformation info = new DokanVolumeInformation();
        info.maximumComponentLength = 256;
        info.volumeName = "VirtualFS";
        info.fileSystemName = "VIFS";
        info.volumeSerialNumber = volumeSerialNumber;
        return info;
	}

	@Override
	public void onUnmount(DokanFileInfo fileInfo) throws DokanOperationException 
	{
		System.out.println("onUnmount");
	}

}

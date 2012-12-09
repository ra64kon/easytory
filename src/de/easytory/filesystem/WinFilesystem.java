package de.easytory.filesystem;

import java.nio.ByteBuffer;
import java.util.ArrayList;

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
public class WinFilesystem implements net.decasdev.dokan.DokanOperations
{
	private final static int volumeSerialNumber = 8888;
	
	public static void main(String[] args) 
	{
		new WinFilesystem().mount("V:\\");
	}
	
	private void mount(String driveLetter) 
	{
		DokanOptions dokanOptions = new DokanOptions();
        dokanOptions.mountPoint = driveLetter;
        dokanOptions.threadCount = 1;
        dokanOptions.optionsMode = DokanOptionsMode.Mode.REMOVABLE_DRIVE.getValue() + DokanOptionsMode.Mode.KEEP_ALIVE.getValue();
		int result = Dokan.mount(dokanOptions, this);
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
		int off = (int)offset;
		byte[] bin = new byte[0];
		
		if (fileName.equals("\\filename.txt"))
		{		
			String text = "Töxt";
			bin = text.getBytes();  // this is not a good idea - charset !
		}
		
		int size = Math.min(buffer.capacity(), bin.length-off);
		if (size <= 0) return 0;
		buffer.put(bin, off, size);  // hope thats correct :-/
		return size;
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
		int fileAttribute = 0;
		int fileSize = 0;
		long creationTime = 0;
		long lastAccessTime = 0;
		long lastWriteTime = 0;
		long fileIndex = 0;
		if (fileName.equals("\\filename.txt"))
		{
			fileSize = 4;
		}
		else if (fileName.equals("\\") || fileName.equals("\\MyDir"))
		{
			fileSize = 0;
			fileAttribute = FileAttribute.FILE_ATTRIBUTE_DIRECTORY;
		}
		else
		{
			throw new DokanOperationException(WinError.ERROR_FILE_NOT_FOUND);
		}
		
		
		//fileAttribute |= FileAttribute.FILE_ATTRIBUTE_READONLY;
		
		return new ByHandleFileInformation(fileAttribute, creationTime, lastAccessTime, lastWriteTime, volumeSerialNumber, fileSize, 1, fileIndex);
	}

	@Override
	public Win32FindData[] onFindFiles(String pathName, DokanFileInfo fileInfo) throws DokanOperationException 
	{
		ArrayList<Win32FindData> files = new ArrayList<Win32FindData>();
		
		int fileAttribute = 0;
		int fileSize = 0;
		long creationTime = 0;
		long lastAccessTime = 0;
		long lastWriteTime = 0;
		
		//fileAttribute |= FileAttribute.FILE_ATTRIBUTE_READONLY;
		fileSize = 4;
		String fileName = "filename.txt";
		String shortFileName = "filename.txt";
		Win32FindData fileData = new Win32FindData(fileAttribute, creationTime, lastAccessTime, lastWriteTime, fileSize, 0, 0, fileName, shortFileName);
		files.add(fileData);		
		
		fileSize = 0;
		fileAttribute = FileAttribute.FILE_ATTRIBUTE_DIRECTORY;
		fileName = "MyDir";
		shortFileName = "MyDir";
		Win32FindData dirData = new Win32FindData(fileAttribute, creationTime, lastAccessTime, lastWriteTime, fileSize, 0, 0, fileName, shortFileName);
		files.add(dirData);
		
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

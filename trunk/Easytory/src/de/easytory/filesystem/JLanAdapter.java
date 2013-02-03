package de.easytory.filesystem;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.alfresco.config.ConfigElement;
import org.alfresco.jlan.app.JLANServer;
import org.alfresco.jlan.server.SrvSession;
import org.alfresco.jlan.server.core.DeviceContext;
import org.alfresco.jlan.server.core.DeviceContextException;
import org.alfresco.jlan.server.filesys.DiskDeviceContext;
import org.alfresco.jlan.server.filesys.DiskInterface;
import org.alfresco.jlan.server.filesys.FileInfo;
import org.alfresco.jlan.server.filesys.FileOpenParams;
import org.alfresco.jlan.server.filesys.FileSystem;
import org.alfresco.jlan.server.filesys.NetworkFile;
import org.alfresco.jlan.server.filesys.SearchContext;
import org.alfresco.jlan.server.filesys.TreeConnection;

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
public class JLanAdapter implements DiskInterface 
{
	//private static final String slash = Character.toString(java.io.File.separatorChar);
	private static final String slash = "\\"; // JLAN makes an internal conversation
	private EasytoryFilesystem easytoryFilesystem;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		String[] arg = new String[1];
		arg[0] = new String("config/jlanConfig.xml");
		JLANServer.main(arg);
	}
	
	
	@Override
	public DeviceContext createContext(String shareName, ConfigElement args) throws DeviceContextException 
	{
		DiskDeviceContext ctx = new DiskDeviceContext("");
		ctx.setFilesystemAttributes( FileSystem.CasePreservedNames + FileSystem.UnicodeOnDisk);
		// ctx.setAvailable(false); //	Mark the filesystem as unavailable
		easytoryFilesystem = new EasytoryFilesystem();
		easytoryFilesystem.createFilesystem();
 		return ctx;
	}

	@Override
	public void treeClosed(SrvSession arg0, TreeConnection arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void treeOpened(SrvSession arg0, TreeConnection arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeFile(SrvSession sess, TreeConnection tree,
			NetworkFile param) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void createDirectory(SrvSession sess, TreeConnection tree, FileOpenParams params) throws IOException 
	{
		// TODO Auto-generated method stub

	}

	@Override
	public NetworkFile createFile(SrvSession sess, TreeConnection tree, FileOpenParams params) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteDirectory(SrvSession sess, TreeConnection tree, String dir)
			throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteFile(SrvSession sess, TreeConnection tree, String name)
			throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public int fileExists(SrvSession sess, TreeConnection tree, String name) 
	{
		String fileName = removeWildcardAndSlash(name);
		int result = easytoryFilesystem.fileExists(fileName);
		System.out.println("fileExists name="+ name + " (" + fileName + "): " + result);
	    return result;
		//return FileStatus.FileExists;
	}

	@Override
	public void flushFile(SrvSession sess, TreeConnection tree, NetworkFile file)
			throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public FileInfo getFileInformation(SrvSession sess, TreeConnection tree, String name) throws java.io.IOException 
	{
		String fileName = removeWildcardAndSlash(name);
		System.out.println("getFileInformation-request name="+ name + " (" + fileName + ")");
		EasytoryFile eFile= easytoryFilesystem.getFileInformation(fileName);
		FileInfo info = new FileInfo();
		eFile.getJLanFileInfo(info);
		System.out.println("getFileInformation-result name=" + info.getFileName() + ", path=" +	info.getPath() + ", directory=" + info.isDirectory());
		return info;
	}


	@Override
	public boolean isReadOnly(SrvSession sess, DeviceContext ctx) throws IOException 
	{
		return true;
	}

	@Override
	public NetworkFile openFile(SrvSession sess, TreeConnection tree,
			FileOpenParams params) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int readFile(SrvSession sess, TreeConnection tree, NetworkFile file,
			byte[] buf, int bufPos, int siz, long filePos) throws IOException {
		System.out.println("readFile file=" + file.getName());
		return 0;
	}

	@Override
	public void renameFile(SrvSession sess, TreeConnection tree,
			String oldName, String newName) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public long seekFile(SrvSession sess, TreeConnection tree,
			NetworkFile file, long pos, int typ) throws IOException {
		System.out.println("seekFile");
		return 0;
	}

	@Override
	public void setFileInformation(SrvSession sess, TreeConnection tree,
			String name, FileInfo info) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public SearchContext startSearch(SrvSession sess, TreeConnection tree, String searchPath, int attrib) throws FileNotFoundException 
	{
		String fileName = removeWildcardAndSlash(searchPath);
		System.out.println("startSearch searchPath="+ searchPath + " (" + fileName + ")");
		JLanFileSearchContext srch = new JLanFileSearchContext(easytoryFilesystem.findFiles(fileName));
		return srch;
	}

	@Override
	public void truncateFile(SrvSession sess, TreeConnection tree,
			NetworkFile file, long siz) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public int writeFile(SrvSession sess, TreeConnection tree,
			NetworkFile file, byte[] buf, int bufoff, int siz, long fileoff)
			throws IOException {
		// TODO Auto-generated method stub
		return 0;
	} 
    
	private String removeWildcardAndSlash(String path)
	{
		if (path.length()==0 || path.equals(slash)) return slash; // root
		if (path.endsWith(slash)) return path.substring(0,path.length()-1);  // Remove slash at the end
		int wildCard = path.indexOf("*");
		if (wildCard>-1) 
		{
			path = path.substring(0, wildCard);
			int lastSlash = path.lastIndexOf(slash);
			if (lastSlash>0) //ignore root 
			{
				return path.substring(0, lastSlash);
			}
			else
			{
				return path; 
			}
		}
		else
		{
			return path;
		}
	}
	
	
}

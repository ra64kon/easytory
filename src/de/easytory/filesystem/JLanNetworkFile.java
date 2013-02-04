package de.easytory.filesystem;

import java.io.IOException;

import org.alfresco.jlan.server.filesys.NetworkFile;

public class JLanNetworkFile extends NetworkFile {

	public JLanNetworkFile(int fid) {
		super(fid);
		// TODO Auto-generated constructor stub
	}

	public JLanNetworkFile(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public JLanNetworkFile(int fid, int did) {
		super(fid, did);
		// TODO Auto-generated constructor stub
	}

	public JLanNetworkFile(int fid, int stid, int did) {
		super(fid, stid, did);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void closeFile() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void flushFile() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void openFile(boolean arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public int readFile(byte[] arg0, int arg1, int arg2, long arg3)
			throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long seekFile(long arg0, int arg1) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void truncateFile(long arg0) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeFile(byte[] arg0, int arg1, int arg2, long arg3)
			throws IOException {
		// TODO Auto-generated method stub

	}

}

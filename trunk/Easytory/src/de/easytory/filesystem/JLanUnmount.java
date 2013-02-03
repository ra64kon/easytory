package de.easytory.filesystem;
import org.alfresco.jlan.app.JLANServer;


public class JLanUnmount {

	public static void main(String[] args) 
	{
		JLANServer.shutdownServer(args);
	}
	

}

package irc;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import jvn.impl.JvnCoordImpl;
import jvn.itf.JvnRemoteCoord;

public class ServerRegistry {

	public static void main(String[] args) {
		/*if (System.getSecurityManager() == null) { 
		 System.setSecurityManager(new java.rmi.RMISecurityManager()); 
		 }*/
		try {
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
		 try { 
			 JvnRemoteCoord coord = new JvnCoordImpl();
			 //JvnRemoteCoord coord_skeleton = (JvnRemoteCoord) UnicastRemoteObject.exportObject(coord, 0);
			 java.rmi.Naming.rebind("JvnCoord", coord);
		 } catch (Exception e) { 
		 e.printStackTrace(); 
		 }
	}

}

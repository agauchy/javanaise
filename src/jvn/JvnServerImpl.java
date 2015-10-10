/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.io.*;

public class JvnServerImpl extends UnicastRemoteObject implements JvnLocalServer, JvnRemoteServer {

	private static final long serialVersionUID = 1L;

	// A JVN server is managed as a singleton
	private static JvnServerImpl js = null;

	private JvnRemoteCoord coord;
	private HashMap<Integer, JvnObject> objects = new HashMap<Integer, JvnObject>();

	/**
	 * Default constructor
	 * 
	 * @throws JvnException
	 **/
	private JvnServerImpl() throws Exception {
		super();
		Registry registry = LocateRegistry.getRegistry("localhost");
		coord = (JvnRemoteCoord) registry.lookup("JvnCoord");
	}

	/**
	 * Static method allowing an application to get a reference to a JVN server
	 * instance
	 * 
	 * @throws JvnException
	 **/
	public static JvnServerImpl jvnGetServer() {
		if (js == null) {
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				return null;
			}
		}
		return js;
	}

	/**
	 * The JVN service is not used anymore
	 * 
	 * @throws JvnException
	 **/
	public void jvnTerminate() throws JvnException {
		try {
			coord.jvnTerminate(this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * creation of a JVN object
	 * 
	 * @param o
	 *            : the JVN object state
	 * @throws JvnException
	 **/
	public JvnObject jvnCreateObject(Serializable o) throws JvnException {
		JvnObjectImpl object;
		try {
			object = new JvnObjectImpl(coord.jvnGetObjectId());
			object.setObject(o);
			return object;
		} catch (RemoteException e) {
			throw new JvnException("Erreur lors de la création de l'objet");
		}
	}

	/**
	 * Associate a symbolic name with a JVN object
	 * 
	 * @param jon
	 *            : the JVN object name
	 * @param jo
	 *            : the JVN object
	 * @throws JvnException
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo) throws JvnException {
		try {
			coord.jvnRegisterObject(jon, jo, this);
			this.objects.put(jo.jvnGetObjectId(), jo);
		} catch (RemoteException e) {
			throw new JvnException("Erreur lors de l'enregistrement de l'objet");
		}
	}

	/**
	 * Provide the reference of a JVN object beeing given its symbolic name
	 * 
	 * @param jon
	 *            : the JVN object name
	 * @return the JVN object
	 * @throws JvnException
	 **/
	public JvnObject jvnLookupObject(String jon) throws JvnException {
		try {
			JvnObject obj =  coord.jvnLookupObject(jon, this);
			if (obj != null)
				this.objects.put(obj.jvnGetObjectId(), obj);
			return obj;
		} catch (RemoteException e) {
			throw new JvnException("Erreur de lookup");
		}
	}

	/**
	 * Get a Read lock on a JVN object
	 * 
	 * @param joi
	 *            : the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnLockRead(int joi) throws JvnException {
		if (JvnCoordImpl.DEBUG) System.out.println("[SERVER] lockRead : " + joi);
		try {
			return coord.jvnLockRead(joi, this);
		} catch (RemoteException e) {
			throw new JvnException("Erreur de lockread");
		}
	}

	/**
	 * Get a Write lock on a JVN object
	 * 
	 * @param joi
	 *            : the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnLockWrite(int joi) throws JvnException {
		try {
			return coord.jvnLockWrite(joi, this);
		} catch (RemoteException e) {
			throw new JvnException("Erreur de lockwrite");
		}
	}

	/**
	 * Invalidate the Read lock of the JVN object identified by id called by the
	 * JvnCoord
	 * 
	 * @param joi
	 *            : the JVN object id
	 * @return void
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public void jvnInvalidateReader(int joi) throws java.rmi.RemoteException, jvn.JvnException {
		this.objects.get(joi).jvnInvalidateReader();
	};

	/**
	 * Invalidate the Write lock of the JVN object identified by id
	 * 
	 * @param joi
	 *            : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException, jvn.JvnException {
		return this.objects.get(joi).jvnInvalidateWriter();
	};

	/**
	 * Reduce the Write lock of the JVN object identified by id
	 * 
	 * @param joi
	 *            : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException, jvn.JvnException {
		return this.objects.get(joi).jvnInvalidateWriterForReader();
	};

}

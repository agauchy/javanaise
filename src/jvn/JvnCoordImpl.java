/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.Serializable;

public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {

	private static final long serialVersionUID = 1L;

	private AtomicInteger future_id;
	
	private HashMap<String, JvnObject> object_name;
	private HashMap<Integer, String> name_id;

	private HashMap<Integer, ArrayList<JvnRemoteServer>> object_readers;
	private HashMap<Integer, JvnRemoteServer> object_writer;

	private HashMap<JvnRemoteServer, ArrayList<JvnObject>> servers_objects;

	/**
	 * Default constructor
	 * 
	 * @throws JvnException
	 **/
	private JvnCoordImpl() throws Exception {
		this.future_id = new AtomicInteger(0);
		this.object_name = new HashMap<String, JvnObject>();
		this.name_id = new HashMap<Integer, String>();
		this.object_readers = new HashMap<Integer, ArrayList<JvnRemoteServer>>();
		this.object_writer = new HashMap<Integer, JvnRemoteServer>();
		this.servers_objects = new HashMap<JvnRemoteServer, ArrayList<JvnObject>>();
	}

	/**
	 * Allocate a NEW JVN object id (usually allocated to a newly created JVN
	 * object)
	 * 
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public int jvnGetObjectId() throws java.rmi.RemoteException, jvn.JvnException {
		return future_id.incrementAndGet();
	}

	/**
	 * Associate a symbolic name with a JVN object
	 * 
	 * @param jon
	 *            : the JVN object name
	 * @param jo
	 *            : the JVN object
	 * @param joi
	 *            : the JVN object identification
	 * @param js
	 *            : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
			throws java.rmi.RemoteException, jvn.JvnException {
		object_name.put(jon, jo);
		name_id.put(jo.jvnGetObjectId(), jon);
		object_writer.put(jo.jvnGetObjectId(), null);
		object_readers.put(jo.jvnGetObjectId(), new ArrayList<JvnRemoteServer>());
		if(servers_objects.get(js) == null) {
			servers_objects.put(js, new ArrayList<JvnObject>());
		}
		servers_objects.get(js).add(jo);
	}

	/**
	 * Get the reference of a JVN object managed by a given JVN server
	 * 
	 * @param jon
	 *            : the JVN object name
	 * @param js
	 *            : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws java.rmi.RemoteException, jvn.JvnException {
		JvnObject object = object_name.get(jon);
		if(servers_objects.get(js) == null) {
			servers_objects.put(js, new ArrayList<JvnObject>());
		}
		if(object == null) {
			return null;
		}
		servers_objects.get(js).add(object);
		return object;
	}

	/**
	 * Get a Read lock on a JVN object managed by a given JVN server
	 * 
	 * @param joi
	 *            : the JVN object identification
	 * @param js
	 *            : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,
	 *             JvnException
	 **/
	public Serializable jvnLockRead(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
		JvnRemoteServer object_w = object_writer.get(joi);
		Serializable new_object_value = null;
		String name = name_id.get(joi);
		if(object_w != null) {
			new_object_value = js.jvnInvalidateWriterForReader(joi);
			object_name.get(name).setSerializable(new_object_value);
		} else {
			new_object_value = object_name.get(name).jvnGetObjectState();
		}
		
		
		
		object_readers.get(joi).add(js);
		
		return new_object_value;
	}

	/**
	 * Get a Write lock on a JVN object managed by a given JVN server
	 * 
	 * @param joi
	 *            : the JVN object identification
	 * @param js
	 *            : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,
	 *             JvnException
	 **/
	public Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
		JvnRemoteServer object_w = object_writer.get(joi);
		Serializable new_object_value = null;
		String name = name_id.get(joi);
		if(object_w != null) {
			new_object_value = js.jvnInvalidateWriterForReader(joi);
			object_name.get(name).setSerializable(new_object_value);
		} else {
			new_object_value = object_name.get(name).jvnGetObjectState();
		}
		
		for(JvnRemoteServer server : object_readers.get(joi)) {
			server.jvnInvalidateReader(joi);
		}
		
		object_writer.put(joi, js);
		
		return new_object_value;
	}

	/**
	 * A JVN server terminates
	 * 
	 * @param js
	 *            : the remote reference of the server
	 * @throws java.rmi.RemoteException,
	 *             JvnException
	 **/
	public void jvnTerminate(JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {
		for(JvnObject jo : servers_objects.get(js)) {
			ArrayList<JvnRemoteServer> object_readers_list = object_readers.get(jo.jvnGetObjectId());
			object_readers_list.remove(js);
			JvnRemoteServer object_w = object_writer.get(jo.jvnGetObjectId());
			if(object_w == js) {
				object_writer.put(jo.jvnGetObjectId(),null);
			}
		}
		
		servers_objects.remove(js);
	}
}

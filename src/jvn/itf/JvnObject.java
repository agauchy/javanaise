/***
 * JAVANAISE API
 * Contact: 
 *
 * Authors: 
 */

package jvn.itf;

import java.io.*;

import jvn.impl.JvnException;

/**
 * Interface of a JVN object. 
 * The serializable property is required in order to be able to transfer 
 * a reference to a JVN object remotely
 */

public interface JvnObject extends Serializable {

	/**
	* Get a Read lock on the object 
	* @throws JvnException
	**/
	public void jvnLockRead()
	throws jvn.impl.JvnException; 

	/**
	* Get a Write lock on the object 
	* @throws JvnException
	**/
	public void jvnLockWrite()
     	throws jvn.impl.JvnException; 

	/**
	* Unlock  the object 
	* @throws JvnException
	**/
	public void jvnUnLock()
	throws jvn.impl.JvnException; 
	
	
	/**
	* Get the object identification
	* @throws JvnException
	**/
	public int jvnGetObjectId()
	throws jvn.impl.JvnException; 
	
	/**
	* Get the object state
	* @throws JvnException
	**/
	public Serializable jvnGetObjectState()
	throws jvn.impl.JvnException; 
	
	
	/**
	* Invalidate the Read lock of the JVN object 
	* @throws JvnException
	**/
  public void jvnInvalidateReader()
	throws jvn.impl.JvnException;
	    
	/**
	* Invalidate the Write lock of the JVN object  
	* @return the current JVN object state
	* @throws JvnException
	**/
  public Serializable jvnInvalidateWriter()
	throws jvn.impl.JvnException;
	
	/**
	* Reduce the Write lock of the JVN object 
	* @return the current JVN object state
	* @throws JvnException
	**/
   public Serializable jvnInvalidateWriterForReader()
	 throws jvn.impl.JvnException;
   
   public void setSerializable(Serializable ser)
     throws jvn.impl.JvnException;
   
   public void setNLState()
   throws jvn.impl.JvnException;
}

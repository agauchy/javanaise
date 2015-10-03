package jvn;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JvnObjectImpl implements JvnObject {

	private static final long serialVersionUID = 1L;
	//private Lock mutex = new ReentrantLock(true);
	private Etat etat = Etat.WLT;
	private int id;
	private Serializable object;

	public JvnObjectImpl(int id) {
		super();
		this.id = id;
	}

	synchronized public void jvnLockRead() throws JvnException {
		//mutex.lock();
		System.out.println("je demande le verrou en lecture");
		JvnServerImpl server;
		switch (this.etat) {
		case NL:
			server = JvnServerImpl.jvnGetServer();
			this.object = server.jvnLockRead(this.jvnGetObjectId());
			this.etat = Etat.RLT;
			break;
		case RLC:
			server = JvnServerImpl.jvnGetServer();
			server.jvnLockRead(this.jvnGetObjectId());
			this.etat = Etat.RLT;
			break;
		case WLC:
			this.etat = Etat.RLT_WLC;
			break;
		default:
			throw new JvnException("[jvnLockRead()] Erreur etat "+this.etat);
		}
	}

	synchronized public void jvnLockWrite() throws JvnException {
		//mutex.lock();
		JvnServerImpl server;
		switch (this.etat) {
		case NL:
			server = JvnServerImpl.jvnGetServer();
			this.object = server.jvnLockWrite(this.jvnGetObjectId());
			this.etat = Etat.WLT;
			break;
		case RLC:
			server = JvnServerImpl.jvnGetServer();
			server.jvnLockWrite(this.jvnGetObjectId());
			this.etat = Etat.WLT;
			break;
		case WLC:
			this.etat = Etat.WLT;
			break;
		default:
			throw new JvnException("[jvnLockWrite()] Erreur etat "+this.etat);
		}
	}

	synchronized public void jvnUnLock() throws JvnException {
		//mutex.tryLock();
		switch (this.etat) {
		case RLT:
			this.etat = Etat.RLC;
			break;
		case WLT:
			this.etat = Etat.WLC;
			break;
		case RLT_WLC:
			this.etat = Etat.WLC;
			break;
		default:
			throw new JvnException("[jvnUnLock()] Erreur etat "+this.etat);
		}

		//mutex.unlock();

	}

	public int jvnGetObjectId() throws JvnException {
		return this.id;
	}

	public Serializable jvnGetObjectState() throws JvnException {
		return object;
	}

	synchronized public void jvnInvalidateReader() throws JvnException {
		//mutex.lock();

		switch (this.etat) {
		case RLC:
			this.etat = Etat.NL;
			break;
		default:
			throw new JvnException("[jvnInvalidateReader()] Erreur etat "+this.etat);
		}

		//mutex.unlock();
	}

	synchronized public Serializable jvnInvalidateWriter() throws JvnException {
		//mutex.lock();
		System.out.println("je me fais jvnInvalidateWriter");
		switch (this.etat) {
		case WLC:
			this.etat = Etat.NL;
			break;
		default:
			throw new JvnException("[jvnInvalidateWriter()] Erreur etat : "+this.etat);
		}

		//mutex.unlock();

		return object;
	}

	synchronized public Serializable jvnInvalidateWriterForReader() throws JvnException {
		//mutex.lock();
		System.out.println("je me fais jvnInvalidateWriterForReader");
		switch (this.etat) {
		case WLC:
			this.etat = Etat.RLC;
			break;
		default:
			throw new JvnException("[jvnInvalidateWriterForReader()] Erreur etat "+this.etat);
		}

		//mutex.unlock();

		return object;
	}

	public void setObject(Serializable o) {
		this.object = o;
	}

	public void setSerializable(Serializable ser) throws JvnException {
		this.object = ser;
	}

	@Override
	public String toString() {
		return "JvnObjectImpl [etat=" + etat + ", id=" + id + ", object=" + object + "]";
	}

	public void setNLState() throws JvnException {
		this.etat = Etat.NL;
	}

	
}

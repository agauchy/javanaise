package jvn;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class JvnObjectImpl implements JvnObject {

	private static final long serialVersionUID = 1L;
	private Lock mutex = new ReentrantLock(true);
	private Etat etat = Etat.WLT;
	private int id;
	private Serializable object;

	public JvnObjectImpl(int id) {
		super();
		this.id = id;
	}

	synchronized public void jvnLockRead() throws JvnException {
		mutex.lock();
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
			throw new JvnException("[jvnLockRead()] Erreur etat");
		}
	}

	synchronized public void jvnLockWrite() throws JvnException {
		mutex.lock();
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
			throw new JvnException("[jvnLockWrite()] Erreur etat");
		}
	}

	synchronized public void jvnUnLock() throws JvnException {
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
			throw new JvnException("[jvnUnLock()] Erreur etat");
		}

		mutex.unlock();
	}

	synchronized public int jvnGetObjectId() throws JvnException {
		return this.id;
	}

	synchronized public Serializable jvnGetObjectState() throws JvnException {
		return object;
	}

	synchronized public void jvnInvalidateReader() throws JvnException {
		mutex.lock();

		switch (this.etat) {
		case RLC:
			this.etat = Etat.NL;
			break;
		default:
			throw new JvnException("[jvnGetObjectState()] Erreur etat");
		}

		mutex.unlock();
	}

	synchronized public Serializable jvnInvalidateWriter() throws JvnException {
		mutex.lock();

		switch (this.etat) {
		case WLC:
			this.etat = Etat.NL;
			break;
		default:
			throw new JvnException("[jvnInvalidateWriter()] Erreur etat");
		}

		mutex.unlock();

		return object;
	}

	synchronized public Serializable jvnInvalidateWriterForReader() throws JvnException {
		mutex.lock();

		switch (this.etat) {
		case WLC:
			this.etat = Etat.RLC;
			break;
		default:
			throw new JvnException("[jvnInvalidateWriterForReader()] Erreur etat");
		}

		mutex.unlock();

		return object;
	}

	synchronized public void setObject(Serializable o) {
		this.object = o;
	}

	public void setSerializable(Serializable ser) throws JvnException {
		this.object = ser;
	}

}

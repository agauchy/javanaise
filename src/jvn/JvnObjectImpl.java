package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject {

	private static final long serialVersionUID = 1L;
	private Etat etat;
	private int id;
	private Serializable object;
	public JvnObjectImpl() {
		super();
		this.etat = Etat.WLT;
	}

	public JvnObjectImpl(int id) {
		super();
		this.etat = Etat.WLT;
		this.id = id;
	}

	public void jvnLockRead() throws JvnException {
		JvnServerImpl server;

		switch (this.etat) {
			case NL:
				server = JvnServerImpl.jvnGetServer();
				this.object = server.jvnLockRead(this.jvnGetObjectId());
				this.etat = Etat.RLT;
				break;
			case RLC:
				this.etat = Etat.RLT;
				break;
			case WLC:
				this.etat = Etat.RLT_WLC;
				break;
			default:
				throw new JvnException("[jvnLockRead()] Erreur etat "+this.etat);
		}
	}

	public void jvnLockWrite() throws JvnException {
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
		
		this.notify();
	}

	public int jvnGetObjectId() throws JvnException {
		return this.id;
	}

	public Serializable jvnGetObjectState() throws JvnException {
		return object;
	}

	public void jvnInvalidateReader() throws JvnException {
		switch (this.etat) {
			case RLT_WLC:
			case RLT:
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			case RLC:
				this.etat = Etat.NL;
				break;
			default:
				throw new JvnException("[jvnInvalidateReader()] Erreur etat "+this.etat);
			}
	}

	public Serializable jvnInvalidateWriter() throws JvnException {
		
		switch (this.etat) {
			case RLT_WLC:
			case WLT:
				try {
					System.out.println("J'attends dans jvnInvalidateWriter");
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			case WLC:
				this.etat = Etat.NL;
				break;
				
			default:
				throw new JvnException("[jvnInvalidateWriter()] Erreur etat : "+this.etat);
		}
		
		return object;
	}

	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		
		switch (this.etat) {
			case RLT_WLC:
			case WLT:
				try {
					System.out.println("J'attends dans jvnInvalidateWriterForReader");
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			case WLC:
				this.etat = Etat.RLC;
				break;
				
			default:
				throw new JvnException("[jvnInvalidateWriterForReader()] Erreur etat "+this.etat);
		}
		
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

package jvn;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import irc.ISentence;
import irc.Sentence;

public class Proxy implements InvocationHandler {

	private JvnObject obj;
	public Proxy(JvnObject obj) {
		this.obj = obj;
		try {
			this.obj.jvnUnLock();
		} catch (JvnException e) {}
	}
	
	public static Object newInstance(Serializable obj, String app) {
		JvnServerImpl js = JvnServerImpl.jvnGetServer();

		JvnObject jo = null;
		try {
			jo = js.jvnLookupObject(app);
			if (jo == null) {
				jo = js.jvnCreateObject(obj);
				js.jvnRegisterObject(app, jo);
				System.out.println(jo);
			}
		} catch (JvnException e) {}
		
		return java.lang.reflect.Proxy.newProxyInstance(
		obj.getClass().getClassLoader(),
		obj.getClass().getInterfaces(),
		new Proxy(jo));
	}
	
	public Object invoke(Object p, Method m, Object[] args) throws Throwable {
		Object result = new Object();
		try {
			result = m.invoke(obj, args);
		} catch (Exception e) {}
		return result;
	}

}

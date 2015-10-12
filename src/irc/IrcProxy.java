/***
 * Irc class : simple implementation of a chat using JAVANAISE
 * Contact: 
 *
 * Authors: 
 */

package irc;

import java.awt.*;
import java.awt.event.*;


import jvn.impl.Proxy;


public class IrcProxy {
	public TextArea	text;
	public TextField data;
	Frame frame;
	ISentence jvnO_sentence;


  /**
  * main method
  * create a JVN object nammed IRC for representing the Chat application
  **/
	public static void main(String argv[]) {
	   try {
		// initialize JVN
		// look up the IRC object in the JVN server
		// if not found, create it, and register it in the JVN server
		
		ISentence jo = (ISentence) Proxy.newInstance(new Sentence(), "IRC");
		//jo = js.jvnCreateObject((Serializable) new Sentence());
		// create the graphical part of the Chat application
		 new IrcProxy(jo);
	   } catch (Exception e) {
		   e.printStackTrace();
	   }
	}

  /**
   * IRC Constructor
   @param jo the JVN object representing the Chat
   **/
	public IrcProxy(ISentence jo) {
		jvnO_sentence = jo;
		frame=new Frame();
		frame.setLayout(new GridLayout(1,1));
		text=new TextArea(10,60);
		text.setEditable(false);
		text.setForeground(Color.red);
		frame.add(text);
		data=new TextField(40);
		frame.add(data);
		Button read_button = new Button("read");
		read_button.addActionListener(new readListenerProxy(this));
		frame.add(read_button);
		Button write_button = new Button("write");
		write_button.addActionListener(new writeListenerProxy(this));
		frame.add(write_button);
		frame.setSize(545,201);
		text.setBackground(Color.black); 
		frame.setVisible(true);
	}
}


 /**
  * Internal class to manage user events (read) on the CHAT application
  **/
 class readListenerProxy implements ActionListener {
	IrcProxy irc;
  
	public readListenerProxy (IrcProxy i) {
		irc = i;
	}
   
 /**
  * Management of user events
  **/
	public void actionPerformed (ActionEvent e) {
		 // lock the object in read mode
		 System.out.println("Objet avant lock read :"+irc.jvnO_sentence);
		//irc.jvnO_sentence.jvnLockRead();
		 System.out.println("Objet apres lock read :"+irc.jvnO_sentence);
		// invoke the method
		Object res = irc.jvnO_sentence.read();
		String s = null;
		if(res instanceof String) {
			s = (String) res;
		}
		// unlock the object
		//irc.jvnO_sentence.jvnUnLock();
		
		// display the read value
		irc.data.setText(s);
		irc.text.append(s+"\n");
	}
}

 /**
  * Internal class to manage user events (write) on the CHAT application
  **/
 class writeListenerProxy implements ActionListener {
	IrcProxy irc;
  
	public writeListenerProxy (IrcProxy i) {
        	irc = i;
	}
  
  /**
    * Management of user events
   **/
	public void actionPerformed (ActionEvent e) {
		// get the value to be written from the buffer
		String s = irc.data.getText();
				
		// lock the object in write mode
		System.out.println("Objet avant lock write :"+irc.jvnO_sentence);
		//irc.jvnO_sentence.jvnLockWrite();
		 System.out.println("Objet apres lock write :"+irc.jvnO_sentence);
		
		// invoke the method
		 irc.jvnO_sentence.write(s);
	}
}




/***
 * Sentence class : used for representing the text exchanged between users
 * during a chat application
 * Contact: 
 *
 * Authors: 
 */
package irc;

import java.io.Serializable;

public class Sentence implements Serializable, ISentence {

	private static final long serialVersionUID = 1L;
	String data;
  
	public Sentence() {
		data = new String("");
	}
	
	public void write(String text) {
		data = text;
	}
	public String read() {
		return data;	
	}

	@Override
	public String toString() {
		return "Sentence [data=" + data + "]";
	}
}
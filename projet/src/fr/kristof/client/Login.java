/**
 * 
 */
package fr.kristof.client;

/**
 * @author master
 *
 */
public class Login {

	private final String user;
	private final String password;
	
	public Login(String user,String password){
		this.user = user;
		this.password = password;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new StringBuilder().append(user).append(":").append(password).toString();
	}
}

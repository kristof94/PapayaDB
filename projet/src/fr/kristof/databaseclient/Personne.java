/**
 * 
 */
package fr.kristof.databaseclient;

/**
 * @author master
 *
 */
public class Personne extends Data{

	/**
	 * @param name
	 */
	public Personne(String name,int age) {
		super(name);
		this.age = age;
	}
	
	public int age ;

}

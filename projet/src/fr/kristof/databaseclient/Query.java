/**
 * 
 */
package fr.kristof.databaseclient;

/**
 * @author master
 *
 */
public class Query {

	private final String nameMethod;
	private final String value;
	
	public Query(String nameMethod,String value){
		this.nameMethod = nameMethod;
		this.value = value;
	}

	public String getNameMethod() {
		return nameMethod;
	}

	public String getValue() {
		return value;
	}
	
	
}

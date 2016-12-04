/**
 * 
 */
package fr.kristof.client;

/**
 * Object which contains Data and what the database must do.
 * @author kristof
 *
 */
public class Query {

	private final String nameMethod;
	private final String value;
	
	/**
	 * Create a Query with method's name and value
	 * @param nameMethod
	 * @param value
	 */
	public Query(String nameMethod,String value){
		this.nameMethod = nameMethod;
		this.value = value;
	}

	/**
	 * @return 
	 */
	public String getNameMethod() {
		return nameMethod;
	}

	/**
	 * @return
	 */
	public String getValue() {
		return value;
	}
	
	
}

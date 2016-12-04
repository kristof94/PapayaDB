/**
 * 
 */
package fr.kristof.client;

/**
 * @author master
 *
 */
public enum HTTPMethod {

	POST("POST"), GET("GET"), PUT("PUT"), DELETE("DELETE");

	private final String method;

	HTTPMethod(String method) {
		this.method = method;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return this.method;
	}
}
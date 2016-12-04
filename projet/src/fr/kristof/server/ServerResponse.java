package fr.kristof.server;

import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

/**
 * This class provides function which could be used as Server response 
 * @author kristof
 *
 */
public class ServerResponse {
	
	/**Send request to client with json data
	 * @param routingContext
	 * @param text
	 */
	public static void responseDatabase(RoutingContext routingContext,String text){
		routingContext.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(text.length()))
		.putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
		.write(text)
		.end();
	}
	
	/**
	 * Send request to client if authentification is incorrect
	 * @param routingContext
	 */
	public static void authentificationError(RoutingContext routingContext){
		sendErrorResponse(routingContext, "Error Authentification");
	}
	
	private static void sendErrorResponse(RoutingContext routingContext,String error){
		routingContext.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(error.length()))
		.putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
		.setStatusCode(404)
		.write(error)
		.end();
	}
}

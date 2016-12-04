package fr.upem.server;

import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

/**
 * @author kristof
 * This class show 
 *
 */
public class ServerResponse {
	
	public static void responseDatabase(RoutingContext routingContext,String text){
		routingContext.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(text.length()))
		.putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
		.write(text)
		.end();
	}
	
	public static void authentificationError(RoutingContext routingContext){
		sendErrorResponse(routingContext, "Error Authentification");
	}
	
	public static void queryError(RoutingContext routingContext){
		sendErrorResponse(routingContext, "Error Bad Query");
	}
	
	public static void requestError(RoutingContext routingContext){
		sendErrorResponse(routingContext, "Error Bad request");
	}
	
	private static void sendErrorResponse(RoutingContext routingContext,String error){
		routingContext.response().putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(error.length()))
		.putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
		.setStatusCode(404)
		.write(error)
		.end();
	}
}

/**
 * 
 */
package fr.upem.server;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.ext.web.RoutingContext;

/**
 * @author master
 *
 */
public class DocumentManager {

	ObjectMapper mapper = new ObjectMapper();

	
	public void selectDocument(RoutingContext routingContext) {
		//HttpServerRequest request = routingContext.request();
		handleSelectDatabaseRequest(routingContext);

	}

	public void insertDocument(RoutingContext routingContext) {
		//HttpServerRequest request = routingContext.request();
		handleInsertDatabaseRequest(routingContext);
	}

	public void deleteDocument(RoutingContext routingContext) {
		handleRemoveDatabaseRequest(routingContext);
	}
	
	private void handleRemoveDatabaseRequest(RoutingContext routingContext){
		routingContext.request().bodyHandler( buffer -> {
				try {
					System.out.println("try to remove document with id "+routingContext.request().getParam("namedoc") +" from bdd "+routingContext.request().getParam("name"));
					ServerResponse.responseDatabase(routingContext, mapper.writeValueAsString("DELETE DOCUMENT SUCCESS"));
				} catch (IOException e) {
					ServerResponse.requestError(routingContext);
				}});
	}
	
	private void handleSelectDatabaseRequest(RoutingContext routingContext){
		routingContext.request().bodyHandler( buffer -> {
				try {
					System.out.println("try to select document with id "+routingContext.request().getParam("namedoc") +" from bdd "+routingContext.request().getParam("name"));
					ServerResponse.responseDatabase(routingContext, mapper.writeValueAsString("SELECT DOCUMENT SUCCESS"));
				} catch (IOException e) {
					ServerResponse.requestError(routingContext);
				}});
	}
	
	private void handleInsertDatabaseRequest(RoutingContext routingContext){
		routingContext.request().bodyHandler( buffer -> {
				try {
					System.out.println("try to insert document in dbb"+routingContext.request().getParam("name"));
					System.out.println(mapper.readValue(buffer.getString(0,buffer.length()), String.class));
					ServerResponse.responseDatabase(routingContext, mapper.writeValueAsString("INSERT DOCUMENT SUCCESS"));
				} catch (IOException e) {
					ServerResponse.requestError(routingContext);
				}});
	}
}

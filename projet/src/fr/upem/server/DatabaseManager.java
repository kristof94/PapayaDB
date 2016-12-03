/**
 * 
 */
package fr.upem.server;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

/**
 * @author master
 *
 */
public class DatabaseManager {

	ObjectMapper mapper = new ObjectMapper();

	public void createDatabase(RoutingContext routingContext) {
		if (!checkAuthentification(routingContext)) {
			return;
		}
		handleCreateDatabaseRequest(routingContext);
	}
		
	public void removeDatabase(RoutingContext routingContext) {
		if (!checkAuthentification(routingContext)) {
			return;
		}
		handleRemoveDatabaseRequest(routingContext);
	}

	public void exportDatabase(RoutingContext routingContext) {
		if (!checkAuthentification(routingContext)) {
			return;
		}
		handleExportDatabaseRequest(routingContext);
	}

	private boolean checkAuthentification(RoutingContext routingContext) {
		HttpServerRequest request = routingContext.request();
		if (Utils.isAuthentified(request)) {
			return true;
		}
		ServerResponse.authentificationError(routingContext);
		return false;
	}

	private void handleCreateDatabaseRequest(RoutingContext routingContext){
		routingContext.request().bodyHandler( buffer -> {
				try {
					System.out.println(mapper.readValue(buffer.getString(0,buffer.length()), String.class));
					ServerResponse.responseDatabase(routingContext, mapper.writeValueAsString("CREATE DATABASE SUCCESS"));
				} catch (IOException e) {
					ServerResponse.requestError(routingContext);
				}});
	}
	
	private void handleRemoveDatabaseRequest(RoutingContext routingContext){
		routingContext.request().bodyHandler( buffer -> {
				try {
					System.out.println("try to remove db with id "+routingContext.request().getParam("name"));
					ServerResponse.responseDatabase(routingContext, mapper.writeValueAsString("DELETE DATABASE SUCCESS"));
				} catch (IOException e) {
					ServerResponse.requestError(routingContext);
				}});
	}
	
	private void handleExportDatabaseRequest(RoutingContext routingContext){
		routingContext.request().bodyHandler( buffer -> {
				try {
					System.out.println("try to export db with id "+routingContext.request().getParam("name"));
					//System.out.println(mapper.readValue(buffer.getString(0,buffer.length()), String.class));
					ServerResponse.responseDatabase(routingContext, mapper.writeValueAsString("EXPORT DATABASE SUCCESS"));
				} catch (IOException e) {
					ServerResponse.requestError(routingContext);
				}});
	}
}

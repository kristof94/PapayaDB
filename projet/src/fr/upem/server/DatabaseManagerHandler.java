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
public class DatabaseManagerHandler implements DataBaseHandler{

	ObjectMapper mapper = new ObjectMapper();
	


	/* (non-Javadoc)
	 * @see fr.upem.server.DataBaseHandler#handleCreateDatabaseRequest(io.vertx.ext.web.RoutingContext)
	 */
	@Override
	public void handleCreateDatabaseRequest(RoutingContext routingContext) {
		routingContext.request().bodyHandler(buffer -> {
			try {
				System.out.println(mapper.readValue(buffer.getString(0, buffer.length()), String.class));
				ServerResponse.responseDatabase(routingContext, mapper.writeValueAsString("CREATE DATABASE SUCCESS"));
			} catch (IOException e) {
				ServerResponse.requestError(routingContext);
			}
		});
	}
	
	@Override
	public void handleDropDatabaseRequest(RoutingContext routingContext) {
		routingContext.request().bodyHandler(buffer -> {
			try {
				System.out.println("try to remove db with id " + routingContext.request().getParam("name"));
				ServerResponse.responseDatabase(routingContext, mapper.writeValueAsString("DELETE DATABASE SUCCESS"));
			} catch (IOException e) {
				ServerResponse.requestError(routingContext);
			}
		});
	}

	@Override
	public void handleExportDatabaseRequest(RoutingContext routingContext) {
		routingContext.request().bodyHandler(buffer -> {
			try {
				System.out.println("try to export db with id " + routingContext.request().getParam("name"));
				// System.out.println(mapper.readValue(buffer.getString(0,buffer.length()),
				// String.class));
				ServerResponse.responseDatabase(routingContext, mapper.writeValueAsString("EXPORT DATABASE SUCCESS"));
			} catch (IOException e) {
				ServerResponse.requestError(routingContext);
			}
		});
	}

	@Override
	public void handleRemoveDatabaseRequest(RoutingContext routingContext) {
		routingContext.request().bodyHandler(buffer -> {
			try {
				System.out.println("try to remove document with id " + routingContext.request().getParam("namedoc")
						+ " from bdd " + routingContext.request().getParam("name"));
				ServerResponse.responseDatabase(routingContext, mapper.writeValueAsString("DELETE DOCUMENT SUCCESS"));
			} catch (IOException e) {
				ServerResponse.requestError(routingContext);
			}
		});
	}
	
	@Override
	public void handleSelectDatabaseRequest(RoutingContext routingContext) {
		routingContext.request().bodyHandler(buffer -> {
			try {
				System.out.println("try to select document with id " + routingContext.request().getParam("namedoc")
						+ " from bdd " + routingContext.request().getParam("name"));
				ServerResponse.responseDatabase(routingContext, mapper.writeValueAsString("SELECT DOCUMENT SUCCESS"));
			} catch (IOException e) {
				ServerResponse.requestError(routingContext);
			}
		});
	}

	@Override
	public void handleInsertDatabaseRequest(RoutingContext routingContext) {
		routingContext.request().bodyHandler(buffer -> {
			try {
				System.out.println("try to insert document in dbb" + routingContext.request().getParam("name"));
				System.out.println(mapper.readValue(buffer.getString(0, buffer.length()), String.class));
				ServerResponse.responseDatabase(routingContext, mapper.writeValueAsString("INSERT DOCUMENT SUCCESS"));
			} catch (IOException e) {
				ServerResponse.requestError(routingContext);
			}
		});
	}



}

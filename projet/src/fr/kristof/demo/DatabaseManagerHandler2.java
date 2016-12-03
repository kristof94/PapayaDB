/**
 * 
 */
package fr.kristof.demo;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.kristof.client.DatabaseClient;
import fr.kristof.client.Login;
import fr.upem.server.DataBaseHandler;
import fr.upem.server.ServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * @author master
 *
 */
public class DatabaseManagerHandler2 implements DataBaseHandler{

	ObjectMapper mapper = new ObjectMapper();
	
	DatabaseClient client = new DatabaseClient();

	public DatabaseManagerHandler2(String ip){
		client.setLogin(new Login("demo","password"));
		client.setSSLWithKeystore("/home/master/Data/workspace_2/DatabaseClient/keystore.jks");
	}
	
	/* (non-Javadoc)
	 * @see fr.upem.server.DataBaseHandler#handleCreateDatabaseRequest(io.vertx.ext.web.RoutingContext)
	 */
	@Override
	public void handleCreateDatabaseRequest(RoutingContext routingContext) {
		routingContext.request().bodyHandler(buffer -> {
			try {
				System.out.println(mapper.readValue(buffer.getString(0, buffer.length()), String.class));
				ServerResponse.responseDatabase(routingContext, mapper.writeValueAsString("CREATE DATABASE WITH API SUCCESS"));
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

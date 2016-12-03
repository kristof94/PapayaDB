/**
 * 
 */
package fr.kristof.demo;

import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.kristof.client.DatabaseClient;
import fr.kristof.client.Login;
import fr.upem.server.DataBaseHandler;
import fr.upem.server.ServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * @author master
 *
 */
public class DatabaseManagerHandlerRest implements DataBaseHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.upem.server.DataBaseHandler#handleCreateDatabaseRequest(io.vertx.ext.
	 * web.RoutingContext)
	 */
	private DatabaseClient client;
	private final String ipHTTPS;
	private final String ipHTTP;
	/**
	 * 
	 */
	public DatabaseManagerHandlerRest() {
		client = new DatabaseClient();
		client.setLogin(new Login("demo","password"));
		client.setSSLWithKeystore("/home/master/Data/workspace_2/DatabaseClient/keystore.jks");
		ipHTTPS ="https://127.0.0.1:8889";
		ipHTTP ="http://127.0.0.1:8888";
	}

	@Override
	public void handleCreateDatabaseRequest(RoutingContext arg0) {
		arg0.request().bodyHandler(buffer -> {
			try {
				String response = client.createDatabase(new String(buffer.getBytes(),"UTF-8"), ipHTTPS+arg0.request().uri());
				ServerResponse.responseDatabase(arg0, response);
			} catch (JsonProcessingException | UnsupportedEncodingException e) {
				ServerResponse.responseDatabase(arg0, e.getMessage());
			}			
		});
	}	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.upem.server.DataBaseHandler#handleDropDatabaseRequest(io.vertx.ext.web
	 * .RoutingContext)
	 */
	@Override
	public void handleDropDatabaseRequest(RoutingContext arg0) {
		arg0.request().bodyHandler(buffer -> {
			String response = client.removeDatabase(ipHTTPS+arg0.request().uri());
			ServerResponse.responseDatabase(arg0,response);
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.upem.server.DataBaseHandler#handleExportDatabaseRequest(io.vertx.ext.
	 * web.RoutingContext)
	 */
	@Override
	public void handleExportDatabaseRequest(RoutingContext arg0) {
		arg0.request().bodyHandler(buffer -> {
			try {
				String response = client.exportDatabase(ipHTTPS+arg0.request().uri());
				ServerResponse.responseDatabase(arg0, response);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.upem.server.DataBaseHandler#handleInsertDatabaseRequest(io.vertx.ext.
	 * web.RoutingContext)
	 */
	@Override
	public void handleInsertDatabaseRequest(RoutingContext arg0) {
		arg0.request().bodyHandler(buffer -> {
			try {
				String response = client.insertDocument(new String(buffer.getBytes(),"UTF-8"),ipHTTP+arg0.request().uri());
				ServerResponse.responseDatabase(arg0, response);
			} catch (JsonProcessingException | UnsupportedEncodingException e) {
				ServerResponse.responseDatabase(arg0, e.getMessage());
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.upem.server.DataBaseHandler#handleRemoveDatabaseRequest(io.vertx.ext.
	 * web.RoutingContext)
	 */
	@Override
	public void handleRemoveDatabaseRequest(RoutingContext arg0) {
		arg0.request().bodyHandler(buffer -> {
			String response =  client.removeDocument(ipHTTP+arg0.request().uri());
			ServerResponse.responseDatabase(arg0, response);
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.upem.server.DataBaseHandler#handleSelectDatabaseRequest(io.vertx.ext.
	 * web.RoutingContext)
	 */
	@Override
	public void handleSelectDatabaseRequest(RoutingContext arg0) {
		arg0.request().bodyHandler(buffer -> {
			String response = client.selectDocument(ipHTTP+arg0.request().uri());
			ServerResponse.responseDatabase(arg0, response);
		});
	}

}

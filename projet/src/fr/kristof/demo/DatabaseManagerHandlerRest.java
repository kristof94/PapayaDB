/**
 * 
 */
package fr.kristof.demo;

import java.io.UnsupportedEncodingException;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.kristof.client.DatabaseClient;
import fr.upem.server.DataBaseHandler;
import fr.upem.server.ServerResponse;
import fr.upem.server.Utils;
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
	public DatabaseManagerHandlerRest(String ipBddHTTP,String ipBddHTTPS) {
		client = new DatabaseClient();
		client.setSSLWithKeystore("/home/master/Data/workspace_2/DatabaseClient/keystore.jks");
		this.ipHTTPS = ipBddHTTPS;
		this.ipHTTP = ipBddHTTP;
	}

	@Override
	public void handleCreateDatabaseRequest(RoutingContext arg0) {		
		arg0.request().bodyHandler( buffer -> {
			try {
				String response = client.createDatabase(new String(buffer.getBytes(),"UTF-8"), ipHTTPS+arg0.request().uri(),Utils.getAuthentification(arg0.request()));
				ServerResponse.responseDatabase(arg0, response);
			} catch (JsonProcessingException | UnsupportedEncodingException e) {
				ServerResponse.responseDatabase(arg0, e.getMessage());
			}});
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
			String response = client.removeDatabase(ipHTTPS+arg0.request().uri(),Utils.getAuthentification(arg0.request()));
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
				String response = client.exportDatabase(ipHTTPS+arg0.request().uri(),Utils.getAuthentification(arg0.request()));
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
	public void handleInsertDocumentDatabaseRequest(RoutingContext arg0) {
		arg0.request().bodyHandler(buffer -> {
			try {
				String response = client.insertDocument(new String(buffer.getBytes(),"UTF-8"),ipHTTPS+arg0.request().uri(),Utils.getAuthentification(arg0.request()));
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
	public void handleRemoveDocumentFromDatabaseRequest(RoutingContext arg0) {
		arg0.request().bodyHandler(buffer -> {
			String response =  client.removeDocument(ipHTTPS+arg0.request().uri(),Utils.getAuthentification(arg0.request()));
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
	public void handleSelectDocumentFromDatabaseRequest(RoutingContext arg0) {
		arg0.request().bodyHandler(buffer -> {
			String response = client.selectDocument(ipHTTP+arg0.request().uri());
			ServerResponse.responseDatabase(arg0, response);
		});
	}

}
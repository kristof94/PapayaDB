/**
 * 
 */
package fr.kristof.demo;

import fr.upem.server.DataBaseHandler;
import fr.upem.server.ServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * @author master
 *
 */
public class DatabaseManagerHandlerDemo implements DataBaseHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.upem.server.DataBaseHandler#handleCreateDatabaseRequest(io.vertx.ext.
	 * web.RoutingContext)
	 */
	@Override
	public void handleCreateDatabaseRequest(RoutingContext arg0) {
		arg0.request().bodyHandler(buffer -> {
			ServerResponse.responseDatabase(arg0, "handleCreateDatabaseRequest");
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
			ServerResponse.responseDatabase(arg0, "handleDropDatabaseRequest");
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
			ServerResponse.responseDatabase(arg0, "handleExportDatabaseRequest");
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
			ServerResponse.responseDatabase(arg0, "handleInsertDatabaseRequest");
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
			ServerResponse.responseDatabase(arg0, "handleRemoveDatabaseRequest");
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
			ServerResponse.responseDatabase(arg0, "handleSelectDatabaseRequest");
		});
	}

}

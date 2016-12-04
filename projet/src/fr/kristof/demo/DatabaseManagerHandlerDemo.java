/**
 * 
 */
package fr.kristof.demo;

import fr.upem.server.DataBaseHandler;
import fr.upem.server.ServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * @author kristof
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
			ServerResponse.responseDatabase(arg0, buffer.toString());
		});	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.upem.server.DataBaseHandler#handleDropDatabaseRequest(io.vertx.ext.web
	 * .RoutingContext)
	 */
	@Override
	public void handleDropDatabaseRequest(RoutingContext arg0) {
		ServerResponse.responseDatabase(arg0,  arg0.request().getParam("name"));

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
			ServerResponse.responseDatabase(arg0,  arg0.request().getParam("name"));
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
			ServerResponse.responseDatabase(arg0, buffer.toString());
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
			ServerResponse.responseDatabase(arg0, arg0.request().getParam("namedoc"));
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
		ServerResponse.responseDatabase(arg0, arg0.request().getParam("name"));
	}

}

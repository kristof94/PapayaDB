/**
 * 
 */
package fr.upem.server;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

/**
 * @author master
 *
 */
public interface DataBaseHandler {

	default boolean checkAuthentification(RoutingContext routingContext) {
		HttpServerRequest request = routingContext.request();
		if (Utils.isAuthentified(request)) {
			return true;
		}
		ServerResponse.authentificationError(routingContext);
		return false;
	}
	
	public default void createDatabase(RoutingContext routingContext) {
		if (!checkAuthentification(routingContext)) {
			return;
		}
		handleCreateDatabaseRequest(routingContext);
	}

	public default void removeDatabase(RoutingContext routingContext) {
		if (!checkAuthentification(routingContext)) {
			return;
		}
		handleDropDatabaseRequest(routingContext);
	}

	public default void exportDatabase(RoutingContext routingContext) {
		if (!checkAuthentification(routingContext)) {
			return;
		}
		handleExportDatabaseRequest(routingContext);
	}

	public default void selectDocument(RoutingContext routingContext) {
		handleSelectDocumentFromDatabaseRequest(routingContext);

	}

	public default void insertDocument(RoutingContext routingContext) {
		if (!checkAuthentification(routingContext)) {
			return;
		}
		handleInsertDocumentDatabaseRequest(routingContext);
	}

	public default void deleteDocument(RoutingContext routingContext) {
		if (!checkAuthentification(routingContext)) {
			return;
		}
		handleRemoveDocumentFromDatabaseRequest(routingContext);
	}

	public void handleCreateDatabaseRequest(RoutingContext routingContext);

	public void handleDropDatabaseRequest(RoutingContext routingContext);

	public void handleExportDatabaseRequest(RoutingContext routingContext);

	public void handleRemoveDocumentFromDatabaseRequest(RoutingContext routingContext);

	public void handleSelectDocumentFromDatabaseRequest(RoutingContext routingContext);

	public void handleInsertDocumentDatabaseRequest(RoutingContext routingContext);

}

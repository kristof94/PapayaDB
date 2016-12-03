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

	public default boolean checkAuthentification(RoutingContext routingContext) {
		HttpServerRequest request = routingContext.request();
		if (Utils.isAuthentified(request)) {
			return true;
		}
		ServerResponse.authentificationError(routingContext);
		return false;
	}

	/* Document */
	public default void selectDocument(RoutingContext routingContext) {
		// HttpServerRequest request = routingContext.request();
		handleSelectDatabaseRequest(routingContext);

	}

	public default void insertDocument(RoutingContext routingContext) {
		// HttpServerRequest request = routingContext.request();
		handleInsertDatabaseRequest(routingContext);
	}

	public default void deleteDocument(RoutingContext routingContext) {
		handleRemoveDatabaseRequest(routingContext);
	}

	public void handleCreateDatabaseRequest(RoutingContext routingContext);

	public void handleDropDatabaseRequest(RoutingContext routingContext);

	public void handleExportDatabaseRequest(RoutingContext routingContext);

	public void handleRemoveDatabaseRequest(RoutingContext routingContext);

	public void handleSelectDatabaseRequest(RoutingContext routingContext);

	public void handleInsertDatabaseRequest(RoutingContext routingContext);

}

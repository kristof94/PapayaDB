/**
 * 
 */
package fr.kristof.server;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

/**
 * @author kristof
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
	
	default void createDatabase(RoutingContext routingContext) {
		if (!checkAuthentification(routingContext)) {
			return;
		}
		handleCreateDatabaseRequest(routingContext);
	}

	default void removeDatabase(RoutingContext routingContext) {
		if (!checkAuthentification(routingContext)) {
			return;
		}
		handleDropDatabaseRequest(routingContext);
	}

	default void exportDatabase(RoutingContext routingContext) {
		if (!checkAuthentification(routingContext)) {
			return;
		}
		handleExportDatabaseRequest(routingContext);
	}

	default void selectDocument(RoutingContext routingContext) {
		handleSelectDocumentFromDatabaseRequest(routingContext);

	}

	default void insertDocument(RoutingContext routingContext) {
		if (!checkAuthentification(routingContext)) {
			return;
		}
		handleInsertDocumentDatabaseRequest(routingContext);
	}

	default void deleteDocument(RoutingContext routingContext) {
		if (!checkAuthentification(routingContext)) {
			return;
		}
		handleRemoveDocumentFromDatabaseRequest(routingContext);
	}

	/**
	 * 
	 * @param routingContext
	 */
	public void handleCreateDatabaseRequest(RoutingContext routingContext);

	/**
	 * @param routingContext
	 */
	public void handleDropDatabaseRequest(RoutingContext routingContext);

	/**
	 * @param routingContext
	 */
	public void handleExportDatabaseRequest(RoutingContext routingContext);

	/**
	 * @param routingContext
	 */
	public void handleRemoveDocumentFromDatabaseRequest(RoutingContext routingContext);

	/**
	 * @param routingContext
	 */
	public void handleSelectDocumentFromDatabaseRequest(RoutingContext routingContext);

	/**
	 * @param routingContext
	 */
	public void handleInsertDocumentDatabaseRequest(RoutingContext routingContext);

}

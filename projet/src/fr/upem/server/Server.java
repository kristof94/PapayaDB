package fr.upem.server;

import java.util.HashMap;
import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * This class describes the implementation of a custom vertx
 * server
 * @author kristof 
 *
 */
public class Server extends AbstractVerticle {

	private final int portHTTPS;
	private final int portHTTP;
	private final String pathManageBdd = "/api/database/:name";
	private final String pathBdd = "/api/database";
	private final String pathInsertDoc = "/api/database/:name";
	private final String pathManageDoc = "/api/database/:name/:namedoc";

	private final DataBaseHandler databaseManager;


	/**
	 * Create Server implementing vertx server
	 * @param portHTTP
	 * @param portHTTPS
	 * @param dataBaseHandler
	 * A dataBaseHandler used to indicate what the server must do
	 */
	public Server(int portHTTP, int portHTTPS,DataBaseHandler dataBaseHandler) {
		this.portHTTPS = portHTTPS;
		this.portHTTP = portHTTP;
		this.databaseManager = dataBaseHandler;
	}

	private Map<String, Route> createRouteHTTP(Router routerHTTP) {
		HashMap<String, Route> routeList = new HashMap<>();
		routeList.put("select", routerHTTP.route(HttpMethod.GET, pathManageDoc));
		return routeList;
	}

	private Map<String, Route> createRouteHTTPS(Router routerHTTPS) {
		HashMap<String, Route> routeList = new HashMap<>();
		routeList.put("create", routerHTTPS.route(HttpMethod.POST, pathBdd));
		routeList.put("delete", routerHTTPS.route(HttpMethod.DELETE, pathManageBdd));
		routeList.put("insert", routerHTTPS.route(HttpMethod.PUT, pathInsertDoc));

		routeList.put("export", routerHTTPS.route(HttpMethod.GET, pathManageBdd));
		routeList.put("remove", routerHTTPS.route(HttpMethod.DELETE, pathManageDoc));

		return routeList;
	}

	private Map<String, Handler<RoutingContext>> createMapHandlerDatabase() {
		HashMap<String, Handler<RoutingContext>> map = new HashMap<>();
		map.put("create", databaseManager::createDatabase);
		map.put("delete", databaseManager::removeDatabase);
		map.put("insert", databaseManager::insertDocument);

		map.put("export", databaseManager::exportDatabase);
		map.put("remove", databaseManager::deleteDocument);
		return map;
	}

	private Map<String, Handler<RoutingContext>> createMapHandlerDocument() {
		HashMap<String, Handler<RoutingContext>> map = new HashMap<>();
		map.put("select", databaseManager::selectDocument);
		return map;
	}

	private void joinRouteHandler(Map<String, Handler<RoutingContext>> mapHandler, Map<String, Route> mapRoute) {
		mapRoute.forEach((k, v) -> v.handler(mapHandler.get(k)));
	}

	private HttpServerOptions createHttpSServerOptions() {
		return new HttpServerOptions().setSsl(true)
				.setKeyStoreOptions(new JksOptions().setPath("keystore.jks").setPassword("direct11"));
	}

	private void prepareRoute(Router routerHTTP,Router routerHTTPS){
		Map<String, Route> mapRouteHTTP = createRouteHTTP(routerHTTP);
		Map<String, Route> mapRouteHTTPS = createRouteHTTPS(routerHTTPS);
		Map<String, Handler<RoutingContext>> mapDatabaseHandler = createMapHandlerDatabase();
		Map<String, Handler<RoutingContext>> mapDocumentHandler = createMapHandlerDocument();
		joinRouteHandler(mapDocumentHandler, mapRouteHTTP);
		joinRouteHandler(mapDatabaseHandler, mapRouteHTTPS);
	}
	
	
	/* (non-Javadoc)
	 * @see io.vertx.core.AbstractVerticle#start()
	 */
	@Override
	public void start() throws Exception {
		Router routerHTTP = Router.router(vertx);
		Router routerHTTPS = Router.router(vertx);
		prepareRoute(routerHTTP, routerHTTPS);
		routerHTTP.route().handler(BodyHandler.create());
		routerHTTPS.route().handler(BodyHandler.create());
		vertx.createHttpServer(createHttpSServerOptions()).requestHandler(routerHTTPS::accept).listen(portHTTPS);
		vertx.createHttpServer().requestHandler(routerHTTP::accept).listen(portHTTP);
	}
}

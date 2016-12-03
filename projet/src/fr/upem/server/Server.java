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
 * @author kristof This class describes the implementation of a custom vertx
 *         server
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
	 * 
	 * @param port
	 */
	public Server(int portHTTP, int portHTTPS,DataBaseHandler dataBaseHandler) {
		this.portHTTPS = portHTTPS;
		this.portHTTP = portHTTP;
		this.databaseManager = dataBaseHandler;
	}

	private Map<String, Route> createRouteHTTP(Router routerHTTP) {
		HashMap<String, Route> routeList = new HashMap<>();
		routeList.put("insert", routerHTTP.route(HttpMethod.PUT, pathInsertDoc));
		routeList.put("select", routerHTTP.route(HttpMethod.GET, pathManageDoc));
		routeList.put("delete", routerHTTP.route(HttpMethod.DELETE, pathManageDoc));
		return routeList;
	}

	private Map<String, Route> createRouteHTTPS(Router routerHTTPS) {
		HashMap<String, Route> routeList = new HashMap<>();
		routeList.put("create", routerHTTPS.route(HttpMethod.POST, pathBdd));
		routeList.put("delete", routerHTTPS.route(HttpMethod.DELETE, pathManageBdd));
		routeList.put("export", routerHTTPS.route(HttpMethod.GET, pathManageBdd));
		return routeList;
	}

	private Map<String, Handler<RoutingContext>> createMapHandlerDatabase() {
		HashMap<String, Handler<RoutingContext>> map = new HashMap<>();
		map.put("create", databaseManager::createDatabase);
		map.put("delete", databaseManager::removeDatabase);
		map.put("export", databaseManager::exportDatabase);
		return map;
	}

	private Map<String, Handler<RoutingContext>> createMapHandlerDocument() {
		HashMap<String, Handler<RoutingContext>> map = new HashMap<>();
		map.put("insert", databaseManager::insertDocument);
		map.put("delete", databaseManager::deleteDocument);
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

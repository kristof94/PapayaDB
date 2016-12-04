/**
 * 
 */
package fr.kristof.demo;

import fr.kristof.server.Server;
import io.vertx.core.Vertx;

/**
 * @author kristof
 *
 */
public class ServerRest {
	public static void main(String[] args) {
		System.out.println("Launch REST Server");
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new Server(8080, 8090,
				new DatabaseManagerHandlerRest("http://127.0.0.1:8888","https://127.0.0.1:8889")));
	}
}

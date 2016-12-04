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
public class ServerDatabase {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Launch Database Server");
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new Server(8888,8889, new DatabaseManagerHandlerDemo()));
	}

}

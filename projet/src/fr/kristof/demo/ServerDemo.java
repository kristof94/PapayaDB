/**
 * 
 */
package fr.kristof.demo;

import fr.upem.server.Server;
import io.vertx.core.Vertx;

/**
 * @author master
 *
 */
public class ServerDemo {

	/**
	 * @param args
	 */
	
	public static void main(String[] argv){
		System.out.println("Launch WEB Server");
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new Server(8080,8090, new DatabaseManagerHandler2("127.0.0.1")));
	}

}

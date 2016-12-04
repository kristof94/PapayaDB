package fr.kristof.server;

import java.util.Base64;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;

/**
 * This class provides functions for authentification
 * @author kristof
 *
 */
public class Utils {
	
	/**
	 * This method decode a base64 string and return the human transcription
	 * @param string
	 * @return
	 *
	 */
	public static String decodeBase64(String string) {
		byte[] byteArray = Base64.getDecoder().decode(string.getBytes());
		return new String(byteArray);
	}
	
	/**
	 * @param request
	 * @return
	 * Return true if the request has the correct login
	 */
	public static boolean isAuthentified(HttpServerRequest request) {
		String authorization = getAuthentification(request);
		return checkAuthorization(authorization);
	}
	
	private static boolean checkAuthorization(String authorization){
		if (authorization != null && authorization.substring(0, 6).equals("Basic ")) {
			String identifiant = authorization.substring(6);
			String login[] = Utils.decodeBase64(identifiant).split(":");
			return (login[0].equals("admin") & login[1].equals("root"));
		}
		return false;
	}
	
	/**
	 * @param request
	 * @return
	 * Return the authorization field of a HttpServerRequest
	 */
	public static String getAuthentification(HttpServerRequest request) {
		return request.headers().get(HttpHeaders.AUTHORIZATION);
	}
}

package fr.upem.server;

import java.util.Base64;

import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;

public class Utils {
	
	public static String decodeBase64(String string) {
		byte[] byteArray = Base64.getDecoder().decode(string.getBytes());
		return new String(byteArray);
	}
	
	public static String encodeBase64(String string) {
		byte[] bytesEncoded = Base64.getEncoder().encode(string.getBytes());
		return new String(bytesEncoded);
	}
	
	public static boolean isAuthentified(HttpServerRequest request) {
		String authorization = request.headers().get(HttpHeaders.AUTHORIZATION);
		if (authorization != null && authorization.substring(0, 6).equals("Basic ")) {
			String identifiant = authorization.substring(6);
			String login[] = Utils.decodeBase64(identifiant).split(":");
			System.out.println("User : "+login[0]);			
			System.out.println("Password : "+login[1]);
			return true;
		}
		return false;
	}
}

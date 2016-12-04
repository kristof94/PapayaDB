/**
 * 
 */
package fr.kristof.demo;

import java.util.Base64;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.kristof.client.Data;
import fr.kristof.client.DatabaseClient;
import fr.kristof.client.Login;

/**
 * @author master
 *
 */
public class ClientDemo {

	private static String encodeBase64(String string) {
		byte[] bytesEncoded = Base64.getEncoder().encode(string.getBytes());
		return new String(bytesEncoded);
	}

	private static String createBasicAuthentification(Login login){
		return  "Basic "+encodeBase64(login.toString());
	}

	public static void main(String[] args) {		
		DatabaseClient client = new DatabaseClient();
		String auth = createBasicAuthentification(new Login("admin","root"));
		client.setSSLWithKeystore("/home/master/Data/workspace_2/DatabaseClient/keystore.jks");
		try {
			System.out.println(client.createDatabase(new Data("nomdeBDD1"), "https://127.0.0.1:8090/api/database",auth));
			System.out.println(client.exportDatabase("https://127.0.0.1:8090/api/database/toto",auth));
			System.out.println(client.removeDatabase("https://127.0.0.1:8090/api/database/test",auth));
			System.out.println(client.insertDocument(new Data("doc2"), "https://127.0.0.1:8090/api/database/test2",auth));
			System.out.println(client.removeDocument("https://127.0.0.1:8090/api/database/test2/doc3",auth));
			System.out.println(client.selectDocument("http://127.0.0.1:8080/api/database/test2/doc3"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}

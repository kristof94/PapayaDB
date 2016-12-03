/**
 * 
 */
package fr.kristof.demo;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.kristof.client.Data;
import fr.kristof.client.DatabaseClient;
import fr.kristof.client.Login;
import fr.kristof.client.Personne;

/**
 * @author master
 *
 */
public class ClientDemo {
	
	public static void main(String[] args) {		
		DatabaseClient client = new DatabaseClient();
		client.setLogin(new Login("admin","root"));
		client.setSSLWithKeystore("/home/master/Data/workspace_2/DatabaseClient/keystore.jks");
		try {
			System.out.println(client.createDatabase(new Data("nomdeBDD1"), "https://127.0.0.1:8090/api/database"));
			System.out.println(client.exportDatabase("https://127.0.0.1:8090/api/database/toto"));
			System.out.println(client.removeDatabase("https://127.0.0.1:8090/api/database/test"));
			
			System.out.println(client.insertDocument(new Personne("doc2",23), "http://127.0.0.1:8080/api/database/test2"));
			System.out.println(client.removeDocument("http://127.0.0.1:8080/api/database/test2/doc3"));
			System.out.println(client.selectDocument("http://127.0.0.1:8080/api/database/test2/doc3"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}

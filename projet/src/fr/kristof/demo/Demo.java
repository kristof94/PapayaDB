/**
 * 
 */
package fr.kristof.demo;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.kristof.databaseclient.Data;
import fr.kristof.databaseclient.DatabaseClient;
import fr.kristof.databaseclient.Login;
import fr.kristof.databaseclient.Personne;

/**
 * @author master
 *
 */
public class Demo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		DatabaseClient client = new DatabaseClient();
		client.setLogin(new Login("demo","password"));
		client.setSSLWithKeystore("/home/master/Data/workspace_2/DatabaseClient/keystore.jks");
		try {
			client.createDatabase(new Data("nomdeBDD1"), "https://127.0.0.1:8090/api/database");
			client.exportDatabase("https://127.0.0.1:8090/api/database/toto");
			client.removeDatabase("https://127.0.0.1:8090/api/database/test");
			
			client.insertDocument(new Personne("doc2",23), "http://127.0.0.1:8080/api/database/test2");
			client.removeDocument("http://127.0.0.1:8080/api/database/test2/doc3");
			client.selectDocument("http://127.0.0.1:8080/api/database/test2/doc3");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}

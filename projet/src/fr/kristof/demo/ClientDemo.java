/**
 * 
 */
package fr.kristof.demo;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.kristof.client.Data;
import fr.kristof.client.DatabaseClient;
import fr.kristof.client.Login;

/**
 * @author kristof
 *
 */
public class ClientDemo {
		
	public static void select(DatabaseClient client,String url){
		long start = System.currentTimeMillis();
		System.out.println(client.selectDocument(url));
		long end = System.currentTimeMillis();
		long time = (end - start);
		System.out.println(time +" ms.");
	}	
	
	public static void main(String[] args) {		
		DatabaseClient client = new DatabaseClient();
		String auth = DatabaseClient.createBasicAuthentification(new Login("admin","root"));
		client.setSSLWithKeystore("/home/master/Data/workspace_2/DatabaseClient/keystore.jks","direct11");
		try {
			System.out.println(client.createDatabase(new Data("nomdeBDD1"), "https://127.0.0.1:8090/api/database",auth));
			System.out.println(client.exportDatabase("https://127.0.0.1:8090/api/database/toto",auth));
			System.out.println(client.removeDatabase("https://127.0.0.1:8090/api/database/test",auth));
			System.out.println(client.insertDocument(new Data("doc2"), "https://127.0.0.1:8090/api/database/dbname",auth));
			System.out.println(client.removeDocument("https://127.0.0.1:8090/api/database/test2/doc3",auth));
			select(client,"http://127.0.0.1:8080/api/database/db/value=2&date=8");
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}

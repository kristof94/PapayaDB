/**
 * 
 */
package fr.kristof.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * API used for create client. It provides HTTPS and HTTP methods.
 * @author kristof
 * 
 */
public class DatabaseClient {

	private final ObjectMapper mapper;

	public DatabaseClient() {
		mapper = new ObjectMapper();
	}

	private static String encodeBase64(String string) {
		byte[] bytesEncoded = Base64.getEncoder().encode(string.getBytes());
		return new String(bytesEncoded);
	}

	
	/**
	 * @param login
	 * Login with user and password 
	 * @return
	 * Return a string containing header for basic authentification
	 */
	public static String createBasicAuthentification(Login login){
		return  "Basic "+encodeBase64(login.toString());
	}
	
	
	/**
	 * @param keystorePath
	 * The path of your keystore.jks
	 * @param password
	 * The password of your keystore.jks
	 */
	public void setSSLWithKeystore(String keystorePath,String password) {
		System.setProperty("javax.net.ssl.keyStorePassword", password);
		System.setProperty("javax.net.ssl.trustStore", keystorePath);
		System.setProperty("javax.net.ssl.keyStore", keystorePath);
		System.setProperty("javax.net.ssl.trustStoreType", "jks");
	}

	/**
	 * Create database
	 * @param data
	 * Data which is sent
	 * @param url
	 * The target url
	 * @param auth
	 * Represents the authentification field
	 * @return
	 * The response sent by the server
	 * @throws JsonProcessingException
	 */
	public String createDatabase(Data data, String url,String auth) throws JsonProcessingException {
		String json = mapper.writeValueAsString(data);
		Query query = new Query("createDatabase", json);
		return sendSSLQuery(mapper.writeValueAsString(query), url, HTTPMethod.POST,auth);
	}
	
	/**
	 * @param data
	 * Data ins string already in format json which is sent
	 * @param url
	 * The target url
	 * @param auth
	 * Represents the authentification field
	 * @return
	 * The response sent by the server
	 * @throws JsonProcessingException
	 */
	public String createDatabase(String data, String url,String auth) throws JsonProcessingException {
		return sendSSLQuery(data, url, HTTPMethod.POST,auth);
	}

	/**
	 * Remove database
	 * @param url
	 * The target url
	 * @param auth
	 * Represents the authentification field
	 * @return
	 * The response sent by the server
	 */
	public String removeDatabase(String url,String auth){
		return sendSSLQuery(null, url, HTTPMethod.DELETE,auth);
	}

	/**
	 * Export database
	 * @param url
	 * The target url
	 * @param auth
	 * Represents the authentification field
	 * @return
	 * The response sent by the server
	 * @throws JsonProcessingException
	 */
	public String exportDatabase(String url,String auth) throws JsonProcessingException {
		return sendSSLQuery(null, url, HTTPMethod.GET,auth);
	}

	/**
	 * Remove document
	 * @param url
	 * The target url
	 * @param auth
	 * Represents the authentification field
	 * @return
	 * The response sent by the server
	 */
	public String removeDocument(String url,String auth) {
		 return sendSSLQuery(null, url, HTTPMethod.DELETE,auth);
	}

	/**
	 * Insert document
	 * @param data
	 * Data which is sent
	 * @param url
	 * The target url
	 * @param auth
	 * Represents the authentification field
	 * @return
	 * The response sent by the server
	 * @throws JsonProcessingException
	 */
	public String insertDocument(Data data, String url,String auth) throws JsonProcessingException {
		String json = mapper.writeValueAsString(data);
		Query query = new Query("insertDocument", json);
		return sendSSLQuery(mapper.writeValueAsString(query), url, HTTPMethod.PUT,auth);
	}
	
	/**
	 * Insert document
	 * @param data
	 * Data ins string already in format json which is sent
	 * @param url
	 * The target url
	 * @param auth
	 * Represents the authentification field
	 * @return
	 * The response sent by the server
	 * @throws JsonProcessingException
	 */
	public String insertDocument(String data, String url,String auth) throws JsonProcessingException {
		return sendSSLQuery(data, url, HTTPMethod.PUT,auth);
	}

	/**
	 * Select document
	 * @param url
	 * The target url
	 * @return
	 */
	public String selectDocument(String url) {
		return sendQuery(null, url, HTTPMethod.GET);
	}

	private String sendSSLQuery(String query, String url, HTTPMethod method,String auth) {
		try {
			return manageSSLQuery(query, url, method,auth).collect(Collectors.joining());
		} catch (IOException e) {
			return e.getMessage();
		}
	}
	
	private String sendQuery(String query, String url, HTTPMethod method) {
		try {
			return manageQuery(query, url, method).collect(Collectors.joining());
		} catch (IOException e) {
			return e.getMessage();
		}
	}
	
	private Stream<String> manageQuery(String query, String url, HTTPMethod method) throws IOException {
		HttpURLConnection connection = createHTTPQuery(new URL(url), method.toString());
		writeQuerytoHttpURLConnection(query, connection);
		return getRequest(connection);
	}

	private void writeQuerytoHttpURLConnection(String query, HttpURLConnection connection) throws IOException {
		if (query != null) {
			connection.getOutputStream().write(query.getBytes());
			connection.getOutputStream().flush();
		}
	}

	private Stream<String> manageSSLQuery(String query, String url, HTTPMethod method,String auth) throws IOException {
		HttpsURLConnection connection = createHTTPSQuery(new URL(url), method.toString(),auth);
		writeQuerytoHttpURLConnection(query, connection);
		return getRequest(connection);
	}

	private Stream<String> getRequest(HttpURLConnection connection) throws IOException {		
		try(InputStream in = connection.getInputStream()){
			Stream<String> stream = Stream.of(new String(in.readAllBytes()));
			in.close();
			return stream;
		}
	}

	private HttpsURLConnection createHTTPSQuery(URL url, String method,String basicAuth) throws IOException {
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		prepareHTTPUrl(connection, method);
		connection.setRequestProperty("Authorization", basicAuth);
		return connection;
	}

	private HttpURLConnection createHTTPQuery(URL url, String method) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		prepareHTTPUrl(connection, method);
		return connection;
	}

	private void prepareHTTPUrl(HttpURLConnection connection, String method) throws ProtocolException {
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod(method);
		connection.setDoOutput(true);
	}
}

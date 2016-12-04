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
 * @author master
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

	public static String createBasicAuthentification(Login login){
		return  "Basic "+encodeBase64(login.toString());
	}
	
	
	public void setSSLWithKeystore(String keystorePath) {
		System.setProperty("javax.net.ssl.keyStorePassword", "direct11");
		System.setProperty("javax.net.ssl.trustStore", keystorePath);
		System.setProperty("javax.net.ssl.keyStore", keystorePath);
		System.setProperty("javax.net.ssl.trustStoreType", "jks");
	}

	public String createDatabase(Data data, String url,String auth) throws JsonProcessingException {
		String json = mapper.writeValueAsString(data);
		Query query = new Query("createDatabase", json);
		return sendSSLQuery(mapper.writeValueAsString(query), url, HTTPMethod.POST,auth);
	}
	
	public String createDatabase(String data, String url,String auth) throws JsonProcessingException {
		return sendSSLQuery(data, url, HTTPMethod.POST,auth);
	}

	public String removeDatabase(String url,String auth){
		return sendSSLQuery(null, url, HTTPMethod.DELETE,auth);
	}

	public String exportDatabase(String url,String auth) throws JsonProcessingException {
		return sendSSLQuery(null, url, HTTPMethod.GET,auth);
	}

	public String removeDocument(String url,String auth) {
		 return sendSSLQuery(null, url, HTTPMethod.DELETE,auth);
	}

	public String insertDocument(Data data, String url,String auth) throws JsonProcessingException {
		String json = mapper.writeValueAsString(data);
		Query query = new Query("insertDocument", json);
		return sendSSLQuery(mapper.writeValueAsString(query), url, HTTPMethod.PUT,auth);
	}
	
	public String insertDocument(String data, String url,String auth) throws JsonProcessingException {
		return sendSSLQuery(data, url, HTTPMethod.PUT,auth);
	}

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
		if (query != null) {
			connection.getOutputStream().write(query.getBytes());
			connection.getOutputStream().flush();
		}
		return getRequest(connection);
	}

	private Stream<String> manageSSLQuery(String query, String url, HTTPMethod method,String auth) throws IOException {
		HttpsURLConnection connection = createHTTPSQuery(new URL(url), method.toString(),auth);
		if (query != null) {
			connection.getOutputStream().write(query.getBytes());
			connection.getOutputStream().flush();
		}
		return getRequest(connection);
	}

	private Stream<String> getRequest(HttpURLConnection connection) throws IOException {
		InputStream in = connection.getInputStream();
		return Stream.of(new String(in.readAllBytes()));
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

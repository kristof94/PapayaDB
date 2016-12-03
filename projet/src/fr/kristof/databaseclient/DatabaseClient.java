/**
 * 
 */
package fr.kristof.databaseclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.Objects;
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
	private Login login;

	public void setLogin(Login login) {
		this.login = login;
	}

	public DatabaseClient() {
		mapper = new ObjectMapper();
	}

	public void setSSLWithKeystore(String keystorePath) {
		System.setProperty("javax.net.ssl.keyStorePassword", "direct11");
		System.setProperty("javax.net.ssl.trustStore", keystorePath);
		System.setProperty("javax.net.ssl.keyStore", keystorePath);
		System.setProperty("javax.net.ssl.trustStoreType", "jks");
	}

	public String createDatabase(Data data, String url) throws JsonProcessingException {
		String json = mapper.writeValueAsString(data);
		Query query = new Query("createDatabase", json);
		return sendSSLQuery(mapper.writeValueAsString(query), url, HTTPMethod.POST);
	}
	
	public String createDatabase(String data, String url) throws JsonProcessingException {
		return sendSSLQuery(data, url, HTTPMethod.POST);
	}

	public String removeDatabase(String url){
		return sendSSLQuery(null, url, HTTPMethod.DELETE);
	}

	public String exportDatabase(String url) throws JsonProcessingException {
		return sendSSLQuery(null, url, HTTPMethod.GET);
	}

	public String removeDocument(String url) {
		 return sendQuery(null, url, HTTPMethod.DELETE);
	}

	public String insertDocument(Data data, String url) throws JsonProcessingException {
		String json = mapper.writeValueAsString(data);
		Query query = new Query("insertDocument", json);
		return sendQuery(mapper.writeValueAsString(query), url, HTTPMethod.PUT);
	}
	
	public String insertDocument(String data, String url) throws JsonProcessingException {
		return sendQuery(data, url, HTTPMethod.PUT);
	}

	public String selectDocument(String url) {
		return sendQuery(null, url, HTTPMethod.GET);
	}

	private String sendSSLQuery(String query, String url, HTTPMethod method) {
		try {
			return manageSSLQuery(query, url, method).collect(Collectors.joining());
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

	private Stream<String> manageSSLQuery(String query, String url, HTTPMethod method) throws IOException {
		HttpsURLConnection connection = createHTTPSQuery(new URL(url), method.toString());
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

	private HttpsURLConnection createHTTPSQuery(URL url, String method) throws IOException {
		Objects.requireNonNull(login);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		prepareHTTPUrl(connection, method);
		connection.setRequestProperty("Authorization", "Basic " + encodeBase64(login.toString()));
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

	private static String encodeBase64(String string) {
		byte[] bytesEncoded = Base64.getEncoder().encode(string.getBytes());
		return new String(bytesEncoded);
	}

}

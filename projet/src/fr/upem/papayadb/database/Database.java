package fr.upem.papayadb.database;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;

/**
 * Class representing a document-oriented database<br>
 * A database is represented by a file listing all documents that are or were contained in it
 * and a number telling whether or not the document is still in.<br>
 * When the number of deleted files becomes too high, the database will by itself remove all lines with a not-contained-anymore document.<br>
 * The database implements a LRU cache for the documents.
 * @author jason
 *
 */
public class Database {
	private final FileChannel dbFileChannel;
	private long fileLength;
	private ArrayDeque<Pair<Integer, Document>> cache;
	private int cacheCapacity;
	private int deletedDocuments;
	private int deletedDocumentsThreshold;

	/**
	 * Creates a database
	 * @param filename The name of the database to create
	 * @param cacheCapacity	The capacity of the cache
	 * @param deletedDocumentsThreshold the maximum of deleted documents the file can contain
	 * @throws IOException If some I/O error occurs
	 */
	public Database(String filename, int cacheCapacity, int deletedDocumentsThreshold) throws IOException {
		Path filepath = Paths.get(filename);
		dbFileChannel = FileChannel.open(filepath, StandardOpenOption.CREATE, StandardOpenOption.READ,
				StandardOpenOption.WRITE);
		cache = new ArrayDeque<>();
		fileLength = new File(filepath.toString()).length();
		this.cacheCapacity = cacheCapacity;
		this.deletedDocumentsThreshold = deletedDocumentsThreshold;
	}

	/**
	 * Creates a database
	 * @param filename The name of the database to create
	 * @throws IOException If some I/O error occurs
	 */
	public Database(String filename) throws IOException {
		this(filename, 32, 32);
	}

	private void cache(String path, Document doc) throws IOException {
		if (cache.size() == cacheCapacity) {
			cache.removeFirst().getV2().close();
		}
		cache.addLast(new Pair<>(path.hashCode(), doc));
	}

	private Document getDocument(String path) throws IOException {
		Document doc = null;
		int hash = path.hashCode();
		for (Pair<Integer, Document> pair : cache) {
			if (pair.getV1() == hash) {
				doc = pair.getV2();
				break;
			}
		}
		if (doc == null) {
			doc = Document.openDocument(path);
			cache(path, doc);
		}
		return doc;
	}

	private boolean uncacheDocument(String path) throws IOException {
		int hash = path.hashCode();
		for (Pair<Integer, Document> pair : cache) {
			if (pair.getV1() == hash) {
				cache.remove(pair);
				return true;
			}
		}
		return false;
	}

	private List<String> getDocumentPaths() throws IOException {
		List<String> paths = new ArrayList<String>();
		MappedByteBuffer charBuffer = dbFileChannel.map(MapMode.READ_WRITE, 0, fileLength);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < fileLength; i++) {
			char c = (char) charBuffer.get();
			if (c == '\n') {
				String[] args = sb.toString().split("=");
				if (args[1].trim().equals("0")) {
					sb.delete(0, sb.length());
					continue;
				}
				paths.add(args[0].trim());
				sb.delete(0, sb.length());
				continue;
			}
			sb.append(c);
		}
		return paths;
	}

	/**
	 * Retrieves data
	 * @param where A list of values to check
	 * @return The data of all documents corresponding to the <code>where<code> clause
	 * @throws IOException IOException If some I/O error occurs
	 */
	public Map<String, Map<String, String>> select(String where) throws IOException {
		synchronized (dbFileChannel) {
			Map<String, Map<String, String>> results = new HashMap<>();
			List<String> paths = getDocumentPaths();
			for (String path : paths) {
				Document doc = getDocument(path);
				Map<String, String> row = doc.select(where);
				if (row != null) {
					results.put(path, row);
				}
			}
			return results;
		}
	}

	/**
	 * Inserts a document
	 * @param filepath The name of the document to insert
	 * @param jsonObject The document's data
	 * @throws IOException If some I/O error occurs
	 */
	public void insert(String filepath, JsonObject jsonObject) throws IOException {
		synchronized (dbFileChannel) {
			Document doc = Document.createDocument(filepath, jsonObject);
			int length = filepath.length();
			MappedByteBuffer map = dbFileChannel.map(MapMode.READ_WRITE, fileLength, length + 6);
			map.put(filepath.getBytes());
			map.put("=1\n".getBytes());
			map.force();
			cache(filepath, doc);
		}
	}

	/**
	 * Deletes a document
	 * @param name The name of the document to delete
	 * @throws IOException If some I/O error occurs
	 */
	public void delete(String name) throws IOException {
		synchronized (dbFileChannel) {
			uncacheDocument(name);
			MappedByteBuffer charBuffer = dbFileChannel.map(MapMode.READ_WRITE, 0, fileLength);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < fileLength; i++) {
				char c = (char) charBuffer.get();
				if (c == '\n') {
					String sbStr = sb.toString();
					if (sbStr.equals(name)) {
						charBuffer.position(charBuffer.position() - 2).putChar('0').getChar();
					}
					sb.delete(0, sb.length());
				} else {
					sb.append(c);
				}
			}
			deletedDocuments++;
			if (deletedDocuments >= deletedDocumentsThreshold) {
				rebuild();
			}
		}
	}

	/**
	 * Closes the database<br>
	 * This method forces all documents in-cache to get closed.
	 * @throws IOException If some I/O error occurs
	 */
	public void close() throws IOException {
		synchronized (dbFileChannel) {
			dbFileChannel.close();
			while (cache.isEmpty() == false) {
				cache.pop().getV2().close();
			}
		}
	}

	/**
	 * Removes all entries from the database<br>
	 * This method forces all documents in-cache to get closed.
	 * @throws IOException If some I/O error occurs
	 */
	public void clear() throws IOException {
		synchronized (dbFileChannel) {
			MappedByteBuffer map = dbFileChannel.map(MapMode.READ_WRITE, 0, fileLength);
			map.clear();
			map.force();
			fileLength = 0;
			while (cache.isEmpty() == false) {
				cache.pop().getV2().close();
			}
			deletedDocuments = 0;
		}
	}

	/**
	 * Exports all data in the database
	 * @return A string representation of all data in the database
	 * @throws IOException If some I/O error occurs
	 */
	public String export() throws IOException {
		synchronized (dbFileChannel) {
			return select("").toString();
		}
	}
	
	/**
	 * Sets a new limit of deleted documents
	 * @param limit The new maximum of deleted documents the database can contain
	 * @throws IOException If some I/O error occurs
	 */
	public void setDeletedDocumentsThreshold(int limit) throws IOException{
		if (limit < 0){
			throw new IllegalArgumentException("limit must be positive");
		}
		deletedDocumentsThreshold = limit;
		if (deletedDocuments >= deletedDocumentsThreshold){
			rebuild();
		}
	}

	private void rebuild() throws IOException {
		synchronized (dbFileChannel) {
			MappedByteBuffer map = dbFileChannel.map(MapMode.READ_WRITE, 0, fileLength);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < fileLength; i++) {
				char c = (char) map.get();
				if (c == '\n') {
					String[] args = sb.toString().split("=");
					if (args[1].trim().equals("0")) {
						int length = 1 + args[0].length();
						map.position(map.position() - length);
						for (int j = 0; j < length; j++) {
							map.put((byte) ' ');
						}
					}
					sb.delete(0, sb.length());
					continue;
				}
				sb.append(c);
			}
			deletedDocuments = 0;
			map.force();
		}
	}
}

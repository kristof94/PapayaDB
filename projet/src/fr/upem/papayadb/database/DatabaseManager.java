package fr.upem.papayadb.database;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.json.Json;
import javax.json.JsonReader;

/**
 * Class handling databases<br>
 * The data is stored in a file containing a list of all databases the manager manages separated by a line-terminator.<br>
 * @author jason
 *
 */
public class DatabaseManager {
	private final String filename;
	private final Map<String, Database> databases; // Used to accelerate database search
	private long length; // Length of the file
	private final FileChannel channel;

	/**
	 * Creates a database manager
	 * @param filepath the path of the file used to store databases
	 * @throws IOException
	 */
	public DatabaseManager(String filepath) throws IOException {
		filename = filepath;
		databases = new HashMap<>();
		channel = FileChannel.open(Paths.get(filepath), StandardOpenOption.CREATE, StandardOpenOption.WRITE,
				StandardOpenOption.READ);
		length = new File(filepath).length();
	}

	private Database getDatabase(String name) {
		Database db = databases.get(name);
		if (db == null) {
			throw new NoSuchElementException("There is no database named " + name);
		}
		return db;
	}

	/**
	 * Creates a database
	 * @param Name the name of the database to create
	 * @return An instance of the newly created database
	 * @throws IllegalArgumentException If a database with the same name is already managed by the manager
	 * @throws IOException If some I/O error occurs
	 */
	public Database createDatabase(String name) throws IOException {
		synchronized (filename) {
			if (databases.containsKey(name)) {
				throw new IllegalArgumentException("Database " + name + " already exists");
			}
			Database db = new Database(name);
			MappedByteBuffer map = channel.map(MapMode.READ_WRITE, length, name.length() + 1);
			map.put(name.getBytes());
			map.put((byte) '\n');
			map.force();
			databases.put(name, db);
			length += map.capacity();
			return db;
		}
	}

	/**
	 * Deletes a database
	 * @param name The name of the database to delete
	 * @throws IllegalArgumentException If no database with the specified name is managed by the manager
	 * @throws IOException If some I/O error occurs
	 */
	public void deleteDatabase(String name) throws IOException {
		synchronized (filename) {
			Database db = getDatabase(name);
			databases.remove(name);
			db.clear();
			MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, length);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < length; i++) {
				char c = (char) map.get();
				if (c == '\n') {
					if (sb.toString().equals(name)) {
						map.position(map.position() - name.length() - 1);
						for (int j = 0; j < name.length() + 1; j++) {
							map.put((byte) ' ');
						}
						map.compact();
						map.force();
						return;
					}
					sb.delete(0, sb.length());
				} else {
					sb.append(c);
				}
			}
		}
	}

	/**
	 * Exports the database<br>
	 * It generates a String representing all data contained in the database
	 * @param name The name of the dataabse to export
	 * @return A String representing the data of the database
	 * @throws IllegalArgumentException If no database with the specified name is managed by the manager
	 * @throws IOException If some I/O error occurs
	 */
	public String exportDatabase(String name) throws IOException {
		synchronized (filename) {
			Database db = getDatabase(name);
			return db.export();
		}
	}

	/**
	 * Inserts a document in a database
	 * @param databaseName The name of the database to insert the document into
	 * @param documentName The name of the document to insert
	 * @param data The document's data
	 * @throws IllegalArgumentException If no database with the specified name in managed by the manager
	 * @throws IOException If some I/O error occurs
	 */
	public void insertDocument(String databaseName, String documentName, String data) throws IOException {
		synchronized (filename) {
			Database db = getDatabase(databaseName);
			JsonReader reader = Json.createReader(new StringReader(data));
			db.insert(documentName, reader.readObject());
			reader.close();
		}
	}

	/**
	 * Retrieves data from a database
	 * @param databaseName The name of the database to get data from
	 * @param where A list of values to check
	 * @return The data of all documents in the specified corresponding to the <code>where<code> clause
	 * @throws IllegalArgumentException If no database with the specified name is managed by the manager
	 * @throws IOException If some I/O error occurs
	 */
	public Map<String, Map<String, String>> select(String databaseName, String where) throws IOException {
		synchronized (filename) {
			Database db = getDatabase(databaseName);
			return db.select(where);
		}
	}

	/**
	 * Deletes a document from a database
	 * @param databaseName The name of the database to delete the document from
	 * @param documentName The name of the document to delete
	 * @throws IllegalArgumentException If no database with the specified name is managed by the manager
	 * @throws IOException If some I/O error occurs
	 */
	public void deleteDocument(String databaseName, String documentName) throws IOException {
		synchronized (filename) {
			Database db = getDatabase(databaseName);
			db.delete(documentName);
		}
	}
}

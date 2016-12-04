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
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.json.Json;
import javax.json.JsonReader;

public class DatabaseManager {
	private final String filename;
	private final Map<String, Database> databases;
	private long length;
	private final FileChannel channel;
	
	public DatabaseManager(String filepath) throws IOException{
		filename = filepath;
		databases = new HashMap<>();
		channel = FileChannel.open(Paths.get(filepath), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ);
		length = new File(filepath).length();
	}
	
	private Database getDatabase(String name){
		Database db = databases.remove(name);
		if (db == null){
			throw new NoSuchElementException("There is no database named " + name);
		}
		return db;
	}
	
	public Database createDatabase(String name) throws IOException{
		synchronized(filename){
			if (databases.containsKey(name)){
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
	
	public void deleteDatabase(String name) throws IOException{
		synchronized(filename){
			Database db = getDatabase(name);
			db.clear();
			MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, length);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i <length; i++){
				char c = (char)map.get();
				if (c == '\n'){
					if (sb.toString().equals(name)){
						map.position(map.position() - name.length() - 1);
						for (int j = 0; j < name.length() + 1; j++){
							map.put((byte) ' ');
						}
						map.compact();
						map.force();
						return;
					}
					sb.delete(0, sb.length());
				}
				else{
					sb.append(c);
				}
			}
		}
	}
	
	public String exportDatabase(String name) throws IOException{
		Database db = getDatabase(name);
		return db.export();
	}
	
	public void insertDocument(String databaseName, String documentName, String data) throws IOException{
		Database db = getDatabase(databaseName);
		JsonReader reader = Json.createReader(new StringReader(data));
		db.insert(documentName, reader.readObject());
		reader.close();
	}
	
	public List<Map<String, String>> select(String databaseName, String data) throws IOException{
		Database db = getDatabase(databaseName);
		return db.select(data);
	}
	
	public void deleteDocument(String databaseName, String documentName) throws Exception{
		Database db = getDatabase(databaseName);
		db.delete(documentName);
	}
}

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
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;

public class Database {
	private final FileChannel dbFileChannel;
	private long fileLength;
	private ArrayDeque<Pair<Integer, Document>> cache;
	private int cacheCapacity;
	private int deletedDocuments;
	private int deletedDocumentsThreshold;

	public Database(Path filepath, int cacheCapacity) throws IOException {
		dbFileChannel = FileChannel.open(filepath, StandardOpenOption.CREATE, StandardOpenOption.READ,
				StandardOpenOption.WRITE);
		cache = new ArrayDeque<>();
		fileLength = new File(filepath.toString()).length();
		this.cacheCapacity = cacheCapacity;
	}

	public Database(String filename) throws IOException {
		this(Paths.get(filename), 32);
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

	public List<Map<String, String>> select(String request) throws IOException {
		synchronized (dbFileChannel) {
			List<Map<String, String>> results = new ArrayList<>();
			List<String> paths = getDocumentPaths();
			for (String path : paths) {
				Document doc = getDocument(path);
				Map<String, String> row = doc.select(request);
				if (row != null) {
					results.add(row);
				}
			}
			return results;
		}
	}

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

	public void delete(String name) throws Exception {
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

	public void close() throws IOException {
		synchronized (dbFileChannel) {
			dbFileChannel.close();
			while (cache.isEmpty() == false) {
				cache.pop().getV2().close();
			}
		}
	}

	public void clear() throws IOException {
		synchronized (dbFileChannel) {
			MappedByteBuffer map = dbFileChannel.map(MapMode.READ_WRITE, 0, fileLength);
			map.clear();
			map.force();
			fileLength = 0;
			cache.clear();
		}
	}

	public String export() throws IOException {
		synchronized (dbFileChannel) {
			return select("").toString();
		}
	}

	public void rebuild() throws IOException {
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

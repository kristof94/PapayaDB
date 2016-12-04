package fr.upem.papayadb.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonValue;

import io.netty.handler.codec.http2.StreamByteDistributor.Writer;

/**
 * A document is plit in two files: one for the data and one for the indexes<br>
 * example for a file named toto:<br>
 * toto.dat:<br>
 * PapayaDB20161204<br>
 * toto.index:<br>
 * name=0 8<br>
 * year=8 4<br>
 * month=12 2<br>
 * day=14 2<br>
 * Note there are two numbers for each index. The first one is the position of the data and the second the length of the data.
 * @author jason
 *
 */
public class Document {
	private final FileChannel fileChannel;
	private long fileSize;
	private final List<Integer> hashCodes;
	private final List<int[]> indexes;
	private final List<String> names;
	private String filepath;
	
	/**
	 * Creates a document
	 * @param filepath
	 * @return 
	 * @throws IOException
	 */
	public static Document openDocument(String filepath) throws IOException{
		Document doc = new Document(filepath);
		doc.open();
		return doc;
	}
	
	public static Document createDocument(String filepath, JsonValue object) throws IOException{
		Document doc = new Document(filepath);
		doc.create(object);
		return doc;
	}
	
	private void fillDataArray(StringBuilder indexSb, StringBuilder dataSb, JsonArray arr, String s, Holder<Integer> index){
		int i = 0;
		for (JsonValue v : arr){
			switch(v.getValueType()){
				case ARRAY:
					JsonArray jsonArray = (JsonArray)v;
					fillDataArray(indexSb, dataSb, jsonArray, s + '[' + i + ']', index);
					break;
				case OBJECT:
					JsonObject innerObj = (JsonObject)v;
					fillDataObject(indexSb, dataSb, innerObj, s + '[' + i + ']', index);
					break;
				case NULL:
					indexSb.append(s);
					indexSb.append('[');
					indexSb.append(i);
					indexSb.append("]=");
					indexSb.append(index.getValue());
					indexSb.append(' ');
					indexSb.append("-1\n");
					break;
				default:
					String value = v.toString().replaceFirst("^\"", "").replaceFirst("\"$", "");
					indexSb.append(s);
					indexSb.append('[');
					indexSb.append(i);
					indexSb.append("]=");
					indexSb.append(index.getValue());
					indexSb.append(' ');
					indexSb.append(value.length());
					indexSb.append("\n");
					index.setValue(index.getValue() + value.length());
					dataSb.append(value);
					break;
			}
			i++;
		}
	}
	
	private void fillDataObject(StringBuilder indexSb, StringBuilder dataSb, JsonObject obj, String s, Holder<Integer> index){
		if (obj == null){
			return;
		}
		obj.forEach((k, v) -> {
			switch(v.getValueType()){
				case ARRAY:
					JsonArray jsonArray = (JsonArray)v;
					fillDataArray(indexSb, dataSb, jsonArray, s + k, index);
					break;
				case OBJECT:
					JsonObject innerObj = (JsonObject)v;
					fillDataObject(indexSb, dataSb, innerObj, s + k, index);
					break;
				case NULL:
					if (s.length() != 0){
						indexSb.append(s);
						indexSb.append('.');
					}
					indexSb.append(k);
					indexSb.append('=');
					indexSb.append(index.getValue());
					indexSb.append(' ');
					indexSb.append("-1\n");
					break;
				default:
					String value = v.toString().replaceFirst("^\"", "").replaceFirst("\"$", "");
					if (s.length() != 0){
						indexSb.append(s);
						indexSb.append('.');
					}
					indexSb.append(k);
					indexSb.append('=');
					indexSb.append(index.getValue());
					indexSb.append(' ');
					indexSb.append(value.length());
					indexSb.append("\n");
					index.setValue(index.getValue() + value.length());
					dataSb.append(value);
					break;
			}
		});
	}
	
	private void fillData(StringBuilder indexSb, StringBuilder dataSb, JsonValue value){
		Holder<Integer> holder = new Holder<>(0);
		switch (value.getValueType()){
			case ARRAY:
				fillDataArray(indexSb, dataSb, (JsonArray)value, "", holder);
				break;
			case OBJECT:
				fillDataObject(indexSb, dataSb, (JsonObject)value, "", holder);
				break;
			case NULL:
				indexSb.append("data=0 -1\n");
				break;
			default:
				String v = value.toString();
				indexSb.append("data=0 ");
				indexSb.append(v.length());
				indexSb.append("\n");
				dataSb.append(v);
		}
	}
	
	private Document(String filepath) throws IOException{
		indexes = new ArrayList<>();
		hashCodes = new ArrayList<>();
		names = new ArrayList<>();
		this.filepath = filepath;
		fileChannel = FileChannel.open(Paths.get(filepath + ".dat"), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
	}
	
	private void create(JsonValue object) throws IOException{
		BufferedWriter indexWriter = new BufferedWriter(new FileWriter(filepath + ".index"));
		BufferedWriter dataWriter = new BufferedWriter(new FileWriter(filepath + ".dat"));
		StringBuilder indexSb = new StringBuilder();
		StringBuilder dataSb = new StringBuilder();
		fillData(indexSb, dataSb, object);
		indexWriter.write(indexSb.toString());
		dataWriter.write(dataSb.toString());
		indexWriter.close();
		dataWriter.close();
	}
	
	private void open() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(filepath + ".index"));
		File file = new File(filepath + ".dat");
		fileSize = file.length();
		//int lineCount = 0;
		while (reader.ready()){
			String line = reader.readLine();
			String[] args = line.split("=");
			String[] indexes = args[1].split(" ");
			this.hashCodes.add(args[0].trim().hashCode());
			this.names.add(args[0].trim());
			this.indexes.add(new int[]{Integer.decode(indexes[0]), Integer.decode(indexes[1])});
			/*if (line.startsWith("{")){
				break;
			}
			String[] args = line.split(": ");
			String[] counts = args[1].split(" ");
			indexes.put(args[0].trim(), new Integer[]{lineCount, Integer.decode(counts[0].trim()), Integer.decode(counts[1].trim())});
			lineCount++;*/
		}
		reader.close();
		/*JsonReader reader = Json.createReader(new FileInputStream(filepath));
		JsonObject metadata = reader.readObject().getJsonObject("metadata");
		if (metadata == null){
			throw new JsonException("Metadata field not found.");
		}
		metadata.forEach((k, v) -> {
			if (v.getValueType() != JsonValue.ValueType.NUMBER){
				throw new JsonException("Metadatas expect numbers as values. Got another type.");
			}
			indexes.put(k, Long.decode(v.toString()));
		});
		reader.close();*/
	}
	
	private boolean checkValue(JsonObject where, String value){
		return true;
	}
	
	/**
	 * Requests values of specific fields
	 * @param request
	 * @return the values in the requested fields
	 * @throws IOException 
	 */
	public Map<String, String> select(JsonObject request) throws IOException{
		synchronized(fileChannel){
			MappedByteBuffer map = fileChannel.map(MapMode.READ_ONLY, 0, fileSize);
			HashMap<String, String> results = new HashMap<>();
			JsonArray fields = request.getJsonArray("fields");
			if (fields == null){
				throw new JsonException("fields field not found");
			}
			JsonObject where = request.getJsonObject("where");
			if (where == null){
				fields.forEach(f -> {
					String name = f.toString();
					name = name.replaceAll("\"", "");
					int tabIndex = hashCodes.indexOf(name.hashCode());
					Objects.checkIndex(tabIndex, indexes.size());
					int[] tab = indexes.get(tabIndex);
					int index = tab[0];
					int length = tab[1];
					if (length > 0){
						byte[] data = new byte[length];
						map.position(index).get(data, 0, length);
						results.put(f.toString().trim(), new String(data));
					}
					else{
						results.put(f.toString().trim(), null);
					}
				});
				return results.isEmpty() ? null : results;
			}
			for (JsonValue f : fields){
				String name = f.toString();
				name = name.replaceAll("\"", "");
				int tabIndex = hashCodes.indexOf(name.hashCode());
				Objects.checkIndex(tabIndex, indexes.size());
				int[] tab = indexes.get(tabIndex);
				int index = tab[0];
				int length = tab[1];
				if (length > 0){
					byte[] data = new byte[length];
					String value = new String(data);
					if (checkValue(where.getJsonObject(name), value) == false){
						return null;
					}
					map.position(index).get(data, 0, length);
					results.put(f.toString().trim(), value);
				}
				else{
					results.put(f.toString().trim(), null);
				}
			}
			return results.isEmpty() ? null : results;
		}
	}
	
	/**
	 * Updates specific fields
	 * @param request
	 * @throws IOException 
	 */
	public JsonObject update(JsonObject request) throws IOException{
		synchronized(fileChannel){
			MappedByteBuffer map = fileChannel.map(MapMode.READ_WRITE, 0, (fileSize + 1) * 4096);
			JsonObject fields = request.getJsonObject("set");
			if (fields == null){
				throw new JsonException("set field not found");
			}
			JsonObject where = request.getJsonObject("where");
			if (where == null){
				fields.forEach((k, v) -> {
					String name = k.toString();
					name = name.replaceAll("\"", "");
					int tabIndex = hashCodes.indexOf(name.hashCode());
					Objects.checkIndex(tabIndex, indexes.size());
					int[] tab = indexes.get(tabIndex);
					if (tab == null){
						return;
					}
					int index = tab[0];
					byte[] value = v.toString().getBytes();
					tab[1] = value.length;
					ByteBuffer before = map.position(0).alignedSlice(index + value.length);
					before.position(index).put(value);
					ByteBuffer after = map.position(indexes.get(tabIndex + 1)[0]).slice().duplicate();
					map.clear().put(before).put(after);
					indexes.subList(tabIndex + 1, indexes.size()).forEach(t -> {
						t[0] += value.length;
					});
				});
			}
			else{
				for (Entry<String, JsonValue> entry : fields.entrySet()){
					String k = entry.getKey();
					JsonValue v = entry.getValue();
					String name = k.toString();
					name = name.replaceAll("\"", "");
					int tabIndex = hashCodes.indexOf(name.hashCode());
					Objects.checkIndex(tabIndex, indexes.size());
					int[] tab = indexes.get(tabIndex);
					if (tab == null){
						return null;
					}
					int index = tab[0];
					byte[] value = v.toString().getBytes();
					if (checkValue(where.getJsonObject(name), new String(value)) == false){
						return null;
					}
					tab[1] = value.length;
					ByteBuffer before = map.position(0).alignedSlice(index + value.length);
					before.position(index).put(value);
					ByteBuffer after = map.position(indexes.get(tabIndex + 1)[0]).slice().duplicate();
					map.clear().put(before).put(after);
					indexes.subList(tabIndex + 1, indexes.size()).forEach(t -> {
						t[0] += value.length;
					});
				}
			}
			map.force();
			return null;
		}
	}
	
	/**
	 * Removes specific fields
	 * @param request
	 * @throws IOException 
	 */
	public JsonObject remove(JsonObject request) throws IOException{
		JsonArray fields = request.getJsonArray("fields");
		if (fields == null){
			throw new JsonException("fields field not found");
		}
		JsonArrayBuilder builder = Json.createArrayBuilder();
		JsonObject where = request.getJsonObject("where");
		if (where == null){
			fields.forEach(f -> {
				String name = f.toString();
				int index = hashCodes.indexOf(name.hashCode());
				if (index == -1){
					return;
				}
				hashCodes.remove(index);
				indexes.remove(index);
				builder.add(name);
			});
		}
		
		return Json.createObjectBuilder().add("fields", builder).build();
	}
	
	public void flush() throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(filepath + ".index"));
		int offset = 0;
		for (int i = 0; i < indexes.size(); i++){
			int length = names.get(i).length();
			writer.write(names.get(i), offset, length);
			offset += length;
		}
		writer.close();
	}
	
	public void close() throws IOException{
		fileChannel.close();
		flush();
	}
	
	/*public JsonObject request(JsonObject request){
		String command = request.getString("command");
		if (command == null){
			return null;
		}
		switch (command){
			case "select":
				return select(request);
		}
	}*/
}

package fr.upem.papayadb.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

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
	 * Opens a document
	 * @param filepath the name of the document to open
	 * @return a document containing data from specified path
	 * @throws IOException
	 */
	public static Document openDocument(String filepath) throws IOException{
		Document doc = new Document(filepath);
		doc.open();
		return doc;
	}
	/**
	 * Creates a document
	 * @param filepath the name of the document to create
	 * @return the newly created document
	 * @throws IOException
	 */
	public static Document createDocument(String filepath, JsonValue object) throws IOException{
		Document doc = new Document(filepath);
		doc.create(object);
		return doc;
	}
	
	private Document(String filepath) throws IOException{
		indexes = new ArrayList<>();
		hashCodes = new ArrayList<>();
		names = new ArrayList<>();
		this.filepath = filepath;
		fileChannel = FileChannel.open(Paths.get(filepath + ".dat"), StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
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
		while (reader.ready()){
			String line = reader.readLine();
			String[] args = line.split("=");
			String[] indexes = args[1].split(" ");
			this.hashCodes.add(args[0].trim().hashCode());
			this.names.add(args[0].trim());
			this.indexes.add(new int[]{Integer.decode(indexes[0]), Integer.decode(indexes[1])});
		}
		reader.close();
	}
	
	private char getComparisonChar(char c, char[] ca, int i, StringBuilder sb, String[] kv){
		char comparison = c;
		switch(c){
			case '!':
				if ((c = ca[i]) != '='){
					throw new IllegalArgumentException("! needs to be followed by a =");
				}
				break;
			case '<':
				if ((c = ca[i]) == '='){
					comparison = 'l';
				}
				break;
			case '>':
				if ((c = ca[i]) == '='){
					comparison = 'g';
				}
				break;
		}
		
		kv[0] = sb.toString();
		sb.delete(0, sb.length());
		return comparison;
	}
	
	/**
	 * Requests values of specific fields
	 * @param request
	 * @return the values in the requested fields
	 * @throws IOException 
	 */
	public Map<String, String> select(String request) throws IOException{
		synchronized(fileChannel){
			MappedByteBuffer map = fileChannel.map(MapMode.READ_ONLY, 0, fileSize);
			HashMap<String, String> results = new HashMap<>();
			String[] fields = request.split("&");
			for (String f : fields){
				String[] kv = new String[2];
				StringBuilder sb = new StringBuilder();
				char[] ca = f.toCharArray();
				char comparison = 0;
				for (int i = 0; i < ca.length; i++){
					char c = ca[i];
					switch(c){
						case '=':
							comparison = c;
							kv[0] = sb.toString();
							sb.delete(0, sb.length());
							break;
						case '!':
						case '<':
						case '>':
							i++;
							comparison = getComparisonChar(c, ca, i, sb, kv);
							break;
						default:
							sb.append(c);
					}
				}
				if (kv.length > 1){
					kv[1] = sb.toString();
				}
				String name = kv[0].toString();
				int tabIndex = hashCodes.indexOf(name.hashCode());
				if (tabIndex < 0){
					return null;
				}
				int[] tab = indexes.get(tabIndex);
				int index = tab[0];
				int length = tab[1];
				String toWrite = null;
				if (length > 0){
					byte[] data = new byte[length];
					map.position(index).get(data, 0, length);
					toWrite = new String(data);
				}
				if (toWrite == null && kv.length > 1 
						|| toWrite != null && kv.length == 1){
					return null;
				}
				switch(comparison){
					case '=':
						if (toWrite != null && kv.length > 1 && !toWrite.equals(kv[1])){
							return null;
						}
						break;
					case '!':
						if (toWrite != null && kv.length > 1 && toWrite.equals(kv[1])){
							return null;
						}
						break;
					case '>':
						if (toWrite != null && kv.length > 1 && toWrite.compareTo(kv[1]) < 1){
							return null;
						}
						break;
					case 'g':
						if (toWrite != null && kv.length > 1 && toWrite.compareTo(kv[1]) < 0){
							return null;
						}
						break;
					case '<':
						if (toWrite != null && kv.length > 1 && toWrite.compareTo(kv[1]) > -1){
							return null;
						}
						break;
					case 'l':
						if (toWrite != null && kv.length > 1 && toWrite.compareTo(kv[1]) > 0){
							return null;
						}
						break;
				}
			}
			for (int i = 0; i < names.size(); i++){
				int[] index = indexes.get(i);
				int ind = index[0];
				int length = index[1];
				String toWrite = null;
				if (length > 0){
					byte[] data = new byte[length];
					map.position(ind).get(data, 0, length);
					toWrite = new String(data);
				}
				results.put(names.get(i), toWrite);
			}
			return results;
		}
	}
	
	public void close() throws IOException{
		fileChannel.close();
	}
}

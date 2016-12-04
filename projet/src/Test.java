import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;

import fr.upem.papayadb.database.Database;
import fr.upem.papayadb.database.Document;

public class Test {
	public static void main(String[] args) throws Exception {
		/*Document doc = Document.openDocument("test.json");
		Map<String, String> select = doc.select(Json.createObjectBuilder()
				.add("fields", Json.createArrayBuilder()
						.add("name")
						.add("a"))
				.build());
		select.forEach((k, v) -> {
			System.out.println(k + ": " + v);
		});*/
		Database db = new Database("db.txt");
		db.insert("createDocTest", (JsonObject)Json.createReader(new BufferedReader(new FileReader("test.json"))).read());
		/*db.delete(Json.createObjectBuilder()
				.add("documents", Json.createArrayBuilder()
						.add("test2"))
				.build());
		List<Map<String, String>> select = db.select(Json.createObjectBuilder()
				.add("fields", Json.createArrayBuilder()
						.add("name")
						.add("a"))
				.build());
		select.forEach(r -> {
			System.out.println("Row data:");
			r.forEach((k, v) -> {
				System.out.println("\t" + k + ": " + v);
			});
		});*/
	}
}

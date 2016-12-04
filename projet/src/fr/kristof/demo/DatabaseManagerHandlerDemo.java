/**
 * 
 */
package fr.kristof.demo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.kristof.client.Data;
import fr.kristof.server.DataBaseHandler;
import fr.kristof.server.ServerResponse;
import fr.upem.papayadb.database.DatabaseManager;
import io.vertx.ext.web.RoutingContext;

/**
 * @author kristof
 *
 */
public class DatabaseManagerHandlerDemo implements DataBaseHandler {

	private DatabaseManager databaseManager;
	private final ObjectMapper mapper;

	/**
	 * @throws IOException
	 * 
	 */
	public DatabaseManagerHandlerDemo() throws IOException {
		databaseManager = new DatabaseManager("db.txt");
		mapper = new ObjectMapper();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.upem.server.DataBaseHandler#handleCreateDatabaseRequest(io.vertx.ext.
	 * web.RoutingContext)
	 */
	@Override
	public void handleCreateDatabaseRequest(RoutingContext arg0) {
		arg0.request().bodyHandler(buffer -> {
			Data data = null;
			try {
				data = mapper.readValue(buffer.toString(), Data.class);
				databaseManager.createDatabase(data.name);
				ServerResponse.responseDatabase(arg0, "Base crée");
			} catch (IOException e) {
				ServerResponse.responseDatabase(arg0, "Base already exists");
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.upem.server.DataBaseHandler#handleDropDatabaseRequest(io.vertx.ext.web
	 * .RoutingContext)
	 */
	@Override
	public void handleDropDatabaseRequest(RoutingContext arg0) {
		try {
			databaseManager.deleteDatabase(arg0.request().getParam("name"));
			ServerResponse.responseDatabase(arg0, "Base supprimé.");
		} catch (IOException e) {
			ServerResponse.sendErrorResponse(arg0,"Base inexistante.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.upem.server.DataBaseHandler#handleExportDatabaseRequest(io.vertx.ext.
	 * web.RoutingContext)
	 */
	@Override
	public void handleExportDatabaseRequest(RoutingContext arg0) {
		try {
			String export = databaseManager.exportDatabase(arg0.request().getParam("name"));
			ServerResponse.responseDatabase(arg0, export);
		} catch (IOException e) {
			ServerResponse.sendErrorResponse(arg0, "Erreur lors de l'export de la base.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.upem.server.DataBaseHandler#handleInsertDatabaseRequest(io.vertx.ext.
	 * web.RoutingContext)
	 */
	@Override
	public void handleInsertDocumentDatabaseRequest(RoutingContext arg0) {
		arg0.request().bodyHandler(buffer -> {
			Data data = null;
			try {
				data = mapper.readValue(buffer.toString(), Data.class);
				databaseManager.insertDocument(arg0.request().getParam("name"), data.name,mapper.writeValueAsString(data));
				ServerResponse.responseDatabase(arg0, "Insertion réussie.");
			} catch (IOException e) {
				ServerResponse.sendErrorResponse(arg0, "Probleme d'insertion.");
			}

		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.upem.server.DataBaseHandler#handleRemoveDatabaseRequest(io.vertx.ext.
	 * web.RoutingContext)
	 */
	@Override
	public void handleRemoveDocumentFromDatabaseRequest(RoutingContext arg0) {
		arg0.request().bodyHandler(buffer -> {
			try {
				databaseManager.deleteDocument(arg0.request().getParam("name"), arg0.request().getParam("namedoc"));
				ServerResponse.responseDatabase(arg0, arg0.request().getParam("namedoc"));
			} catch (Exception e) {
				ServerResponse.sendErrorResponse(arg0, "Remove document.");
			}
		});
	}

	// private final String checkQueryFormatRegex
	// ="([a-zA-Z\\d]*?)&([a-zA-Z\\d]*?)";
	// private final String checkQueryFormatRegex =
	// "([a-zA-Z\\d]*?)=([a-zA-Z\\d]*?)";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.upem.server.DataBaseHandler#handleSelectDatabaseRequest(io.vertx.ext.
	 * web.RoutingContext)
	 */
	@Override
	public void handleSelectDocumentFromDatabaseRequest(RoutingContext arg0) {
		try {
			Map<String, Map<String, String>> data = databaseManager.select(arg0.request().getParam("name"), arg0.request().getParam("namedoc"));
			ServerResponse.responseDatabase(arg0, arg0.request().params().toString());
		} catch (IOException e) {
			ServerResponse.responseDatabase(arg0, e.getMessage());
		}

		// detectParameters(arg0.request().getParam("namedoc").toString());
	}

	/*
	 * private void detectParameters(String uri) { Objects.requireNonNull(uri);
	 * String[] parameter = uri.split("&"); HashMap<String,String> mapParameter
	 * = new HashMap<>(); for (String str : parameter) { Pattern pattern =
	 * Pattern.compile(checkQueryFormatRegex); Matcher matcher =
	 * pattern.matcher(str); if (!matcher.matches()) { throw new
	 * IllegalAccessError("URL INVALIDE"); } mapParameter.put(matcher.group(1),
	 * matcher.group(2)); } }
	 */

}

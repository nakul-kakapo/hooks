import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class HookHandler implements HttpHandler {

	private static final int PORT = 4025;
	private static final String CONTEXT = "/hooks";
	private static final String HUB_CHALLENGE = "hub.challenge";
	private static final String HUB_MODE = "hub.mode";
	private static final String HUB_VERIFY_TOKEN = "hub.verify_token";
	private String challenge = "";

	public HookHandler() {
		run();
	}

	public void run() {

		// get page access token

		HttpServer server = null;
		try {
			server = HttpServer.create();
// 			server = HttpServer.create(new InetSocketAddress(PORT), 10);
			server.setExecutor(Executors.newFixedThreadPool(10));
			server.start();
			server.createContext(CONTEXT, this);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void handle(HttpExchange httpExch) throws IOException {
		
		String query = httpExch.getRequestURI().getQuery();
		System.out.println("Query: " + query);
		if (query != null) {
			Map<String, String> params = queryToMap(query);
			challenge = params.get(HUB_CHALLENGE);
		}

		InputStream inputStream = httpExch.getRequestBody();
		StringBuilder sb = new StringBuilder();
		int i;
		while ((i = inputStream.read()) != -1) {
			sb.append((char) i);
		}
		inputStream.close();
		String incomingJSONContent = sb.toString();
		
		System.out.println("incomingJSONContent: " + incomingJSONContent);
//		(new JsonParser()).parseJson(incomingJSONContent);
//
//		replyOK(httpExch); // SYNCHRONOUS REPLY TO FB
//
//		if (!VERIFY_WEBHOOK && !incomingJSONContent.contains("delivery") && !incomingJSONContent.contains("read")) {
//			// get recipient ID from incoming message
//			asyncReply(httpExch);
//		}
		 
	}

	private Map<String, String> queryToMap(String query) {
		Map<String, String> result = new HashMap<>();
		for (String param : query.split("&")) {
			String[] entry = param.split("=");
			if (entry.length > 1) {
				result.put(entry[0], entry[1]);
			} else {
				result.put(entry[0], "");
			}
		}
		return result;
	}
	
	private void replyOK(HttpExchange httpExch) {
		try {
			Headers responseHeaders = httpExch.getResponseHeaders();
			responseHeaders.add("Content-Type", ("application/json"));
			httpExch.sendResponseHeaders(200, challenge.length());
			try (OutputStream os = httpExch.getResponseBody()) {
				if (!challenge.isEmpty()) {
					os.write(challenge.getBytes());
				}
			}

		} catch (IOException e1) {
			System.out.println(e1.getMessage());
		}
	}

}

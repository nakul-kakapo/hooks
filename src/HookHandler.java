import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HookHandler implements HttpHandler {
	
	private static final int PORT = 4025;
	private static final String CONTEXT = "/hooks";
	
	public HookHandler() {
		run();
	}
	
	public void run() {

		// get page access token

		HttpServer server = null;
		try {
			server = HttpServer.create(new InetSocketAddress(PORT), 10);
			server.setExecutor(Executors.newFixedThreadPool(10));
			server.start();
			server.createContext(CONTEXT, this);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void handle(HttpExchange exchange) throws IOException {
		// TODO Auto-generated method stub
		
	}

}

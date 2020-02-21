package net.minecraft.src.betatweaks;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;

import net.minecraft.src.PropertyManager;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonServer {
	private static final PropertyManager manager = new PropertyManager(new File("server.properties"));
	private static final String HOSTNAME = manager.getStringProperty("server-ip", "");
    //private static final String HOSTNAME = "localhost";
	private static final int PORT = manager.getIntProperty("server-port", 25565);
    //private static final int PORT = 8080;
    private static final int BACKLOG = 1;

    private static final String HEADER_ALLOW = "Allow";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final int STATUS_OK = 200;
    private static final int STATUS_METHOD_NOT_ALLOWED = 405;

    private static final int NO_RESPONSE_LENGTH = -1;

    private static final String METHOD_GET = "GET";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String ALLOWED_METHODS = METHOD_GET + "," + METHOD_OPTIONS;

    public static void main() {
        HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);
			server.createContext("/func1", he -> {
	            try {
	                final Headers headers = he.getResponseHeaders();
	                final String requestMethod = he.getRequestMethod().toUpperCase();
	                switch (requestMethod) {
	                    case METHOD_GET:
	                        // do something with the request parameters
	                        //final String responseBody = "['hello world!']";
	                        final String responseBody = new StringBuilder("'[").append(time()).append("]'").toString();
	                        headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
	                        final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
	                        he.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
	                        he.getResponseBody().write(rawResponseBody);
	                        break;
	                    case METHOD_OPTIONS:
	                        headers.set(HEADER_ALLOW, ALLOWED_METHODS);
	                        he.sendResponseHeaders(STATUS_OK, NO_RESPONSE_LENGTH);
	                        break;
	                    default:
	                        headers.set(HEADER_ALLOW, ALLOWED_METHODS);
	                        he.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, NO_RESPONSE_LENGTH);
	                        break;
	                }
	            } finally {
	                he.close();
	            }
	        });
	        server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
    
    public static String time() {
    	return String.valueOf(System.currentTimeMillis());
    }

}

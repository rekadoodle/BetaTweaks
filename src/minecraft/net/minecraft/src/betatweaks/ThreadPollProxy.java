package net.minecraft.src.betatweaks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import javax.net.ssl.SSLException;

import net.minecraft.src.betatweaks.gui.GuiServerList;

/**
 * @important This class must be compiled with Java 8 
 * 
 * The JohnyMuffin proxy uses TLSv1.2 (or at least higher than TLSv1)
 * Java 8 uses this by default
 * Java 7 has the option to use this but I don't have JDK7 and nobody uses that
 * Java 6 can only use up to TLSv1 so it cannot be used to access this server
 */
public class ThreadPollProxy extends Thread{
	
	public ThreadPollProxy(GuiServerList menu, ServerData server)
    {
    	this.menu = menu;
        this.server = server;
    }
	
	@Override
	public void run() {
		try {
			InputStream inputstream = null;
			try {
				inputstream = new URL(proxyUrl + server.proxyName).openStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(inputstream, Charset.forName("UTF-8")));
				String jsonText = readAll(rd);
				if(References.isInstalled(References.jsonHandler)) {
					try {
						int playerCount = References.jsonHandler.getPlayerCount(jsonText);
						server.playerCount = new StringBuilder("\2477").append(playerCount).append("\2478/\2477?").toString();
						server.status = "Server Online";
						server.responseTime = ServerData.ONLINE;
					}
					catch(Exception e) {
						server.setConnectionFailed(e.getMessage());
					}
				}
				else {
					server.setConnectionFailed("Missing JSON libraries");
				}
				
			} finally {
				if(inputstream != null) inputstream.close();
			}
		}
		catch(SSLException e) {
			if(e.getMessage().equalsIgnoreCase("Received fatal alert: protocol_version")) {
				server.setConnectionFailed("betatweaks.ThreadPollProxy must be compiled in Java 8");
			}
			else {
				handleGenericError(e);
			}
		}
		catch(UnknownHostException e) {
			server.setConnectionFailed("Failed to connect to proxy");
		}
		catch (IOException e) {
			handleGenericError(e);
		}
		finally {
			synchronized(GuiServerList.getSync()) {
				menu.serversBeingPinged--;
			}
		}
	}
	
	private void handleGenericError(Exception e) {
		server.setConnectionFailed("Failed to reach proxy");
		e.printStackTrace();
	}
	
	/**
	 * @author Rolan Illig https://stackoverflow.com/a/4308662
	 * Credit to this guy for this method and some code in run() for reading JSON from a URL
	 */
	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}
	
	private final String proxyUrl = "https://node.johnymuffin.com/request/";
    private final ServerData server;
    private final GuiServerList menu;
}

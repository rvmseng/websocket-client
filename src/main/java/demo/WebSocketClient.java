package demo;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;

@ClientEndpoint
public class WebSocketClient {
	private String SERVER_URI;
	private Session session;

	public WebSocketClient() {
	}

	public WebSocketClient(String url) {
		this.SERVER_URI = url;
	}

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Connected to WebSocket server");
		System.out.println("Session ID is > " + session.getId());
		this.session = session;
	}

	@OnMessage
	public void onMessage(String message) {
		System.out.println("Received message: " + message);
	}

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		System.out.println("Connection closed: " + closeReason);
	}

	public void connect() throws Exception {
		ClientManager client = ClientManager.createClient();

		// Set a custom property if needed (e.g., to configure a proxy)
		// client.getProperties().put(ClientProperties.PROXY_URI,"http://your.proxy.server.url");

		// Connect to the WebSocket server
		client.connectToServer(this, new URI(SERVER_URI));
	}

	public boolean checkSessionIsLive() {
		Session session = getSession();
		return session != null && session.isOpen();
	}

	public void sendMessag(String message) {
		if (checkSessionIsLive()) {
			try {
				getSession().getBasicRemote().sendText(message);
			} catch (IOException e) {
				System.out.println("ERROR: can not send message");
				e.printStackTrace();
			}
		}
	}

	public void closeSession() {
		if (checkSessionIsLive()) {
			// Close the WebSocket connection when done
			try {
				getSession().close();
			} catch (IOException e) {
				System.out.println("ERROR: can not close session");
				e.printStackTrace();
			}
		}
	}

	public Session getSession() {
		return session;
	}
}

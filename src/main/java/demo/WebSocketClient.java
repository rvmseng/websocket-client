package demo;

import java.io.IOException;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

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

		Map<String, String> customHeaders = new HashMap<String, String>();
		customHeaders.put("Connection", "Upgrade");
		customHeaders.put("Sec-Websocket-Extensions", "permessage-deflate; client_max_window_bits");
		customHeaders.put("Sec-Websocket-Key", Helper.generateBse64Random());
		customHeaders.put("Sec-Websocket-Protocol", "js.lightstreamer.com");
		customHeaders.put("Sec-Websocket-Version", "13");
		customHeaders.put("Upgrade", "websocket");
		customHeaders.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36");

		session.getUserProperties().put("headers", customHeaders);

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

		SSLContext sslContext=disableAndGetSSLContext();
		SSLParameters sslParameters = new SSLParameters();
		sslParameters.setEndpointIdentificationAlgorithm("");

		// Connect to the WebSocket server
		/*
		 * client.setDefaultSSLContext(sslContext);
		 * client.setDefaultSSLParameters(sslParameters);
		 */
		
		client.connectToServer(this, new URI(SERVER_URI));
	}

	public boolean checkSessionIsLive() {
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

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
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

	private SSLContext disableAndGetSSLContext() {
		SSLContext sslContext = null;

		try {
			TrustManager[] trustAllCertificates = new TrustManager[] { new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType) {
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType) {
				}
			} };

			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCertificates, new java.security.SecureRandom());

			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sslContext;

	}
}

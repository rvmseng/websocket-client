package client;

import java.io.IOException;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.websocket.ClientEndpoint;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.client.ClientProperties;

@ClientEndpoint
public class WebSocketClient extends Endpoint {
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
		/*
		 * customHeaders.put("Connection", "Upgrade"); customHeaders.put("Origin",
		 * "https://rivas.irfarabi.com"); customHeaders.put("Sec-Websocket-Extensions",
		 * "permessage-deflate; client_max_window_bits");
		 * customHeaders.put("Sec-Websocket-Key", Helper.generateBse64Random());
		 * customHeaders.put("Sec-Websocket-Version", "13");
		 * customHeaders.put("Upgrade", "websocket");
		 * 
		 * customHeaders.put("Sec-Websocket-Protocol", "TLCP-2.1.0.lightstreamer.com");
		 * customHeaders.put("User-Agent",
		 * "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36"
		 * );
		 */

		/*
		 * session.getRequestParameterMap().put("Sec-Websocket-Protocol",
		 * Arrays.asList("TLCP-2.1.0.lightstreamer.com"));
		 * session.getRequestParameterMap().put("X-Array", Arrays.asList("Sample"));
		 */

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
		// client.getProperties().put(ClientProperties.PROXY_URI,"https://127.0.0.1:8080");

		/*
		 * SSLContext sslContext=disableAndGetSSLContext(); SSLParameters sslParameters
		 * = new SSLParameters(); sslParameters.setEndpointIdentificationAlgorithm("");
		 */

		/*
		 * client.setDefaultSSLContext(sslContext);
		 * client.setDefaultSSLParameters(sslParameters);
		 */

		ClientEndpointConfig.Builder configBuilder = ClientEndpointConfig.Builder.create();

		configBuilder.configurator(new ClientEndpointConfig.Configurator() {
			public void beforeRequest(Map<String, List<String>> headers) {
				headers.put("Sec-Websocket-Protocol", Arrays.asList("TLCP-2.1.0.lightstreamer.com"));
			}
		});

		ClientEndpointConfig clientConfig = configBuilder.build();
		client.connectToServer(this, clientConfig, new URI(SERVER_URI));
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

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		onOpen(session);
	}
}

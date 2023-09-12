package example;

import java.util.Scanner;

import client.WebSocketClient;

public class Demo {

	private static WebSocketClient websocket;

	public static void main(String[] args) {

		try {

			System.setProperty("com.sun.security.enableAIAcaIssuers", "true");

			/*
			 * System.setProperty("https.proxyHost", "127.0.0.1");
			 * System.setProperty("https.proxyPort", "8080");
			 * System.setProperty("http.proxyHost", "127.0.0.1");
			 * System.setProperty("http.proxyPort", "8080");
			 */

			// wss://javascript.info/article/websocket/demo/hello/
			// wss://pushengine.ramandtech.com/lightstreamer/

			websocket = new WebSocketClient("wss://pushengine.ramandtech.com/lightstreamer/");
			//websocket = new WebSocketClient("ws://javascript.info/article/websocket/demo/hello/");

			websocket.connect();
			websocket.sendMessag("control\r\n" + "LS_subId=5&LS_op=delete&LS_reqId=13&");
			// getInputFromUser();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getInputFromUser() {
		// Send a message to the server
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.print("\nEnter a message to send (or 'exit' to quit): ");
			String input = scanner.nextLine();
			if ("exit".equalsIgnoreCase(input)) {
				break;
			}
			if (websocket.checkSessionIsLive()) {
				websocket.sendMessag(input);
			} else {
				break;
			}
		}
	}
}

package demo;

import java.util.Scanner;

public class Demo {

	private static WebSocketClient websocket;

	public static void main(String[] args) {

		try {
			// wss://javascript.info/article/websocket/demo/hello/
			// wss://pushengine.ramandtech.com/lightstreamer/

			websocket = new WebSocketClient("wss://pushengine.ramandtech.com/lightstreamer/");
			// websocket = new
			// WebSocketClient("wss://javascript.info/article/websocket/demo/hello/");
			websocket.connect();

			// websocket.sendMessag("Reza");
			getInputFromUser();

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

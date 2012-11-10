import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.zeromq.ZMQ;

public class drmario {

	public static void main(String[] args) throws JSONException {

		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket command = context.socket(ZMQ.REQ);
		ZMQ.Socket state = context.socket(ZMQ.SUB);
		
		System.out.println("Enter game token...");
		Scanner input = new Scanner(System.in);
		String gtkn = input.nextLine();
		
		System.out.println("Enter server...");
		input = new Scanner(System.in);
		String server = input.nextLine();
		
		
		MatchState ms = new MatchState(server);
		new Thread(ms).start();
		GameMove gm = new GameMove(command, gtkn, server, ms);
		new Thread(gm).start();

		
	}
}

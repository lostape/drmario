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
		
		System.out.println("Enter command server...");
		input = new Scanner(System.in);
		String commserver = input.nextLine();
		
		System.out.println("Enter state server...");
		input = new Scanner(System.in);
		String stateserver = input.nextLine();
		
		MatchState ms = new MatchState(stateserver);
		new Thread(ms).start();
		GameMove gm = new GameMove(command, gtkn, commserver);
		new Thread(gm).start();

		
	}
}

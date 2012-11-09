import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;
import org.zeromq.ZMQ;

public class drmario {

	public static void main(String[] args) throws JSONException {

		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket command = context.socket(ZMQ.REQ);
		ZMQ.Socket state = context.socket(ZMQ.SUB);
		
		String clienttoken;
		
		command.connect("tcp://ec2-54-242-48-216.compute-1.amazonaws.com:5557");
		state.connect("tcp://ec2-54-242-48-216.compute-1.amazonaws.com:5556");
		
		System.out.println("Enter game token...");
		Scanner input = new Scanner(System.in);
		String gtkn = input.nextLine();
		
		//connect to game
		JSONObject gameconn = new JSONObject();
		gameconn.put("comm_type", "MatchConnect");
		gameconn.put("match_token", gtkn);
		gameconn.put("team_name", "Team 139");
		gameconn.put("password", "Filosoft02");
	
		System.out.println(gameconn.toString());
		command.send(gameconn.toString().getBytes(), 0);
		
		String response = new String(command.recv(0));
		
		JSONObject reply = new JSONObject(response);
		
		String commtype = reply.getString("comm_type");
		if(commtype == "ErrorResp"){
			System.out.println(reply.getString("error"));
			System.out.println(reply.getString("message"));
			System.exit(1);
		}
		else if(commtype == "MatchConnectResp"){
			if(reply.getString("resp") == "ok"){
				clienttoken = reply.getString("client_token");
			}
		}
		System.out.println(reply.getString("comm_type"));
		
		
		
	}

}

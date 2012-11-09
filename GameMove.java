import org.zeromq.*;
import org.json.*;


public class GameMove implements Runnable {

	ZMQ.Socket command;
	String client;
	String server;
	String gametoken;
	
	public GameMove(ZMQ.Socket c, String gtkn, String serv){
		command = c;
		gametoken = gtkn;
		server = serv;
		
	}
	
	public void gameconnect() throws JSONException{
		
		command.connect("tcp://" + server + ":5557");
			
		JSONObject gameconn = new JSONObject();
		gameconn.put("comm_type", "MatchConnect");
		gameconn.put("match_token", gametoken);
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
				client = reply.getString("client_token");
			}
		}		
		
	}
	
	public void move() throws JSONException{
		JSONObject move = new JSONObject();
		move.put("comm_type", "GameMove");
		move.put("client_token", client);
		move.put("move", "left");
		
		System.out.println(move.toString());
		command.send(move.toString().getBytes(), 0);
		
		System.out.println(new String(command.recv(0)));
	}
		
	@Override
	public void run() {
		
		try {
			gameconnect();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		while(true){
			
			try {
				move();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}

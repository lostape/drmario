import org.zeromq.*;
import org.json.*;


public class GameMove implements Runnable {

	ZMQ.Socket command;
	String client;
	String server;
	String gametoken;
	MatchState state;
	int nextcol;
	
	public GameMove(ZMQ.Socket c, String gtkn, String serv, MatchState ms){
		command = c;
		gametoken = gtkn;
		server = serv;
		state = ms;
		
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
		
		System.out.println(reply.toString());
		
		String commtype = reply.getString("comm_type");		
		if(commtype.equals("ErrorResp")){
			System.out.println(reply.getString("error"));
			System.out.println(reply.getString("message"));
			System.exit(1);
		}
		else if(commtype.equals("MatchConnectResp")){
			if(reply.getString("resp").equals("ok")){
				client = reply.getString("client_token");
			}
		}		
		
	}
	
	public void decideMove(){
		
		if(state.current1.type != null){
			//switch(state.current1.type.charAt(0)){
			//case 'O':
				for(int i = 190; i >= 0; i -= 10){
					for(int j = i; j < (i + 10); j++){
						if(state.board[0][j] == 0){
							nextcol = j;
							return;
						}
					}
				}
			//}
		}
	}
	
	public void move() throws JSONException{
				
		JSONObject m = new JSONObject();
		m.put("comm_type", "GameMove");
		m.put("client_token", client);

		decideMove();

		if(state.current1.col < nextcol){
			m.put("move", "right");				
		}
		else if(state.current1.col > nextcol){
			m.put("move", "left");
		}
		else if(state.current1.col == nextcol){
			m.put("move", "drop");
		}
		
		System.out.println(m.toString());
		command.send(m.toString().getBytes(), 0);
		command.recv(0);
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

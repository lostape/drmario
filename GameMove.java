import org.zeromq.*;
import org.json.*;


public class GameMove implements Runnable {

	ZMQ.Socket command;
	String client;
	String server;
	String gametoken;
	MatchState state;
	int nextcol;
	int nextrot;
	boolean findmove = true;
	int piece;
	
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
	
	public boolean newpiece(){
		if(piece == state.current1.number)
			return false;
		else
			return true;
	}
	
	public boolean value(int[] f){
		return true;
	}
	
	public boolean checkCell(int cell, int value){
		if(state.board[cell] != value){
			return false;
		}
		for(int i = cell % 10; i < cell; i += 10){
			if(state.board[i] == 1){
				return false;
			}
		}
		return true;
	}
	
	public void decideMove(){
		
		if(state.current1.type != null){
			findmove = false;
			int fake[] = state.board;
			
			switch(state.current1.type.charAt(0)){
			case 'O':
				for(int i = 190; i >= 0; i -= 10){
					for(int j = i; j < (i + 9); j++){
						if(checkCell(j, 0) && checkCell(j+1, 0)){
							fake = state.board;
							fake[j] = 1;
							fake[j+1]= 1;
							fake[j-10]=1;
							fake[j+1-10]=1;
							if(value(fake)){
								nextcol = (j+1) % 10;
								nextrot = 0;
							}
							
						}
					}
				}
				break;
			case 'I':
				for(int i = 190; i >= 0; i -= 10){
					for(int j = i; j < (i + 7); j++){
						if(checkCell(j,0) && checkCell(j+1,0) && checkCell(j+2,0) && checkCell(j+3,0)){
							fake = state.board;
							fake[j] = 1;
							fake[j+1]= 1;
							fake[j+2]=1;
							fake[j+3]=1;
							if(value(fake)){
								nextcol = ((j+2) % 10);
								nextrot = 0;
							}
	
						}
						else if(checkCell(j,0)){
							fake = state.board;
							fake[j] = 1;
							fake[j-10]= 1;
							fake[j-20]=1;
							fake[j-30]=1;
							if(value(fake)){
								nextcol = (j % 10);
								nextrot = 1;
							}
						}
						else if(checkCell(j+1,0)){
							fake = state.board;
							fake[j+1] = 1;
							fake[j+1-10]= 1;
							fake[j+1-20]=1;
							fake[j+1-30]=1;
							if(value(fake)){
							nextcol = ((j+1) % 10);
							nextrot = 1;
							}
						}
						else if(checkCell(j+2,0)){
							fake = state.board;
							fake[j+2] = 1;
							fake[j+2-10]= 1;
							fake[j+2-20]=1;
							fake[j+2-30]=1;
							if(value(fake)){
							nextcol = ((j+2) % 10);
							nextrot = 1;
							}
						}
						else if(checkCell(j+3,0)){
							fake = state.board;
							fake[j+3] = 1;
							fake[j+3-10]= 1;
							fake[j+3-20]=1;
							fake[j+3-30]=1;
							if(value(fake)){
							nextcol = ((j+3) % 10);
							nextrot = 1;
							}
								
						}
					}
				}
				break;
			case 'S':
				for(int i = 190; i >= 0; i -= 10){
					for(int j = i; j < (i + 8); j++){
						if(checkCell(j,0) && checkCell(j+1,0) && checkCell(j+2,1)){
							fake = state.board;
							fake[j] = 1;
							fake[j+1]= 1;
							fake[j+1-10]=1;
							fake[j+2-10]=1;
							if(value(fake)){
							nextcol = ((j+1) % 10);
							nextrot = 0;
							}
							
						}
					}
					for(int j = i; j < (i + 9); j++){
						if(checkCell(j,1) && checkCell(j+1,0)){
							fake = state.board;
							fake[j-10] = 1;
							fake[j-20]= 1;
							fake[j+1-10]=1;
							fake[j+1]=1;
							if(value(fake)){
							nextcol = ((j) % 10);
							nextrot = 1;
							}
							
						}
					}
				}
				break;
			case 'Z':
				for(int i = 190; i >= 0; i -= 10){
					for(int j = i; j < (i + 8); j++){
						if(checkCell(j,1) && checkCell(j+1,0) && checkCell(j+2,0)){
							fake = state.board;
							fake[j+1] = 1;
							fake[j+1-10]= 1;
							fake[j+2]=1;
							fake[j-10]=1;
							if(value(fake)){
							nextcol = ((j+1) % 10);
							nextrot = 0;
							}
							
						}
					}
					for(int j = i; j < (i + 9); j++){
						if(checkCell(j,0) && checkCell(j+1,1)){
							fake = state.board;
							fake[j] = 1;
							fake[j-10]= 1;
							fake[j+1-10]=1;
							fake[j+1-20]=1;
							if(value(fake)){
							nextcol = ((j) % 10);
							nextrot = 1;
							}
							
						}
					}
				}
				break;
			case 'L':
				for(int i = 190; i >= 0; i -= 10){
					for(int j = i; j < (i + 8); j++){
						if(checkCell(j,0) && checkCell(j+1,1) && checkCell(j+2,1)){
							fake = state.board;
							fake[j] = 1;
							fake[j-10]= 1;
							fake[j+1-10]=1;
							fake[j+2-10]=1;
							if(value(fake)){
							nextcol = ((j+1) % 10);
							nextrot = 0;
							}
						
						}
						else if(checkCell(j,0) && checkCell(j+1,0) && checkCell(j+2,0)){
							fake = state.board;
							fake[j] = 1;
							fake[j+1]= 1;
							fake[j+2]=1;
							fake[j+2-10]=1;
							if(value(fake)){
							nextcol = ((j+1) % 10);
							nextrot = 2;
							}
							
						}
					}
					for(int j = i; j < (i + 9); j++){
						if(checkCell(j,0) && checkCell(j+1,0)){
							fake = state.board;
							fake[j] = 1;
							fake[j-10]= 1;
							fake[j-20]=1;
							fake[j+1]=1;
							if(value(fake)){
							nextcol = ((j) % 10);
							nextrot = 1;
							}
							
						}
						else if(checkCell(j-10,1) && checkCell(j+1,0)){
							fake = state.board;
							fake[j+1] = 1;
							fake[j+1-10]= 1;
							fake[j+1-20]=1;
							fake[j-20]=1;
							if(value(fake)){
							nextcol = ((j+1) % 10);
							nextrot = 3;
							}
							
						}
					}
				}
				break;
			case 'J':
				for(int i = 190; i >= 0; i -= 10){
					for(int j = i; j < (i + 8); j++){
						if(checkCell(j,1) && checkCell(j+1,1) && checkCell(j+2,0)){
							fake = state.board;
							fake[j-10] = 1;
							fake[j+1-10]= 1;
							fake[j+2-10]=1;
							fake[j+3]=1;
							if(value(fake)){
							nextcol = ((j+1) % 10);
							nextrot = 0;
							}
							
						}
						
						else if(checkCell(j,0) && checkCell(j+1,0) && checkCell(j+2,0)){
							fake = state.board;
							fake[j] = 1;
							fake[j-10]= 1;
							fake[j+1]=1;
							fake[j+2]=1;
							if(value(fake)){
							nextcol = ((j+1) % 10);
							nextrot = 2;
							}
							
						}
						
					}
					for(int j = i; j < (i + 9); j++){
						if(checkCell(j,0) && checkCell(j+1,0)){
							fake = state.board;
							fake[j] = 1;
							fake[j+1]= 1;
							fake[j+1-10]=1;
							fake[j+1-20]=1;
							if(value(fake)){
							nextcol = ((j+1) % 10);
							nextrot = 3;
							}
							
						}
						else if(checkCell(j+1-10,1) && checkCell(j,0)){
							fake = state.board;
							fake[j] = 1;
							fake[j-10]= 1;
							fake[j-20]=1;
							fake[j+1-20]=1;
							if(value(fake)){
							nextcol = ((j) % 10);
							nextrot = 1;
							}
							
						}
					}
				}
				break;
			case 'T':
				for(int i = 190; i >= 0; i -= 10){
					for(int j = i; j < (i + 8); j++){
						if(checkCell(j,1) && checkCell(j+1,0) && checkCell(j+2,1)){
							fake = state.board;
							fake[j-10] = 1;
							fake[j+1-10]= 1;
							fake[j+1]=1;
							fake[j+2-10]=1;
							if(value(fake)){
							nextcol = ((j+1) % 10);
							nextrot = 0;
							}
							
						}
						
						else if(checkCell(j,0) && checkCell(j+1,0) && checkCell(j+2,0)){
							fake = state.board;
							fake[j] = 1;
							fake[j+1]= 1;
							fake[j+2]=1;
							fake[j+1-10]=1;
							if(value(fake)){
							nextcol = ((j+1) % 10);
							nextrot = 2;
							}
							
						}
						
					}
					for(int j = i; j < (i + 8); j++){
						if(checkCell(j,0) && checkCell(j,1)){
							fake = state.board;
							fake[j] = 1;
							fake[j-10]= 1;
							fake[j-20]=1;
							fake[j+1-10]=1;
							if(value(fake)){
							nextcol = ((j) % 10);
							nextrot = 1;
							}
							
						}
						else if(checkCell(j,1) && checkCell(j,0)){
							fake = state.board;
							fake[j-10] = 1;
							fake[j+1-10]= 1;
							fake[j+1-20]=1;
							fake[j+1]=1;
							if(value(fake)){
							nextcol = ((j+1) % 10);
							nextrot = 3;
							}
							
						}
					}
				}
				break;
			default:
				nextcol = 8;
				return;
			}
		}
	}
	
	public void move() throws JSONException{
		
		if(state.pinfo == true){
			JSONObject m = new JSONObject();
			m.put("comm_type", "GameMove");
			m.put("client_token", client);
	
			if(newpiece()){
				decideMove();
				piece = state.current1.number;
			}
			
			if(state.current1.orient < nextrot){
				m.put("move", "lrotate");
			}
			else if(state.current1.orient > nextrot){
				m.put("move", "rrotate");
			}
			else{
				if(state.current1.col < nextcol){
					m.put("move", "right");				
				}
				else if(state.current1.col > nextcol){
					m.put("move", "left");
				}
				else if(state.current1.col == nextcol && state.current1.orient == nextrot){
					m.put("move", "drop");
					findmove = true;
				}
			}
			
			System.out.println(m.toString());
			command.send(m.toString().getBytes(), 0);
			command.recv(0);
			state.pinfo = false;
		}
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

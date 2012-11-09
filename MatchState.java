import org.zeromq.*;
import org.json.*;

public class MatchState implements Runnable {

	String stateserver;
	ZMQ.Socket statesocket;
	String gamename;
	int seq;
	double time;
	int[][] board;
	Piece current1;
	Piece current2;
	String[] queue;
	
	
	
	public MatchState(String server){
		stateserver = server;
		statesocket = ZMQ.context(1).socket(ZMQ.SUB);
		statesocket.connect("tcp://" + stateserver + ":5556");
		board = new int[2][200];
		
	}
	
	private void parseboardState(JSONObject b) throws NumberFormatException, JSONException{
		gamename = b.getString("game_name");
		seq = b.getInt("sequence");
		time = b.getDouble("timestamp");

		JSONObject states = b.getJSONObject("states");		
		JSONObject client1 = states.getJSONObject("client1");
		updateboard(client1.getString("board_state"), 0);
		JSONObject client2 = states.getJSONObject("client2");
		updateboard(client2.getString("board_state"),1);
		
		
	}
	
	private void parsePieceState(JSONObject p) throws JSONException{
		seq = p.getInt("sequence");
		time = p.getDouble("timestamp");
		
		JSONObject states = p.getJSONObject("states");
		JSONObject client1 = states.getJSONObject("client1");
		current1.setOrient(client1.getInt("orient"));
		current1.setType(client1.getString("piece"));
		current1.setNumber(client1.getInt("number"));
		current1.setRow(client1.getInt("row"));
		current1.setCol(client1.getInt("col"));
		
		JSONObject client2 = states.getJSONObject("client2");
		current2.setOrient(client2.getInt("orient"));
		current2.setType(client2.getString("piece"));
		current2.setNumber(client2.getInt("number"));
		current2.setRow(client2.getInt("row"));
		current2.setCol(client2.getInt("col"));
		
		JSONArray q = p.getJSONArray("queue");
		for(int i = 0; i < q.length(); i++){
			queue[i] = q.getString(i);
		}
		
	}
	
	private void updateboard(String hex, int client){
		for(int i = 0, j = 0; i < hex.length(); i++, j+=4){
			switch(hex.charAt(i)){
			case '0':
				board[client][j] = 0;
				board[client][j+1] = 0;
				board[client][j+2] = 0;
				board[client][j+3] = 0;
				break;
			case '1':
				board[client][j] = 1;
				board[client][j+1] = 0;
				board[client][j+2] = 0;
				board[client][j+3] = 0;
				break;
			case '2':
				board[client][j] = 0;
				board[client][j+1] = 1;
				board[client][j+2] = 0;
				board[client][j+3] = 0;
				break;
			case '3':
				board[client][j] = 1;
				board[client][j+1] = 1;
				board[client][j+2] = 0;
				board[client][j+3] = 0;
				break;
			case '4':
				board[client][j] = 0;
				board[client][j+1] = 0;
				board[client][j+2] = 1;
				board[client][j+3] = 0;
				break;
			case '5':
				board[client][j] = 1;
				board[client][j+1] = 0;
				board[client][j+2] = 1;
				board[client][j+3] = 0;
				break;
			case '6':
				board[client][j] = 0;
				board[client][j+1] = 1;
				board[client][j+2] = 1;
				board[client][j+3] = 0;
				break;
			case '7':
				board[client][j] = 1;
				board[client][j+1] = 1;
				board[client][j+2] = 1;
				board[client][j+3] = 0;
				break;
			case '8':
				board[client][j] = 0;
				board[client][j+1] = 0;
				board[client][j+2] = 0;
				board[client][j+3] = 1;
				break;
			case '9':
				board[client][j] = 1;
				board[client][j+1] = 0;
				board[client][j+2] = 0;
				board[client][j+3] = 1;
				break;
			case 'A':
				board[client][j] = 0;
				board[client][j+1] = 1;
				board[client][j+2] = 0;
				board[client][j+3] = 1;
				break;
			case 'B':
				board[client][j] = 1;
				board[client][j+1] = 1;
				board[client][j+2] = 0;
				board[client][j+3] = 1;
				break;
			case 'C':
				board[client][j] = 0;
				board[client][j+1] = 0;
				board[client][j+2] = 1;
				board[client][j+3] = 1;
				break;
			case 'D':
				board[client][j] = 1;
				board[client][j+1] = 0;
				board[client][j+2] = 1;
				board[client][j+3] = 1;
				break;
			case 'E':
				board[client][j] = 0;
				board[client][j+1] = 1;
				board[client][j+2] = 1;
				board[client][j+3] = 1;
				break;
			case 'F':
				board[client][j] = 1;
				board[client][j+1] = 1;
				board[client][j+2] = 1;
				board[client][j+3] = 1;
				break;	
			}
		}
	}
	
	@Override
	public void run() {
		while(true){
			try {
				JSONObject state = new JSONObject(new String(statesocket.recv(0)));
				
				String commtype = state.getString("comm_type");
				if(commtype == "Gameboard[client]State"){
					parseboardState(state);
				}
				else if(commtype == "GamePieceState"){
					parsePieceState(state);
				}
				else if(commtype == "GameEnd"){
					
				}
				else if(commtype == "MatchEnd"){
					System.exit(1);
				}		
				
				
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}

}

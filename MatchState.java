import org.zeromq.*;
import org.json.*;

public class MatchState implements Runnable {

	String stateserver;
	ZMQ.Socket statesocket;
	String gamename;
	String match;
	int seq;
	double time;
	int[][] board;
	Piece current1;
	String[] queue;
	boolean binfo = false;
	boolean pinfo = false;
	
	
	
	public MatchState(String server, String mtkn){
		stateserver = server;
		match = mtkn;
		statesocket = ZMQ.context(1).socket(ZMQ.SUB);
		statesocket.connect("tcp://" + stateserver + ":5556");
		statesocket.subscribe(match.getBytes());
		board = new int[2][200];
		current1 = new Piece();
		queue = new String[5];
		
		
	}
	
	private void parseboardState(JSONObject b) throws NumberFormatException, JSONException{
		gamename = b.getString("game_name");
		seq = b.getInt("sequence");
		time = b.getDouble("timestamp");

		JSONObject states = b.getJSONObject("states");		
		JSONObject client1 = states.getJSONObject("Team 139");
		updateboard(client1.getString("board_state"), 0);
		
		
	}
	
	private void parsePieceState(JSONObject p) throws JSONException{
		seq = p.getInt("sequence");
		time = p.getDouble("timestamp");
		
		JSONObject states = p.getJSONObject("states");
		JSONObject client1 = states.getJSONObject("Team 139");
		current1.setOrient(client1.getInt("orient"));
		current1.setType(client1.getString("piece"));
		current1.setNumber(client1.getInt("number"));
		current1.setRow(client1.getInt("row"));
		current1.setCol(client1.getInt("col"));
		
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
				String filter = new String(statesocket.recv(0));
				String json = new String(statesocket.recv(0));
				//System.out.println(json);
				
				JSONObject state = new JSONObject(json);
				
				String commtype = state.getString("comm_type");

				if(commtype.equals("GameboardState")){
					parseboardState(state);
					binfo = true;
					System.out.println(binfo);
				}
				else if(commtype.equals("GamePieceState")){
					parsePieceState(state);
					pinfo = true;
				}
				else if(commtype.equals("GameEnd")){
					
				}
				else if(commtype.equals("MatchEnd")){
					System.exit(1);
				}		
				
				
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}

}

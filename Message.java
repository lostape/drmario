import org.zeromq.*;



public class Message {

	public static void main(String[] args) {
	
		ZMQ.Context context = ZMQ.context(1);
		ZMQ.Socket socket = context.socket(ZMQ.REQ);
		
		System.out.println("HELLO");
		
		socket.connect("tcp://ec2-50-19-179-117.compute-1.amazonaws.com:5557");
		
		String requestString = "Hello";
        byte[] request = requestString.getBytes();
        //request[request.length-1]=0; //Sets the last byte to 0
        // Send the message
        System.out.println("Sending request " + "É");
        socket.send(request, 0);

        //  Get the reply.
        byte[] reply = socket.recv(0);
        //  When displaying reply as a String, omit the last byte because
        //  our "Hello World" server has sent us a 0-terminated string:
        System.out.println("Received reply " + ": [" + new String(reply,0,reply.length-1) + "]");

	}

}

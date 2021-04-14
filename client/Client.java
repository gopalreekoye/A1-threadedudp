//Reekoye Gopal
//
//05/04/2021
//
//
//
//Client.java


package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

import utils.*;

//Class to handle multi threaded instance of a UDP Client
//


public class Client
{
// 	private Connection connection;
	private  boolean running = true;
	private  String serverAddress = "localhost";
	private  int serverPort = 1434;
	private  DatagramSocket socket;
	private  String text = "";
	private  String currentDest = "server";
	private  User user;
	private  String username;
	public   String[] out = new String[2];
	public 	 boolean isNewMessage = false;
	public	 String [] onlineUsers;
// 	//private Thread process, send, receive;

	public Client(String username){
		this.username = username;
		System.out.println("Client created");
	}

	public String getText(){
		return text;
	}
	public void setText(String txt){
		text = txt;
	}

	public  void run()
	{
		try{
			socket = new DatagramSocket();
			user = createUser();
				
			
			Thread listener = new Thread(
            new Runnable(){
                @Override
                public void run(){
					listen();
                }
            });
			listener.start();
			send(new Message(user, true, "CU","server"));
			
			

		}catch (SocketException | UnknownHostException e){
			e.printStackTrace();
		}
		
	}



	public  void listen(){
		while(running){	
			try {
				byte[] buffer = new byte[1024];
				DatagramPacket dgpacket = new DatagramPacket(buffer, buffer.length);
				socket.receive(dgpacket);
				Message result = new Message(new String(dgpacket.getData()));
				if(result.getCommand()){
					handleCommand(result);
				}else{
					handleMessage(result);	
				}
				
				
			} catch (IOException e) {
				
				e.printStackTrace();
			
			}
		}
	}

	public void getConnectedUsers(){
		System.out.println("Getting connected users");
		send(new Message(user, true, ":l","server"));
	}

	public  void send(Message message){
		byte[] sendbuffer = message.getString().getBytes();
		DatagramPacket sendPacket;
		try {
			sendPacket = new DatagramPacket(sendbuffer, sendbuffer.length,InetAddress.getByName(serverAddress) , serverPort);
			socket.send(sendPacket);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e) {	
			e.printStackTrace();
		}
		
	}

	public void send(String message){
		send(new Message(user,false,message,currentDest));
	}

	public  User createUser() throws UnknownHostException{	
		return new User(socket.getLocalPort(),InetAddress.getLocalHost().getHostAddress(), username);
	}

	public  void handleCommand(Message result){
		System.out.println("Handling command");
		if(result.getText().equals(":q")){
			System.out.println("bye!");
			running = false;
		}
		else if(result.getText().startsWith(":l")){
			System.out.println("got connected users");
			
			String nameId = result.getText();
			if(nameId.length() > 3){
				nameId = nameId.substring(3);
			}
			else{
				nameId = nameId.substring(2);
			}

			String [] content = nameId.split("#");
			onlineUsers = new String [content.length];
			for(int i = 0; i < content.length; i++){
				onlineUsers[i] = content[i];
			}
			System.out.println(Arrays.toString(onlineUsers));
		}
		else{
			System.out.println("Server: "+result.getText());
		}
	}

	public  void handleMessage(Message result){
		if(!result.getUser().getId().equals(user.getId())){
			out[0] = result.getUser().getId()+":"+ result.getUser().getUsername(); 
			out[1] = result.getUser().getUsername()+" : "+result.getText();
			isNewMessage = true;
			System.out.println("id : "+ out[0]);
		}
		else
		{

		}
	}

	public void setDestination(String dest){
		currentDest = dest;
	}

	public String decoupleAddress(String str){
		String [] id = str.split(":");
		return id[0].trim();
	}
}

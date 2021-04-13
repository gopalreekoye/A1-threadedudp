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
import java.util.Scanner;

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
	public   String  out = "";
	public 	 boolean isNewMessage = false;
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
			send(new Message(user, true, "CU","server"));	
			listen();
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

	public  User createUser() throws UnknownHostException{	
		return new User(socket.getLocalPort(),InetAddress.getLocalHost().getHostAddress(), username);
	}

	public  void handleCommand(Message result){
		if(result.getText().equals(":q")){
			System.out.println("bye!");
			running = false;
		}
		else if(result.getText().startsWith(":l")){
			System.out.println(result.getText().substring(3).trim());
		}
		else{
			out = "Server: "+result.getText();
		}
		isNewMessage = true;
	}

	public  void handleMessage(Message result){
		if(!result.getUser().getId().equals(user.getId())){
			out = result.getUser().getUsername()+" : "+result.getText();
			isNewMessage = true;
		}
	}
}

package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import utils.*;

//Server class for handling a multi- threaded inistance of a UDP server
//

public class Server
{
// 	//Server information
// 	//
	private int port;
 	private DatagramSocket socket;
 	private boolean running = true;

// 	//threads to handle processes
// 	//private Thread send, receive, process;

// 	//Client arraylist
	public static ArrayList<User> CLIENTS = new ArrayList<User>();


// 	//Constructor for server
// 	//@@param port
	
	public Server(int port)
	{
		this.port = port;
		try {
			socket = new DatagramSocket(this.port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}



	public void listen(){
		System.out.println("Server is listening on port: "+this.port);
		while(running){	
			try {
				byte[] buffer = new byte[1024];
				DatagramPacket dgpacket = new DatagramPacket(buffer, buffer.length);
				socket.receive(dgpacket);
			
				Message request = new Message(new String(dgpacket.getData()));
				if(request.getCommand()){
					System.out.println("handlin command");
					handleCommand(request);
				}
				else{
					System.out.println("distributing message");
					distributeMessage(request);
				}
				

			} catch (IOException e) {
				
				e.printStackTrace();
			
			}
		}
	}
	public void send(Message message, User user) throws UnknownHostException{
		byte [] buffer = message.getString().getBytes();
		DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(user.getAddress()), user.getPort());
		try {
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(Message message) throws UnknownHostException{
		byte [] buffer = message.getString().getBytes();
		DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(message.getUser().getAddress()), message.getUser().getPort());
		try {
			socket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void distributeMessage(Message message){
		String destination = message.getDestination();
		System.out.println("destination: "+destination);
		if(!destination.equals("server")){
			System.out.println("non server branch");
			User destUser = searchClientById(destination);
			if(destUser != null){
				try {
					System.out.println("non server dest");
					send(message,destUser);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void handleCommand(Message request) throws UnknownHostException{
		if(request.getText().equals("CU")){
			CLIENTS.add(request.getUser());
			send(new Message(request.getUser(), true,"registered" , request.getUser().getId()));
		}
		else if(request.getText().equals(":q")){
			CLIENTS.remove(request.getUser());
			send(new Message(request.getUser(), true,":q" , request.getUser().getId()));
			
		}
		else if(request.getText().equals(":l")){
			send(new Message(request.getUser(), true, getAvailableUsers(), request.getUser().getId()));
		}
	}

	public User searchClientById(String id){
		for(int i = 0; i < CLIENTS.size(); i++){
			if(CLIENTS.get(i).getId().equals(id)){
				return CLIENTS.get(i);
			}
		}
		return null;
	}

	public String getAvailableUsers(){
		String list = "";
		for(User client : CLIENTS){
			list += "& "+ client.getId()+":"+client.getUsername();
			list = ":l "+list;
		}
		return list;
	}
}
 
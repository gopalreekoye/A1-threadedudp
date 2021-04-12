package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import data.Connection;
import data.Packet;
import data.PacketHandler;
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


// 	//Server initialisation
// 	//@throws SocketException
// 	//
// 	/*public void init() throws SocketException
// 	{
// 		this.socket = new DatagramSocket(this.port);
// 		process = new Thread(this, "server_process");
// 		process.start();
// 	}
// 	*/
// 	//get port the server is binded to
// 	//@return port
// 	//
// 	public int getPort()
// 	{
// 		return port;
// 	}

// 	//Send a packet to client
// 	//
// 	public void send(final Packet packet)
// 	{
// 		//send = new Thread("send_thread"){
// 		//	public void run(){
// 				DatagramPacket dgpack = new DatagramPacket(
// 						packet.getData(),
// 						packet.getData().length,
// 						packet.getAddr(),
// 						packet.getPort()
// 				);

// 				try{
// 					socket.send(dgpack);

// 				}catch(IOException e)
// 				{
// 					System.out.println(e.getMessage());
// 				}
// 		//	}
// 	//	};
// 	//	send.start();
// 	}

// 	//broadcast packet to all connected users
// 	//@param packet
// 	//
// 	public void broadcast(byte[] data)
// 	{
// 		for(Connection c : CLIENTS)
// 		{
// 			send(new Packet(data, c.getAddress(), c.getPort()));
// 		}
// 	}

// 	//receiving packet from clients
// 	//
// 	public void receive()
// 	{
// 	//	receive = new Thread("receive_thread")
// 	//	{
// 	//		public void run(){
// 				while(running)
// 				{
					
					// try {
					// 	byte[] buffer = new byte[1024];
					// 	DatagramPacket dgpacket = new DatagramPacket(buffer, buffer.length);
					// 	socket.receive(dgpacket);
					// 	Packet recievedPacket = new Packet(dgpacket.getData(), dgpacket.getAddress(), dgpacket.getPort());

					// 	if(dgpacket.getData().length > 5){
					// 		this.send(recievedPacket);
					// 	}

					// } catch (IOException e) {
						
					// 	e.printStackTrace();
					
					// }					

// 					//handler.process(new Packet(dgpacket.getData(), dgpacket.getAddress(), dgpacket.getPort()));

// 				}
// 		//	}
// 	//	};

// 	//	receive.start();
// 	}

// 	//run method for runnable thread
// 	//
// 	/*public void run()
// 	{
// 		running = true;
// 		System.out.println("Server started on port " + port);
// 		this.receive();
// 	}*/
	public void listen(){
		System.out.println("Server is listening on port: "+this.port);
		while(running){	
			try {
				byte[] buffer = new byte[1024];
				DatagramPacket dgpacket = new DatagramPacket(buffer, buffer.length);
				socket.receive(dgpacket);
			
				Message request = new Message(new String(dgpacket.getData()));
				if(request.getCommand()){
					if(request.getText().equals("CU")){
						CLIENTS.add(request.getUser());
						send(new Message(request.getUser(), true,"registered" , request.getUser().getId()));
					}
					else if(request.getText().equals(":q")){
						send(new Message(request.getUser(), true,":q" , request.getUser().getId()));
						CLIENTS.remove(request.getUser());
					}
					else if(request.getText().equals(":l")){
						send(new Message(request.getUser(), true, getAvailableUsers(), request.getUser().getId()));
					}
				}
				else
				
				distributeMessage(request);

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
			list += ":l & "+ client.getId()+":"+client.getUsername();
		}
		return list;
	}
}

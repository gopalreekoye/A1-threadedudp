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

import data.Connection;
import data.Packet;
import data.PacketHandler;
import server.Server;


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
// 	//private Thread process, send, receive;

	public  void main(String [] args)
	{
		try{
			socket = new DatagramSocket();
			user = createUser();
			send(new Message(user, true, "CU","server"));
			Thread commandline = new Thread(
				new Runnable(){
					@Override
					public void run(){
						Scanner input = new Scanner(System.in);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		
						while(!text.equals(":q"))
						{
							System.out.print("You: ");
							text = input.nextLine();
							if(text.startsWith(":")){
								if(text.startsWith(":d")){
									String selection = text.substring(2);
									currentDest = selection.trim();
									System.out.println("Destination changed to " + currentDest);
								}
								else if(text.startsWith(":l")){
									try {
										send(new Message(user, true, text,"server"));
										Thread.sleep(1000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
								else{
									send(new Message(user, true, text,"server"));
								}
								
							}else{
								send(new Message(user, false, text,currentDest));
							}
						}
						send(new Message(user, true, ":q","server"));
						
					}
				}
			);
			commandline.start();
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
		Scanner input = new Scanner(System.in);
		System.out.println("Enter username");
		String username = input.nextLine();

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
			System.out.println("Server: "+result.getText());
		}
	}

	public  void handleMessage(Message result){
		if(!result.getUser().getId().trim().equals(user.getId().trim())){
			System.out.println(result.getUser().getUsername()+" : "+result.getText());
		}
	}
}

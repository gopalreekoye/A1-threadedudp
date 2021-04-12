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
	private static boolean running = true;
	private static String serverAddress = "localhost";
	private static int serverPort = 1434;
	private static DatagramSocket socket;
	private static String text = "";
	private static String currentDest = "server";
// 	//private Thread process, send, receive;

	public static void main(String [] args)
	{
		try{
			socket = new DatagramSocket();
			User user = createUser();
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
									System.out.println("Destination changed to" + currentDest);
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
								
							}
							send(new Message(user, false, text,currentDest));
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

// 	//Client initialisation
// 	//
// 	/*private void init()
// 	{
// 		process = new Thread(this, "server_process");
// 		process.start();
// 	}
// 	*/
// 	//send data 

// 	public void send(final byte[] data)
// 	{
// 		//send = new Thread("Sending Thread"){
// 		//	public void run()
// 		//	{
// 				connection.send(data);
// 		//	}
// 		//};
// 		//send.start();
// 	}

// 	//Receive data on the given server connection
// 	//
// 	public String receive()
// 	{
//        String s = "";
// 		//receive = new Thread("receive_thread"){
// 			//public void run()
// 			//{
// 				while(running)
// 				{
// 					byte[] buffer = new byte[1024];
// 					DatagramPacket dgpacket = new DatagramPacket(buffer, buffer.length);

// 					try{
// 						socket.receive(dgpacket);
//                   	s = new String(dgpacket.getData());
                  
// 					}catch(IOException e)
// 					{
// 						e.printStackTrace();
// 					}
					
// 					if(s.length() > 2){
// 						break;
// 					}

// 					//handler.process(new Packet(dgpacket.getData(), dgpacket.getAddress(), dgpacket.getPort()));

// 				}
// 			//}  
          

// 		//};

// 		//receive.start();
// 		return s;
// 	}

// 	//close current connection
// 	//
// 	public void close()
// 	{
// 		connection.close();
// 		running = false;
// 	}

// 	@Override
// 	public void run()
// 	{
// 		running = true;
// 	}

	public static void listen(){
		while(running){	
			try {
				byte[] buffer = new byte[1024];
				DatagramPacket dgpacket = new DatagramPacket(buffer, buffer.length);
				socket.receive(dgpacket);
				Message result = new Message(new String(dgpacket.getData()));
				if(result.getCommand()){
					if(result.getText().equals("CU")){
						//
					}
					else if(result.getText().equals(":q")){
						System.out.println("bye!");
						break;
					}
					else if(result.getText().startsWith(":l")){
						System.out.println(result.getText().substring(3).trim());
					}
					else{
						System.out.println("Server: "+result.getText());
					}
				}else{
					
				}
				
				
			} catch (IOException e) {
				
				e.printStackTrace();
			
			}
		}
	}

	public static void send(Message message){
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

	public static User createUser() throws UnknownHostException{
		Scanner input = new Scanner(System.in);
		System.out.println("Enter username");
		String username = input.nextLine();

		return new User(socket.getLocalPort(),InetAddress.getLocalHost().getHostAddress(), username);
		
	}
}

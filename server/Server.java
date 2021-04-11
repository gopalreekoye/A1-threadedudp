package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

import data.Connection;
import data.Packet;

//Server class for handling a multi- threaded inistance of a UDP server
//

public class Server implements Runnable
{
	//Server information
	//
	private int port;
	private DatagramSocket socket;
	private boolean running;
   String s;

	//threads to handle processes
	private Thread send, receive, process;

	//Client arraylist
	public static ArrayList<Connection> CLIENTS = new ArrayList<Connection>();


	//Constructor for server
	//@@param port
	
	public Server(int port)
	{
		this.port = port;
		
		try{
			this.init();
		}catch (SocketException e)
		{
			System.err.println("Unable to inintialise the server..."+ e.getMessage());
		}
	}


	//Server initialisation
	//@throws SocketException
	//
	public void init() throws SocketException
	{
		this.socket = new DatagramSocket(this.port);
		process = new Thread(this, "server_process");
		process.start();
	}

	//get port the server is binded to
	//@return port
	//
	public int getPort()
	{
		return port;
	}

	//Send a packet to client
	//
	public void send(final Packet packet)
	{
		send = new Thread("send_thread"){
			public void run(){
				DatagramPacket dgpack = new DatagramPacket(
						packet.getData(),
						packet.getData().length,
						packet.getAddr(),
						packet.getPort()
				);

				try{
					socket.send(dgpack);

				}catch(IOException e)
				{
					System.out.println(e.getMessage());
				}
			}
		};
		send.start();
	}

	//broadcast packet to all connected users
	//@param packet
	//
	public void broadcast(byte[] data)
	{
		for(Connection c : CLIENTS)
		{
			send(new Packet(data, c.getAddress(), c.getPort()));
		}
	}

	//receiving packet from clients
	//
	public String receive()
	{
     
		receive = new Thread("receive_thread")
		{
         
			public void run(){
				while(running)
				{
					byte[] buffer = new byte[1024];
					DatagramPacket dgpacket = new DatagramPacket(buffer, buffer.length);

					try{
						socket.receive(dgpacket);
                  s= new String(dgpacket.getData());
					}
					catch(IOException e)
					{
						e.printStackTrace();
					}

					
				}
			}
		};

		receive.start();
      return s;
	}

	//run method for runnable thread
	//
	public void run()
	{
		running = true;
		System.out.println("Server started on port " + port);
	}
}

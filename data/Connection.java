//Reekoye Gopal
//
//07/04/2021
//
//Connection.java


package data;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Connection
{
	private InetAddress addr;
	private int port, id;
	private DatagramSocket clientSocket;


	//new server Connection constructor
	//
	public Connection(DatagramSocket socket, InetAddress addr, int port, int id)
	{
		this.addr=addr;
		this.port=port;
		this.clientSocket = socket;
		this.setId(id);
	}

	//Sending data on this connection
	//
	public void send(byte[] data)
	{
		DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
		try{
		
			clientSocket.send(packet);
		}catch(IOException e){
		
			e.printStackTrace();
		}
	}

	//Receiving data on this connection
	//
	public byte[] receive()
	{
		byte[] buffer = new byte[1024];
		DatagramPacket packet =new DatagramPacket(buffer, buffer.length);

		try{
			clientSocket.receive(packet);
		}catch (IOException e){
			e.printStackTrace();
		}

		byte[] data = packet.getData();
		return data;
	}

	//get prt number
	//
	public int getPort()
	{
		return this.port;
	}

	//get the address of this connection
	//
	public InetAddress getAddress()
	{
		return this.addr;
	}

	//close connection
	//
	public void close()
	{
		new Thread()
		{
			public void run()
			{
				synchronized(clientSocket)
				{
					clientSocket.close();
				}
			}
		}.start();
	}


	//get id
	//
	
	public int getId()
	{
		return id;
	}

	//set id
	//
	public void setId(int id)
	{
		this.id = id;
	}



}



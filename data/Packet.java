package data;


import java.net.InetAddress;

public class Packet
{
	private byte[] data;
	private InetAddress ip;
	private int port;
	private Connection conn;


	//New Packet construction
	//
	public Packet(byte[] data, Connection receiver)
	{
		this.data = data;
		this.conn= receiver;
	}

	//create packet using client information
	//
	public Packet(byte[] data, InetAddress ip, int port)
	{
		this.data = data;
		this.ip = ip;
		this.port = port;
		this.conn = new Connection(null, ip, port, 10);
	}

	//get data from packet
	//
	public byte[] getData()
	{
		return data;
	}

	//get the ip
	//
	public InetAddress getAddr()
	{
		return ip;
	}

	//get port
	//
	public int getPort()
	{
		return port;
	}


	//get the connection on which this packet was sent
	//
	public Connection getConnection()
	{
		return this.conn;
	}


	@Override
	public String toString()
	{
		return "Data: "+ new String(this.data)+"\n From: "+getConnection().getAddress()+":"+getConnection().getPort();
	}
}

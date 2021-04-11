package main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import client.Client;
import server.Server;
import data.Connection;
import data.Packet;
import data.PacketHandler;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.lang.String;


public class testmainServer
{
   public static void main(String[] args) throws Exception
   {
      Server server= new Server(9999);
      String str = server.receive();
      System.out.println("server:"+str);
      InetAddress ia = InetAddress.getLocalHost();
      String str1= "Server:Connection received";
      byte[]b2 = str1.getBytes();
      Packet packet= new Packet(b2, ia, 1900);
      server.send(packet);
   }
}
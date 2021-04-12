package main;

import server.Server;

public class testmainServer
{
   public static void main(String[] args)
   {
      Server server = new Server(1434);
      server.listen(); 
      
   }
}
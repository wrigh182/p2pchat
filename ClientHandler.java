/**
 * ClientHandler.java
 *
 * This class handles communication between the client
 * and the server.  It runs in a separate thread but has a
 * link to a common list of sockets to handle broadcast.
 *
 */
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class ClientHandler implements Runnable
{
	private Socket connectionSock = null;
	private ArrayList<Socket> socketList;

	ClientHandler(Socket sock, ArrayList<Socket> socketList)
	{
		this.connectionSock = sock;
		this.socketList = socketList;	// Keep reference to master list
	}

	public void run()
	{
        // Send client list of other clients and wait for response
		try
		{
			System.out.println("Connection made with socket " + connectionSock);
			BufferedReader clientInput = new BufferedReader(new InputStreamReader(connectionSock.getInputStream()));
			DataOutputStream clientOutput = new DataOutputStream(connectionSock.getOutputStream());
			
			// Send each socket in array to client
			sendList(clientOutput);

			while (true)
			{
				// Wait and get client message
				String clientText = clientInput.readLine();

				// Code: Send List
				if (clientText.equals("200"))
				{
					sendList(clientOutput);
				}

				// Code: Exit
				if (clientText.equals("300"))
				{
					// Send exit code back to client
					clientOutput.writeBytes("300\n");

					// Client has dissconnected 
					System.out.println("Closing connection for socket " + connectionSock);
					// Remove from arraylist
					socketList.remove(connectionSock);
					connectionSock.close();
					break;
				}

				// Connection was lost
				else if (clientInput == null)
				{ 
				  System.out.println("Closing connection for socket " + connectionSock);
				   // Remove from arraylist
				   socketList.remove(connectionSock);
				   connectionSock.close();
				   break;
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
			// Remove from arraylist
			socketList.remove(connectionSock);
		}
	}

	public void sendList(DataOutputStream thisClientOutput)
	{
		try
		{
			// Send length of list to client
			thisClientOutput.writeBytes("listLength" + (socketList.size()-1) + "\n");
			// Send each socket in list to client
			for (Socket s : socketList)
			{
				if (s != connectionSock)
				{
					thisClientOutput.writeBytes(s.getRemoteSocketAddress().toString() + "\n");
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
			// Remove from arraylist
			socketList.remove(connectionSock);
		}
	}

} // ClientHandler for MTServer.java

/**
 * ServerReader.java
 *
 * This class runs on the client end and just
 * displays any text received from the server.
 *
 */
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

public class ServerReader implements Runnable
{
	private ThreadAssist threadAssist;
	private Socket connectionSock = null;
	private ArrayList<String> socketAddrList;

	ServerReader(Socket sock, ArrayList<String> list, ThreadAssist threadAssist)
	{
		this.threadAssist = threadAssist;
		this.connectionSock = sock;
		this.socketAddrList = list; // reference to main list
	}

	public void run()
	{
       		 // Wait for data from the server.  If received, output it.
		try
		{
			BufferedReader serverInput = new BufferedReader(new InputStreamReader(connectionSock.getInputStream()));

			while (true)
			{
			// Get data sent from the server
			String serverText = serverInput.readLine();

				// Code: exit
				if (serverText.equals("300"))
				{
					connectionSock.close();
					break;
				}

				// Check if list is being sent
				else if (serverText.length() > 10 && serverText.substring(0 , 10).equals("listLength"))
				{
					// Save list length
					int listLength = Integer.parseInt(serverText.substring(10));

					// Print number of other clients AKA list length
					System.out.println("\n\n");
					if (listLength > 1) System.out.println("There are " + listLength + " other users:");
					else if (listLength == 1) System.out.println("There is 1 other user:");
					else System.out.println("There are no other users");

					// Clear existing list
					socketAddrList.clear();

					// Recieve list and print
					for (int i = 0; i < listLength; ++i)
					{
						serverText = serverInput.readLine();
						socketAddrList.add(serverText);

						System.out.println((i+1) + ")  " + serverText.substring(1));
					}

					// Prompt for peer selection
					System.out.println("\nEnter the number of the client you would like to connect to,");
					System.out.println("or enter \"r\" to refresh list of clients");
					threadAssist.setState(ThreadAssist.State.SELECT);
				}

				else if (serverInput == null)
				{
					// Connection was lost
					System.out.println("Closing connection for socket " + connectionSock);
					connectionSock.close();
					break;
				}
			

			}

		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
	}
	public Socket makeSocket(String address)
	{
		try
		{
			String hostname = address.substring(1, address.indexOf(':'));
			int port = Integer.parseInt(address.substring(address.indexOf(':')+1)) + 1000;
			Socket tempSock = new Socket(hostname, port);
			return tempSock;
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
			return null;
		}

	}

} // ServerReader for MTClient

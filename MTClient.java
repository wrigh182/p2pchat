/**
 * MTClient.java
 *
 * This program implements a simple multithreaded chat client.  It connects to the
 * server (assumed to be localhost on port 7654) and starts two threads:
 * one for listening for data sent from the server, and another that waits
 * for the user to type something in that will be sent to the server.
 * Anything sent to the server is broadcast to all clients.
 *
 * The MTClient uses a ServerReader whose code is in a separate file.
 * The ServerReader runs in a separate thread, recieves messages form the server,
 * and displays them on the screen.
 *
 * Data received is sent to the output screen, so it is possible that as
 * a user is typing in information a message from the server will be
 * inserted.  
 *
 */
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;



public class MTClient
{
	private ThreadAssist threadAssist;
	private ArrayList<String> socketAddrList;

	public MTClient ()
	{
		threadAssist = new ThreadAssist();
		threadAssist.setState(ThreadAssist.State.SERVER);
		socketAddrList = new ArrayList<String>();

	}
	
	public void runClient()
	{
		String data;
		boolean skipFirstSend = false;
		try
		{
			String hostname = "localhost";
			int port = 7654;



			System.out.println("\nConnecting to server on port " + port);
			Socket connectionSock = new Socket(hostname, port);

			DataOutputStream serverOutput = new DataOutputStream(connectionSock.getOutputStream());

			System.out.println("Connection made.");


			// Start a thread to listen and display data sent by the server
			ServerReader listener = new ServerReader(connectionSock, this.socketAddrList, this.threadAssist);
			Thread listenerThread = new Thread(listener);
			listenerThread.start();

			// Start a thread to listen for peer connection on (current port + 1000)
			PeerListener peerListener = new PeerListener(connectionSock.getLocalPort() + 1000, this.threadAssist);
			Thread peerListenerThread = new Thread(peerListener);
			peerListenerThread.start();

			// Read input from the keyboard and send it to appropriate location.
			// The only way to quit is to hit control-c
			Scanner keyboard = new Scanner(System.in);
			while (true)
			{
				// Get keyboard input
				data = keyboard.nextLine();

				// Send text to Server
				if (threadAssist.getState() == ThreadAssist.State.SERVER)
				{
					serverOutput.writeBytes(data + "\n");
				}

				// Select from options list
				else if (threadAssist.getState() == ThreadAssist.State.SELECT)
				{
					// Send Code: request list of clients
					if (data.equals("r"))
					{
						threadAssist.setState(ThreadAssist.State.SERVER);
						serverOutput.writeBytes("200\n");
					}

					// Connect to slected client
					else
					{
						try
						{
							int numSelect = Integer.parseInt(data);
							// Selection must be in range of listed clients
							if (numSelect <= socketAddrList.size() && numSelect > 0)
							{
								System.out.println("Connecting to peer #" + data);
								threadAssist.setPeerSocket(makeSocket(socketAddrList.get(numSelect - 1)));
								threadAssist.setState(ThreadAssist.State.PEER);
								threadAssist.setPeer(true);
								System.out.println("Connected");

								// Start peer reader thread (listens and prints peer's text)
								PeerReader peerReader = new PeerReader(threadAssist.getPeerSocket());
								Thread peerReaderThread = new Thread(peerReader);
								peerReaderThread.start();

								//set bool so last entered value isn't sent
								skipFirstSend = true;
							}
							else
							{
								System.out.println("Invalid Selection.  Please try again.");
							}

						} 
						catch (NumberFormatException e) 
						{
							System.out.println("Invalid Selection.  Please try again.");
						}
						
					}
				}

				// Send text to Peer
				if (threadAssist.getState() == ThreadAssist.State.PEER)
				{
					// Dissconnect from server
					serverOutput.writeBytes("300\n");

					DataOutputStream peerOutput = new DataOutputStream(threadAssist.getPeerSocket().getOutputStream());
					if (!skipFirstSend) peerOutput.writeBytes(data + "\n");
					
					while (true)
					{
						data = keyboard.nextLine();
						peerOutput.writeBytes(data + "\n");
					}
				}
			}
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
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



	public static void main(String[] args)
	{
		MTClient client = new MTClient();
		client.runClient();
	}
} // MTClient


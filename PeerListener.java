import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class PeerListener implements Runnable
{
	private ThreadAssist threadAssist;

	int portNum;
	PeerListener(int port, ThreadAssist threadAssist)
	{
		portNum = port;
		this.threadAssist = threadAssist;
	}

	public void run()
	{
		getConnection();
	}

	private void getConnection()
	{
		// Wait for a connection from the client
		try
		{
			System.out.println("Waiting for peer connections on port " + portNum);
			ServerSocket serverSock = new ServerSocket(portNum);

			// Wait for client to connect
			threadAssist.setPeerSocket(serverSock.accept());

			// Connected
			threadAssist.setPeer(true);
			System.out.println(threadAssist.getPeerSocket().getRemoteSocketAddress() + " has connected.");
			BufferedReader peerInput = new BufferedReader(new InputStreamReader(threadAssist.getPeerSocket().getInputStream()));
			threadAssist.setState(ThreadAssist.State.PEER);

			// Start peer reader thread (listens and prints peer's text)
			PeerReader peerReader = new PeerReader(threadAssist.getPeerSocket());
			Thread peerReaderThread = new Thread(peerReader);
			peerReaderThread.start();
			
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
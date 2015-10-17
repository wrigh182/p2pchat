import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class PeerReader implements Runnable
{
	private Socket connectionSock = null;

	PeerReader(Socket sock)
	{
		connectionSock = sock;
	}

	public void run()
	{
		try
		{
			BufferedReader peerInput = new BufferedReader(new InputStreamReader(connectionSock.getInputStream()));
			while (true)
			{
				String peerText = peerInput.readLine();
				System.out.println("Peer: " + peerText);
			}
			//serverSock.close();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
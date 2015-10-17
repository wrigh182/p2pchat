import java.net.Socket;

public class ThreadAssist
{
	ThreadAssist(){}

	/////////////////////////////////////////////////////
	// ENUM
	public enum State 
	{
		SERVER, SELECT, PEER
	}

	State state;

	public void setState(State theState)
	{
		state = theState;
	}

	public State getState()
	{
		return state;
	}


	/////////////////////////////////////////////////////
	// PEER SOCKET
	Socket peerSock;

	public void setPeerSocket(String hostname, int port)
	{
		try
		{
			peerSock = new Socket(hostname, port);
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
	}

	public void setPeerSocket(Socket sock)
	{
		try
		{
			peerSock = sock;
		}
		catch (Exception e)
		{
			System.out.println("Error: " + e.toString());
		}
	}

	public Socket getPeerSocket()
	{
		return peerSock;
	}

	/////////////////////////////////////////////////////
	// PEER BOOL
	boolean peer = false;

	public void setPeer(boolean set)
	{
		peer = set;
	}

	public boolean isPeer()
	{
		if (peer)
			return true;
		else
			return false;
	}





}
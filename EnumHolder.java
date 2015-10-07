public class EnumHolder
{
	public enum State {
			SERVER, SELECT, PEER
		}
	State state;
	EnumHolder()
	{
		state = State.SERVER;
	}

	public void setState(State theState)
	{
		state = theState;
	}

	public State getState()
	{
		return state;
	}
}
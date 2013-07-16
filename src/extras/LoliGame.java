package extras;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import main.*;

@SuppressWarnings("rawtypes") 
public class LoliGame
{
	private MessageEvent event;
	private ResultSet result;
	
	public LoliGame(MessageEvent event)
	{
		super();
		this.event = event;
	}
}

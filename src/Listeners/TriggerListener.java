package Listeners;

import main.Globals;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

import extras.Triggers;

@SuppressWarnings("rawtypes")
public class TriggerListener extends ListenerAdapter
{
	@Override
	public void onAction(ActionEvent event)
	{
		if (event.getMessage().contains(event.getBot().getNick()))
			new Triggers(event).parseMsg(event.getMessage(), event.getChannel(), event.getUser().getNick());
	}
	
	@Override
	public void onMessage(MessageEvent event)
	{
		String[] parts = event.getMessage().split(" ");
		if (parts[0].toLowerCase().equals("!teach")) 
			new Triggers(event).teach(event.getUser().getNick(), Globals.putTogether(parts, 1, parts.length -1));
		else if (event.getMessage().contains(event.getBot().getNick())) 
			new Triggers(event).parseMsg(event.getMessage(), event.getChannel(), event.getUser().getNick());
	}
	
	@Override
	public void onPrivateMessage(PrivateMessageEvent event)
	{
		String[] parts = event.getMessage().split(" ");
		if (parts[0].toLowerCase().equals("!teach")) 
			new Triggers(event).teach(event.getUser().getNick(), Globals.putTogether(parts, 1, parts.length -1));
	}
}
package Listeners;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

import extras.Help;

@SuppressWarnings("rawtypes")
public class HelpListener extends ListenerAdapter
{
	@Override
	public void onPrivateMessage(PrivateMessageEvent event)
	{
		String[] parts = event.getMessage().split(" ");
		if (parts[0].toLowerCase().equals("help") && (parts.length == 1)) 
			new Help(event, "general").response();
		if (parts[0].toLowerCase().equals("help") && (parts.length == 2)) 
			new Help(event, parts[1].toLowerCase()).response();
	}
}

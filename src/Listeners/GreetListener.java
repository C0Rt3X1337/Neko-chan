package Listeners;

import main.Globals;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

import extras.Greet;

@SuppressWarnings("rawtypes")
public class GreetListener extends ListenerAdapter
{
	@Override
	public void onJoin(JoinEvent event)
	{
		if (!event.getUser().getNick().equals(event.getBot().getNick())) new Greet(event).greet();
	}
	
	@Override
	public void onPrivateMessage(PrivateMessageEvent event)
	{
		String[] parts = event.getMessage().split(" ");
		
		if (parts[0].toLowerCase().equals("greet"))
		{
			if (parts[1].toLowerCase().equals("request")) new Greet(event).request(parts[2], Globals.putTogether(parts, 3, parts.length - 1));
			if (parts[1].toLowerCase().equals("check")) new Greet(event).check();
			if (parts[1].toLowerCase().equals("on")) new Greet(event).enable();
			if (parts[1].toLowerCase().equals("off")) new Greet(event).disable();
		}
	}
}
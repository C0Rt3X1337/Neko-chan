package Listeners;

import main.Globals;

import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

import extras.FishGame;

@SuppressWarnings("rawtypes")
public class FishListener extends ListenerAdapter
{
	@Override
	public void onMessage(MessageEvent event)
	{
		String[] parts = Colors.removeFormattingAndColors(event.getMessage()).split(" ");
		
		if (parts[0].equalsIgnoreCase("!fish") && (parts.length == 1)) 
			new FishGame(event).fish();
		if (parts[0].equalsIgnoreCase("!fish") && (parts.length == 2)) 
			new FishGame(event).fishstats(parts[1], Globals.VALUE);
		if (parts[0].equalsIgnoreCase("!fishtop") && (parts.length == 1)) 
			new FishGame(event).fishstats(event.getUser().getNick(), Globals.TOP);
		if (parts[0].equalsIgnoreCase("!fishrivalz") && (parts.length == 1)) 
			new FishGame(event).fishstats(event.getUser().getNick(), Globals.RIVALZ);
		if (parts[0].equalsIgnoreCase("!steal") && (parts.length == 2))  
			new FishGame(event).steal(parts[1]);
		
		//combo trigger with purefmwc (Yuuki-chan)
		if (parts[0].equalsIgnoreCase("!fishloli") && (parts.length == 1)) 
			new FishGame(event).fish();
		if (parts[0].equalsIgnoreCase("!fishloli") && (parts.length == 2)) 
			new FishGame(event).fishstats(parts[1], Globals.VALUE);
	}
	
	/*
	@Override
	public void onPrivateMessage(PrivateMessageEvent event)
	{
		String[] parts = event.getMessage().split(" ");
		
		if (parts[0].equalsIgnoreCase("!fish") && (parts.length == 1)) 
			new FishGame(event).fish();
		if (parts[0].equalsIgnoreCase("!fish") && (parts.length == 2)) 
			new FishGame(event).fishstats(parts[1], Globals.VALUE);
		if (parts[0].equalsIgnoreCase("!fishtop") && (parts.length == 1)) 
			new FishGame(event).fishstats(event.getUser().getNick(), Globals.TOP);
		if (parts[0].equalsIgnoreCase("!fishrivalz") && (parts.length == 1)) 
			new FishGame(event).fishstats(event.getUser().getNick(), Globals.RIVALZ);
		if (parts[0].equalsIgnoreCase("!steal") && (parts.length == 2))  
			new FishGame(event).steal2(parts[1]);
	}
	*/
}
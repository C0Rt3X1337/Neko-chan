package Listeners;

import main.Globals;

import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

@SuppressWarnings("rawtypes")
public class PMAdminListener extends ListenerAdapter
{
	private byte userLevel = 0;
	@Override
	public void onPrivateMessage(PrivateMessageEvent event)
	{
		userLevel = Globals.getUserLevel(event.getUser().getNick());
		if (userLevel >= Globals.CAN_SAY_DO)
		{
			if (Globals.isIdentified(event, event.getUser().getNick()))
			{
				String[] parts = event.getMessage().split(" ");
				//to pm somebody
				if (parts[0].toLowerCase().equals("sayto") && (userLevel >= Globals.CAN_SAY_DO)) 
					event.getBot().sendMessage(parts[1], Globals.putTogether(parts, 2, parts.length -1));
				
				//to send an /me action to smby
				if (parts[0].toLowerCase().equals("doat") && (userLevel >= Globals.CAN_SAY_DO))
					event.getBot().sendAction(parts[1], Globals.putTogether(parts, 2, parts.length -1));
				
				//to send raw irc data
				if (parts[0].toLowerCase().equals("sendraw")  && (userLevel >= Globals.FULL_ADMIN)) 
					event.getBot().sendRawLine(Globals.putTogether(parts, 1, parts.length -1));
				
				//to enable or disable the verbose mode
				if (parts[0].equals("verbose") && parts[1].equals("on") && event.getUser().getNick().equals(Globals.BOTMASTER)) 
					Globals.verbose= true; 
				if (parts[0].equals("verbose") && parts[1].equals("off") && event.getUser().getNick().equals(Globals.BOTMASTER)) 
					Globals.verbose = false;
				
				//to quit the bot
				if (parts[0].equals("quit") && event.getUser().getNick().equals(Globals.BOTMASTER)) 
				{
					event.getBot().disconnect();
					try
					{
						Thread.sleep(5000);
					} catch (InterruptedException e) { }
					System.exit(0);
				}
			} else event.getBot().sendNotice(event.getUser(), "Please identify with NickServ if you want to use the admin commands.");
		}
	}
}

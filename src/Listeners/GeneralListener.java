package Listeners;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import main.DB;
import main.Globals;

import org.pircbotx.Colors;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.*;

@SuppressWarnings("rawtypes")
public class GeneralListener extends ListenerAdapter/*<PircBotX>*/
{
	private Date date;
	private SimpleDateFormat timeformat = new SimpleDateFormat(" [HH:mm:ss] ");

	@Override
	public void onConnect(ConnectEvent event)
	{
		event.getBot().identify(Globals.nickservPW);
	}
	
	@Override
	public void onJoin(JoinEvent event)
	{
		if (event.getUser().getNick().equals(event.getBot().getNick())) 
		try
		{
			DB.exec("INSERT INTO Channels VALUES ('" + event.getChannel().getName() + "');");
		} catch (SQLException e) { }
	}
	
	@Override
	public void onPart(PartEvent event)
	{
		if (event.getUser().getNick().equals(event.getBot().getNick()))
			try
			{
				DB.exec("DELETE FROM Channels WHERE Channel = '" + event.getChannel().getName() + "';");
			} catch (SQLException e) { }
	}
	
	@Override
	public void onKick(KickEvent event)
	{
		if (event.getRecipient().getNick().equals(event.getBot().getNick()))
		{
			try
			{
				DB.exec("DELETE FROM Channels WHERE Channel = '" + event.getChannel().getName() + "';");
			} catch (SQLException e) { }
			event.getBot().joinChannel(event.getChannel().getName());
		}
	}
	
	@Override
	public void onAction(ActionEvent event)
	{
		if (event.getAction().contains(event.getBot().getNick()))
		{
			date = new Date();
			if (Globals.verbose) event.getBot().sendMessage(Globals.BOTMASTER, Colors.PURPLE + Colors.BOLD + "Action" + Colors.NORMAL + timeformat.format(date) + "<" + event.getUser().getNick() + ":" + event.getChannel().getName() + "> " + event.getAction());
		}
	}
	
	/*
	@Override
	public void onServerResponse(ServerResponseEvent event)
	{
		date = new Date();
     	event.getBot().sendMessage(Globals.BOTMASTER, Colors.RED + Colors.BOLD + "Server" + Colors.NORMAL + timeformat.format(date) + "[" + event.getCode() + "] " + event.getResponse());
	}
	*/
	
	@Override
	public void onMessage(MessageEvent event)
	{
		if (event.getMessage().contains(event.getBot().getName()))
		{
			date = new Date();
			if (Globals.verbose) event.getBot().sendMessage(Globals.BOTMASTER, Colors.CYAN + Colors.BOLD + "HL" + Colors.NORMAL + timeformat.format(date) + "<" + event.getUser().getNick() + ":" + event.getChannel().getName() + "> " + event.getMessage());
		}
	}
	
	@Override
	public void onPrivateMessage(PrivateMessageEvent event)
	{
		date = new Date();
     	if (Globals.verbose) event.getBot().sendMessage(Globals.BOTMASTER, Colors.DARK_BLUE + Colors.BOLD + "PM" + Colors.NORMAL + timeformat.format(date) + "<" + event.getUser().getNick() + "> " + event.getMessage());

	}
	
	@Override
	public void onNotice(NoticeEvent event)
	{
        date = new Date();
        if (Globals.verbose && !event.getMessage().contains("STATUS")) event.getBot().sendMessage(Globals.BOTMASTER, Colors.DARK_GREEN + Colors.BOLD + "Notice" + Colors.NORMAL + timeformat.format(date) + "<"  + event.getUser().getNick() + "> " + event.getNotice());
	}
}

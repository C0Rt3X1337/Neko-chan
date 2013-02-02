package extras;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.pircbotx.Colors;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.*;

import main.*;

@SuppressWarnings("rawtypes")
public class Greet
{
	private String sender, status;
	private boolean userFound = false;
	private ResultSet result;
	private JoinEvent joinEvent = null;
	private PrivateMessageEvent pmEvent = null;
	
	public Greet(Event event)
	{
		super();
		if (event instanceof JoinEvent) 
		{
			joinEvent = (JoinEvent) event;
			sender = joinEvent.getUser().getNick();
		}
		if (event instanceof PrivateMessageEvent) 
		{
			pmEvent = (PrivateMessageEvent) event;
			sender = pmEvent.getUser().getNick();
		}
		try
		{
			result = DB.get("SELECT * FROM Greet WHERE User = '" + sender + "';");
			userFound = result.last();
			if (userFound) status = result.getString("Status");
		} catch (SQLException e) { }
	}

	public void greet()
	{
		String type;
		if (userFound && status.equals("ENABLED"))
		{
			try
			{
				type = result.getString("Type");
				if (type.equals("SAY")) joinEvent.getBot().sendMessage(joinEvent.getChannel(), result.getString("Message"));
				if (type.equals("ACT")) joinEvent.getBot().sendAction(joinEvent.getChannel(), result.getString("Message"));
			} catch (SQLException e) { }
		}
	}
	
	public void request(String type, String greet)
	{
		boolean invalidType = !type.equalsIgnoreCase("ACT") && !type.equalsIgnoreCase("SAY");
		boolean tooLong = greet.length() > 300;
		
		if (invalidType) pmEvent.getBot().sendMessage(sender, "Invalid Message type. Only " + Colors.BOLD + "SAY" + Colors.NORMAL + " and " + Colors.BOLD + "ACT" + Colors.NORMAL + " are allowed.");
		if (tooLong) pmEvent.getBot().sendMessage(sender, "Your requested greeting is too long. Maximum length is 300 characters.");
		if (!invalidType && !tooLong)
		{
			try
			{
				if (!userFound) DB.exec("INSERT INTO Greet VALUES ('" + sender + "', 'PENDING' , '" + type.toUpperCase() + "', '" + greet.replace("'", "\\'") + "');");
				else DB.exec("UPDATE Greet SET Status = 'PENDING', Type = '" + type.toUpperCase() + "', Message = '" + greet.replace("'", "\\'") + "' WHERE User = '" + sender + "';");
				pmEvent.getBot().sendMessage(sender, "You have successfully requested your greeting. Please wait patiently for " + Globals.BOTMASTER + " to approve it. It's done when it's done.");
			} catch (SQLException e) { }
		}
	}
	
	public void check()
	{
		if (userFound) pmEvent.getBot().sendMessage(sender, "Status of your greeting: " + status);
		else pmEvent.getBot().sendMessage(sender, "No greeting could be found for " + sender + ".");
	}
	
	public void enable()
	{
		if (userFound && !status.equals("PENDING") && !status.equals("DECLINED") && status.equals("DISABLED"))
		{
			try
			{
				DB.exec("UPDATE Greet SET Status = 'ENABLED' WHERE User = '" + sender + "';");
			} catch (SQLException e) { }
			pmEvent.getBot().sendMessage(sender, "Your greeting is now enabled and will be displayed in every channel I'm in whenever you join.");
		}
	}
	
	public void disable()
	{
		if (userFound  && !status.equals("PENDING") && !status.equals("DECLINED") && status.equals("ENABLED"))
		{
			try
			{
				DB.exec("UPDATE Greet SET Status = 'DISABLED' WHERE User = '" + sender + "';");
			} catch (SQLException e) { }
			pmEvent.getBot().sendMessage(sender, "Your greeting is now disabled and wont be displayed until you enable it again.");
		}
	}
}

package extras;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.*;

import main.DB;

@SuppressWarnings("rawtypes")
public class Triggers
{
	private String modMsg;
	private String nick;
	private Event event;
	
	private ResultSet result;
	
	public Triggers(Event event)
	{
		super();
		this.event = event;
	}

	public void parseMsg(String message, Channel channel, String sender)
	{
		//to detect if the bot is addressed
		if (message.contains(event.getBot().getNick()))
		{
			//retrieve actions from DB and exec them
			//replace botname w/ variable %this
			modMsg = message.replace(event.getBot().getNick(), "%this");
			modMsg = modMsg.replace("'", "\\'");
			//replace nick with %nick if its a nick in channel

			Iterator<User> iterator = channel.getUsers().iterator();
			boolean nickFound = false;
			String temp1;
			while (iterator.hasNext() && !nickFound)
			{
				temp1 = iterator.next().getNick();
				if (modMsg.contains(temp1))
				{
					nickFound = true;
					this.nick = temp1;
					modMsg = modMsg.replace(this.nick, "%nick");
				}
				
			}
			try
			{
				if (event instanceof ActionEvent) result = DB.get("SELECT * FROM Triggers WHERE UserType='ACT' AND UserAction='" + modMsg.trim() + "';");
				if (event instanceof MessageEvent) result = DB.get("SELECT * FROM Triggers WHERE UserType='SAY' AND UserAction='" + modMsg.trim() + "';");
				
				if (result.last())
				{
					result.absolute( (int) (Math.round( Math.random() * (result.getRow() - 1 ) ) + 1) );
					
					modMsg = result.getString(5);
					if(modMsg.contains("%nick")) modMsg = modMsg.replace("%nick", nick);
					if(modMsg.contains("%sender")) modMsg = modMsg.replace("%sender", sender);
					if (result.getString(4).equals("SAY")) event.getBot().sendMessage(channel, modMsg);
					if (result.getString(4).equals("ACT")) event.getBot().sendAction(channel, modMsg);
				}
			} catch (SQLException e) { }
		}		
	}
	
	//to teach Neko-chan actions and storing them in DB table
	//separator is '//' for simplicity
	public void teach(String user, String message)
	{
		//split message at separator and replace ' with \' for SQL not to cause any errors
		String[] parts = message.trim().replace("'", "\\'").split("//");
		String query = "INSERT INTO Triggers VALUES ('" + user + "', '" + parts[0].trim() + "', '" + parts[1].trim() + "', '" + parts[2].trim() + "', '" + parts[3].trim() + "');";
		try
		{
			DB.exec(query);
			DB.exec("ALTER TABLE Triggers ORDER BY UserAction");
			event.getBot().sendNotice(user, "Trigger sucessfully added! Feel free to try it out ^^");
		} catch (SQLException e) { event.getBot().sendNotice(user, "Error while adding Trigger to DB! Try again and/or do !teachhelp"); }
	}
}

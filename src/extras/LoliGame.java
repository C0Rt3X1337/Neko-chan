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
	private long now = System.currentTimeMillis();
	private long last, next, diff = Globals.HOUR;

	public LoliGame(MessageEvent event)
	{
		super();
		this.event = event;
	}

	public void neko()
	{
		int recieved = (int) Math.round(Math.random() * 10);
		int current = recieved;
		boolean noWait = true;
		boolean gotSpecial = false;

		try
		{
			result = DB.get("SELECT * FROM LoliGame WHERE User='" + event.getUser().getNick() + "';");
			if (result.last()) //entry found
			{
				last = result.getLong("LastTime");
				current = result.getInt("number");
				diff = now - last;
				if (diff >= 6 * Globals.HOUR ) //can play now
				{
					current = current + recieved;
					DB.exec("UPDATE Loli SET number=" + current + ", LastTime=" + now + " WHERE User='" + event.getUser().getNick() + "';");
					DB.exec("UPDATE Loli SET number= (number - " + recieved + ") WHERE User='BANK';");
				} else noWait = false;
					
			} else //add new User entry
			{
				DB.exec("INSERT INTO Fish VALUES ('" + event.getUser().getNick() + "', " + now + ", " + recieved + ", " + 0 +");");
				DB.exec("UPDATE Loli SET number= (number - " + recieved + ") WHERE User='BANK';");
				DB.exec("ALTER TABLE Fish ORDER BY User ASC");
			}
		} catch (Exception e) { }
		//report to User what was done
		if (noWait)
		{	
			if (recieved == 0) event.getBot().sendAction(event.getChannel(), "gives " + Colors.RED + "no" + Colors.NORMAL + " Neko to " + event.getUser().getNick() + ". " + event.getUser().getNick() + " now has " + current + " fishes!");
			if (recieved == 1) event.getBot().sendAction(event.getChannel(), "gives " + Colors.DARK_GREEN + "a Neko" + Colors.NORMAL + " to " + event.getUser().getNick() + ". " + event.getUser().getNick() + " now has " + current + " fishes!");
			if (recieved > 1) event.getBot().sendAction(event.getChannel(), "gives " + Colors.DARK_GREEN + recieved + Colors.NORMAL + " Nekos to " + event.getUser().getNick() + ". " + event.getUser().getNick() + " now has " + current + " fishes!");
		} else 
		{
			next = Globals.HOUR - diff;
			int nextMins = (int) (next / (60 * 1000));
			int nextSecs = (int) ((next / 1000) - (nextMins * 60));
			event.getBot().sendNotice(event.getUser().getNick(), "Sorry " + event.getUser().getNick() + ". Try again in " + nextMins + " minutes and " + nextSecs + " seconds");
		}	
	}

	public void assignSpecialLoli(String user, int number)
	{

	}

	//for !fish nick and !fishtop
	public void nekostats(String nick, byte type)
	{
		int rank = 0, total = 0, temp = 0;

		try
		{
			result = DB.get("SELECT User, number FROM Fish ORDER BY number DESC;");
		} catch (SQLException e) { }

		switch (type)
		{
			//output value and rank of a given player
			case Globals.VALUE:
			{
				try
				{
					while (result.next())
					{
						if(!(temp == result.getInt("number"))) 
						{	
							total++;
							temp = result.getInt("number"); 
						}
						if (event.getUser().getNick().equals(result.getString("User"))) rank = total;
					}

					if (rank != 0)
					{
						result = DB.get("SELECT number FROM Fish WHERE User='" + event.getUser().getNick() + "';");
						result.last();
						if (event.getUser().getNick().equals(nick)) event.getBot().sendMessage(event.getChannel(), "" + event.getUser().getNick() + " you have " + result.getInt("number") + " nekos. Which makes you rank " + rank + " out of " + total);
						else event.getBot().sendMessage(event.getChannel(), event.getUser().getNick() + " has " + result.getInt("number") + " nekos. Which makes " + event.getUser().getNick() + " rank " + rank + " out of " + total);

					} else 
					{ 
						if (event.getUser().getNick().equals(nick)) event.getBot().sendMessage(event.getChannel(), "You haven't played my game yet. Do !fish to start now " + event.getUser().getNick());
						else event.getBot().sendMessage(event.getChannel(), "" + event.getUser().getNick() + " hasn't played my game yet");
					}
				} catch (SQLException e) { }
			} break;

			case Globals.TOP:
			{
				String top5 = "Top 5 !neko players:";
				try
				{
					result.beforeFirst();
					for (int i = 1; i <= 5; i++)
					{
						result.next();
						top5 = top5.concat(" " + i + ". " + result.getString("User") + ": " + result.getInt(2) + " nekos");
					}
				} catch (SQLException e) { }
				event.getBot().sendMessage(event.getChannel(), top5);
			} break;
		}
	}
}

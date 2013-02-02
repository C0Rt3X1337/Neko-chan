package extras;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.pircbotx.Colors;
import org.pircbotx.hooks.events.MessageEvent;

import main.*;

@SuppressWarnings("rawtypes")
public class FishGame
{
	private long now = System.currentTimeMillis();
	private long last, next, diff = Globals.HOUR;
	private LinkedList<Player> players;
	private MessageEvent event;
	private ResultSet result;
	private Rates rates;
	
	private class Player
	{
		private String nick;
		private int number;
		
		public Player(String nick, int number)
		{
			this.nick = nick;
			this.number = number;
		}
		
		public String getNick() { return nick; }
		public int getNumber() { return number; }
	}
	
	private class Rates
	{
		public double eatChance, stealSuccessChance, punishChance, stealAmmount, punishmentAmmount;
		
		//not used yet. only default rates apply. preparation for a future update
		public Rates(String nick)
		{
			try
			{
				ResultSet result = DB.get("SELECT * FROM FishRates WHERE nick = '" + nick + "';");
				if (!result.last()) 
				{
					result = DB.get("SELECT * FROM FishRates WHERE nick = 'DEFAULT RATES';");
					result.last();
				}
				this.eatChance = result.getDouble("eatChance");
				this.stealSuccessChance = result.getDouble("stealSuccessChance");
				this.punishChance = result.getDouble("punishChance");
				this.stealAmmount = result.getDouble("stealAmmount");
				this.punishmentAmmount = result.getDouble("punishmentAmmount");
			} catch (SQLException e) { }
		}
	}
	
	public FishGame(MessageEvent event)
	{
		super();
		this.event = event;
		rates = new Rates(this.event.getUser().getNick());
	}
	
	//for !fish trigger
	public void fish()
	{
		int recieved = (int) Math.round(Math.random() * 10);
		int current = recieved;
		boolean noWait = true;
		
		if (Math.random() <= rates.eatChance) recieved = -recieved;
		try
		{
			result = DB.get("SELECT * FROM Fish WHERE User='" + event.getUser().getNick() + "';");
			if (result.last()) //entry found
			{
				last = result.getLong("lastTime");
				current = result.getInt("number");
				diff = now - last;
				if (diff >= Globals.HOUR) //can play now
				{
					event.getBot().sendMessage(event.getChannel(), "Okaeri " + event.getUser().getNick() + " !");
					if (current < 30) recieved = Math.abs(recieved); //cant lose if less than 30 fish. so ammount cant go below 0
					current = current + recieved;
					DB.exec("UPDATE Fish SET number=" + current + ", lastTime=" + now + ", CanSteal=1 WHERE User='" + event.getUser().getNick() + "';");
				} else noWait = false;
			} else //add new player entry
			{
				recieved = Math.abs(recieved);
				event.getBot().sendMessage(event.getChannel(), "Hajimemashite " + event.getUser().getNick() + " !");
				DB.exec("INSERT INTO Fish VALUES ('" + event.getUser().getNick() + "', " + now + ", " + recieved + ", 0, 1);");
				DB.exec("ALTER TABLE Fish ORDER BY User ASC");
			}
		} catch (Exception e) { }
		
		//report to player what was done
		if (noWait)
		{	
			if (recieved < -1) event.getBot().sendAction(event.getChannel(), "eats " + Colors.RED + Math.abs(recieved) + Colors.NORMAL + " of " + event.getUser().getNick() + "'s fishes. " + event.getUser().getNick() + " now has " + current + " fishes!");
			if (recieved == -1) event.getBot().sendAction(event.getChannel(), "eats " + Colors.RED +  "one" + Colors.NORMAL + " of " + event.getUser().getNick() + "'s fishes. " + event.getUser().getNick() + " now has " + current + " fishes!");
			if (recieved == 0) event.getBot().sendAction(event.getChannel(), "gives " +Colors.RED + "no" + Colors.NORMAL + " fish to " + event.getUser().getNick() + ". " + event.getUser().getNick() + " now has " + current + " fishes!");
			if (recieved == 1) event.getBot().sendAction(event.getChannel(), "gives " + Colors.DARK_GREEN + "a fish" + Colors.NORMAL + " to " + event.getUser().getNick() + ". " + event.getUser().getNick() + " now has " + current + " fishes!");
			if (recieved > 1) event.getBot().sendAction(event.getChannel(), "gives " + Colors.DARK_GREEN + recieved + Colors.NORMAL + " fishes to " + event.getUser().getNick() + ". " + event.getUser().getNick() + " now has " + current + " fishes!");
		} else
		{
			next = Globals.HOUR - diff;
			int nextMins = (int) (next / (60 * 1000));
			int nextSecs = (int) ((next / 1000) - (nextMins * 60));
			event.getBot().sendNotice(event.getUser(), "Sorry " + event.getUser().getNick() + ". Try again in " + nextMins + " minutes and " + nextSecs + " seconds");
		}
	}
	
	//for !fish nick !fishtop and fishrivalz
	public void fishstats(String nick, byte type)
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
						if (nick.equals(result.getString("User"))) rank = total;
					}
					if (rank != 0)
					{	
							result = DB.get("SELECT number FROM Fish WHERE User='" + nick + "';");
							result.last();
							if (event.getUser().getNick().equals(nick)) event.getBot().sendMessage(event.getChannel(), "" + nick + " you have " + result.getInt(1) + " fishes. Which makes you rank " + rank + " out of " + total);
							else event.getBot().sendMessage(event.getChannel(), nick + " has " + result.getInt("number") + " fishes. Which makes " + nick + " rank " + rank + " out of " + total);
						
					} else 
					{ 
						if (event.getUser().getNick().equals(nick)) event.getBot().sendMessage(event.getChannel(), "You haven't played my game yet. Do !fish to start now " + event.getUser().getNick());
						else event.getBot().sendMessage(event.getChannel(), "" + nick + " hasn't played my game yet");
					}
				} catch (SQLException e) { }
			} break;
			
			case Globals.TOP:
			{
				String top5 = "Top 5 !fish players:";
				try
				{
					for (int i = 1; i <= 5; i++)
					{
						result.next();
						top5 = top5.concat(" " + i + ". " + result.getString("User") + ": " + result.getInt("number") + " fishes");
					}
				} catch (SQLException e) { }
				event.getBot().sendNotice(event.getUser(), top5);
			} break;
			
			case Globals.RIVALZ:
			{
				String rivalz = "";
				try
				{
					result = DB.get("SELECT * FROM Fish WHERE User = '" + nick + "';");
					if (result.last())
					{
						players = this.getRivalz(nick);
						Iterator<Player> playerIterator = players.listIterator();
						while (playerIterator.hasNext())
						{
							Player player = playerIterator.next();
							if (player.getNick().equals(nick)) rivalz = rivalz.concat(Colors.BOLD + player.getNick() + ": " + player.getNumber() + " fish" + Colors.NORMAL + " | ");
							else rivalz = rivalz.concat(player.getNick() + ": " + player.getNumber() + " fish | ");
						}
						event.getBot().sendNotice(event.getUser(), rivalz.substring(0, rivalz.length() - 3));
					}
					else event.getBot().sendNotice(event.getUser(), "You havent played my game yet. Do " + Colors.BOLD + "!fish" + Colors.NORMAL + "to start now.");
				} catch (SQLException e) { }
			} break;
		}
	}
	
	//for !steal nick
	public void steal(String target)
	{
		int ammount = 0;
		
		try
		{	
			//check for self targeting
			if (!event.getUser().getNick().equals(target))
			{
				//check if sender is a player
				result = DB.get("SELECT * FROM Fish WHERE User = '" + event.getUser().getNick() + "';");
				if (result.last()) 
				{
					//check if the sender can steal now
					if (result.getBoolean("CanSteal"))
					{
						//check if target is a player
						result = DB.get("SELECT number FROM Fish WHERE User='" + target + "';");
						if (result.last()) 
						{
							//check if target is valid	
							boolean isValid = false;
							players = this.getRivalz(event.getUser().getNick());
							ListIterator<Player> playerIterator = players.listIterator();
							while (playerIterator.hasNext()) if (playerIterator.next().getNick().equals(target)) isValid = true;
							if (isValid)
							{
								//only one steal attempt per hour
								DB.exec("UPDATE Fish SET CanSteal=0 WHERE User='" + event.getUser().getNick() + "';");
								//initial steal notification
								event.getBot().sendMessage(target, "" + event.getUser().getNick() + " attempted stealing some of your fishes.");
								//the actual stealing
								if (Math.random() <= rates.stealSuccessChance)
								{
									result = DB.get("SELECT number FROM Fish WHERE User='" + target + "';");
									result.last();
									ammount = (int) Math.round(rates.stealAmmount * result.getInt("number"));
									DB.exec("UPDATE Fish SET number = (number - " + ammount + ") WHERE User = '" + target + "';");
									DB.exec("UPDATE Fish SET number = (number + " + ammount + ") WHERE User = '" + event.getUser().getNick() + "';");
									event.getBot().sendMessage(event.getChannel(), "" + event.getUser().getNick() + " has successfully stolen " + ammount + " fish from " + target + ".");
									event.getBot().sendMessage(target, "You lost " + ammount + " fishes to " + event.getUser().getNick());
								} else //chance to backfire at the sender
								{
									if (Math.random() <= rates.punishChance)
									{
										result = DB.get("SELECT number FROM Fish WHERE User='" + event.getUser().getNick() + "';");
										result.last();
										ammount = (int) Math.round(rates.punishmentAmmount * result.getInt("number"));
										DB.exec("UPDATE Fish SET number = (number - " + ammount + ") WHERE User = '" + event.getUser().getNick() + "';");
										DB.exec("UPDATE Fish SET number = (number + " + ammount + ") WHERE User = '" + target + "';");
										event.getBot().sendMessage(event.getChannel(), "" + event.getUser().getNick() + " got caught being a dirty thief and had to pay " + ammount + " fish to " + target + ".");
										event.getBot().sendMessage(target, "" + event.getUser().getNick() + " got caught being a dirty thief and had to pay " + ammount + " fish to you.");
									} else 
									{
										event.getBot().sendMessage(event.getChannel(), "" + event.getUser().getNick() + " failed stealing " + target + "'s fish.");
										event.getBot().sendMessage(target, "" + event.getUser().getNick() + " failed.");
									}
								}
							} else event.getBot().sendNotice(event.getUser(), "" + target + " is not a valid target for you. Do " + Colors.BOLD + "!fishrivalz" + Colors.NORMAL + " to get your valid targets.");
						} else event.getBot().sendMessage(event.getChannel(), "" + target + " hasn't played my game yet ");
					} else event.getBot().sendNotice(event.getUser().getNick(), "You have recently attempted stealing from someone. Try again later after your next !fish");
				} else event.getBot().sendNotice(event.getUser().getNick(), "You haven't played my game yet " + event.getUser().getNick() + ". Do !fish to start now.");
			} else event.getBot().sendNotice(event.getUser().getNick(), "You can't steal your own fish dumbass!");
		} catch (SQLException e) { }
	}
	
	 /* currently not used
	//turn a time diff in ms into a string that humans can read
	private String getNext(int hours)
	{
		int nextHours = 0;
		
		next = (hours * Globals.HOUR) - diff;
		if (hours >= 2) nextHours  = (int) (next / (60 * 60 * 1000));
		int nextMins = (int) ((next / (60 * 1000)) - (nextHours * 60));
		int nextSecs = (int) ((next / 1000) -  (nextHours * 60 * 60) - (nextMins * 60));
		return "" + nextHours + " hours and " + nextMins + " minutes and " + nextSecs + " seconds";
	}
	*/
	
	//return a LinkedList containing the rivalz of a given nick
	//format: <5 players above nick> <nick> <5 players below nick>
	private LinkedList<Player> getRivalz(String nick) throws SQLException
	{
		LinkedList<Player> players = new LinkedList<Player>();
		ResultSet result = DB.get("SELECT number FROM Fish WHERE User = '" + nick + "';");
		result.last();
		int number = result.getInt("number");
		result = DB.get("SELECT * FROM Fish WHERE number >= " + number + " AND User != '" + nick + "' ORDER BY number ASC LIMIT 0, 5");
		result.afterLast();
		while (result.previous()) players.add(new Player(result.getString("User"), result.getInt("number")));
		players.add(new Player(nick, number));
		result = DB.get("SELECT * FROM Fish WHERE number <= " + number + " AND User != '" + nick + "' ORDER BY number DESC LIMIT 0, 5");
		while (result.next()) players.add(new Player(result.getString("User"), result.getInt("number")));

		return players;
	}
}

package extras;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.pircbotx.Colors;
import org.pircbotx.hooks.events.PrivateMessageEvent;

import main.DB;
import main.Globals;

@SuppressWarnings("rawtypes")
public class Help
{
	private String topic;
	private byte userLevel;
	private boolean entryFound = false;
	private ResultSet result;
	private PrivateMessageEvent event;
	
	public Help(PrivateMessageEvent event, String topic)
	{
		super();
		this.topic = topic;
		this.event = event;
		this.userLevel = Globals.getUserLevel(event.getUser().getNick());
	}
	
	public void response()
	{
		try
		{
			result = DB.get("SELECT Line FROM Help WHERE Tag = '" + topic + "' AND userLevel <= '" + userLevel + "';");
			while (result.next())
			{
				entryFound = true;
				//get current line from the result set
				String line = result.getString("Line");
				//apply bold formatting
				line  = line.replace("[BOLD]", Colors.BOLD).replace("[/BOLD]", Colors.NORMAL);
				//replace variables with their actual content
				line = line.replace("%master", Globals.BOTMASTER).replace("%bot", event.getBot().getNick()).replace("%sender2", event.getUser().getNick());
				//output it
				event.getBot().sendMessage(event.getUser(), line);
			}
			if (!entryFound) event.getBot().sendMessage(event.getUser(), "No help available for " + Colors.BOLD + topic +Colors.NORMAL + ". Try " + Colors.BOLD + "/msg " + event.getBot().getNick() + " help" + Colors.NORMAL + " to see all available help topics.");
		} catch (SQLException e) { }
	}
}
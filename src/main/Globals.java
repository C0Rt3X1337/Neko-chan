package main;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.WaitForQueue;
import org.pircbotx.hooks.events.NoticeEvent;

public class Globals
{
	public static final String BOTMASTER = "C0Rt3X";
	
	public static String nickservPW = "";
	
	public final int basePrice = 500; //for the shop (everything related to that is still beta)
	
	public static final long HOUR = 3600000;
	
	public static final byte FULL_ADMIN = 10;
	public static final byte CAN_SAY_DO = 5;
	
	public static final byte VALUE = 100;
	public static final byte TOP = 101;
	public static final byte RIVALZ = 102;
	
	public static final byte SAY = 110;
	public static final byte ACT = 111;

	public static boolean starting = true;
	public static boolean verbose = false;
	
	public static String putTogether(String[] parts, int from, int to)
	{
		String temp = "";
		for (int i = from; i <= to; i++) temp = temp + parts[i] + " ";
		return temp.trim();
	}
	
	public static byte getUserLevel(String nick)
	{
		try
		{
			ResultSet result = DB.get("SELECT Permission FROM Permissions WHERE User='" + nick + "';");
			if (result.last()) return result.getByte("Permission"); else return 0;
		} catch (SQLException e) { return -1; }
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean isIdentified(Event event, String nick)
	{
		boolean isIdentified = false, isDone = false;
		String[] statusParts;
		event.getBot().sendMessage("NickServ", "STATUS " + nick);
		while (!isDone)
		{ 
			try
			{
				WaitForQueue queue = new WaitForQueue(event.getBot());
				NoticeEvent noticeEvent = queue.waitFor(NoticeEvent.class);
				if (noticeEvent.getUser().getNick().equalsIgnoreCase("NickServ") && noticeEvent.getMessage().contains("STATUS " + nick)) 
				{
					statusParts = noticeEvent.getMessage().split(" ");
					isDone = true;
					queue.close();
					if (statusParts[2].equals("2") || statusParts[2].equals("3")) isIdentified = true;
				}
			} catch (InterruptedException e) { }
		}
		return isIdentified;
	}
}

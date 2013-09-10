package de.javagimmicks.games.inkognito.message.message;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.javagimmicks.games.inkognito.model.Player;

public class ReportNamesMessage implements Message
{
	private final List<String> m_oPlayerNames;

	public static ReportNamesMessage fromPlayerList(final List<Player> oPlayers)
	{
		List<String> oNamesList = new ArrayList<String>(new AbstractList<String>()
		{
			public String get(int index)
			{
				return oPlayers.get(index).getName();
			}

			public int size()
			{
				return oPlayers.size();
			}
		});
		
		return new ReportNamesMessage(oNamesList);
	}
	
	public ReportNamesMessage(List<String> oPlayerNames)
	{
		m_oPlayerNames = oPlayerNames;
	}
	
	public String serialize()
	{
		StringBuffer oResult = new StringBuffer();
		
		oResult.append(SIG_REP_NAMES).append(' ');
		
		for(String sPlayerName : m_oPlayerNames)
		{
			oResult.append(sPlayerName).append(' ');
		}
		
		return oResult.toString();
	}
	
	public List<String> getPlayerNames()
	{
		return Collections.unmodifiableList(m_oPlayerNames);
	}
}

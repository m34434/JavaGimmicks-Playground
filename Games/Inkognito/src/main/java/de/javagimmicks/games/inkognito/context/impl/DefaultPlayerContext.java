package de.javagimmicks.games.inkognito.context.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.javagimmicks.games.inkognito.context.PlayerContext;
import de.javagimmicks.games.inkognito.model.Player;

public class DefaultPlayerContext implements PlayerContext
{
	private final Map<String, Player> m_oPlayers = new LinkedHashMap<String, Player>();
	private final LinkedList<Player> m_oPlayerRotationList = new LinkedList<Player>();
	
	public void reset()
	{
		m_oPlayerRotationList.clear();
		m_oPlayerRotationList.addAll(m_oPlayers.values());
	}
	
	public void init(List<Player> oPlayers)
	{
		for(Player oPlayer : oPlayers)
		{
			m_oPlayers.put(oPlayer.getName(), oPlayer);
		}
		
		reset();
	}
	
	public void rotatePlayers()
	{
		m_oPlayerRotationList.addLast(m_oPlayerRotationList.removeFirst());
	}
	
	public List<Player> getPlayersRotated()
	{
		return Collections.unmodifiableList(m_oPlayerRotationList);
	}
	
	public Player getPlayer(String sPlayerName)
	{
		return m_oPlayers.get(sPlayerName);
	}
	
	public Set<String> getPlayerNames()
	{
		return Collections.unmodifiableSet(m_oPlayers.keySet());
	}
	
	public Collection<Player> getInitialPlayers()
	{
		return Collections.unmodifiableCollection(m_oPlayers.values());
	}
	
	public int getPlayerCount()
	{
		return m_oPlayers.size();
	}
}

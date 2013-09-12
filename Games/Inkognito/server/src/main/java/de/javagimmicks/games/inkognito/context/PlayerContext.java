package de.javagimmicks.games.inkognito.context;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import de.javagimmicks.games.inkognito.model.Player;

public interface PlayerContext
{

	public void init(List<Player> oPlayers);

	public void rotatePlayers();

	public List<Player> getPlayersRotated();

	public Player getPlayer(String sPlayerName);

	public Set<String> getPlayerNames();

	public Collection<Player> getInitialPlayers();

	public int getPlayerCount();
	
	public void reset();
}
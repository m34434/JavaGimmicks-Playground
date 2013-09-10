package de.javagimmicks.games.inkognito.context;

import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.Player;

public interface CardShowingContext
{
	public boolean mayPlayerShowPair(Player oShowingPlayer,
			Player oAskingPlayer, CardPair oCardPair);

	public void notifiyPlayerShow(Player oShowingPlayer, Player oAskingPlayer,
			CardPair oCardPair);

	public boolean mayPlayerAskId(Player oAskingPlayer, Player oShowingPlayer);

	public boolean mayPlayerShowId(Player oShowingPlayer, Player oAskingPlayer,
			Card oCard);

	public void notifiyPlayerShow(Player oShowingPlayer, Player oAskingPlayer,
			Card oCard);

	public void reset();
}
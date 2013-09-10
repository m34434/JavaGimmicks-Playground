package de.javagimmicks.games.inkognito.context.ai;

import java.util.List;
import java.util.Set;

import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardPair;
import de.javagimmicks.games.inkognito.model.Player;

public interface CardAnalysingContext
{
	public boolean isSolutionFound();

	public boolean isPlayerIdKnown(Player oPlayer);

	public boolean isPlayerNameKnown(Player oPlayer);

	public boolean isPlayerTelephoneKnown(Player oPlayer);

	public Set<Card> getPossiblePlayerTelephones(Player oPlayer);

	public Set<Card> getPossiblePlayerNames(Player oPlayer);

	public Set<CardPair> getPossiblePlayerId(Player oPlayer);

	public void notifyCardPairSeen(Player oPlayer, CardPair oSeenPair);

	public void notifyCardSeen(Player oPlayer, Card oSeenCard);

	public void init(List<Player> oPlayers, List<Card> oNameCards,
			List<Card> oTelephoneCards);

	public List<List<CardPair>> getRemainingSolutions();

	public void reset();

}
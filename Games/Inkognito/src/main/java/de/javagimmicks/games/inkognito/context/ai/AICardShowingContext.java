package de.javagimmicks.games.inkognito.context.ai;

import de.javagimmicks.games.inkognito.context.CardShowingContext;

public interface AICardShowingContext extends CardShowingContext
{
	public CardAnalysingContext getCardAnalysingContext();
}

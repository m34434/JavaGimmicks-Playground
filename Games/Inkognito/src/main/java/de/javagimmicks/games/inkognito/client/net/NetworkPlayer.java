package de.javagimmicks.games.inkognito.client.net;

public interface NetworkPlayer
{
    /** Wird mit allen Nachrichten vom Moderator aufgerufen.
     * Bei Requests ist die Antwort fuer den Moderator als Ergebniswert
     * zurueckzuliefern.
     * Bei Messages wird null zurueckgegeben.
     * @param request String der vom Moderator geschickt worden ist.
     * @return Bei Requests Antwort an den Moderator oder null bei Messages.
     */
	public String process(String message);
}

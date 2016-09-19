package scorekeeper.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains player and score data to represent a score keeper game.
 */
public class ScoreKeeperGameData {
    private List<String> players;
    private Map<String, Long> scores;

    public List<String> getPlayers() {
        if(players == null)
        {
            players = new ArrayList<>();
        }
        
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public Map<String, Long> getScores() {
        if(scores == null)
        {
            scores = new HashMap<>();
        }
        
        return scores;
    }

    public void setScores(Map<String, Long> scores) {
        this.scores = scores;
    }

    @Override
    public String toString() {
        return "[ScoreKeeperGameData players: " + players + "] scores: " + scores + "]";
    }
}

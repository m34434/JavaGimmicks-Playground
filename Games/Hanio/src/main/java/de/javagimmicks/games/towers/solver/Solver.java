package de.javagimmicks.games.towers.solver;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.javagimmicks.games.towers.model.GameConfiguration;


public class Solver
{
    private GameConfiguration _configuration;
    
    public Solver(GameConfiguration configuration)
    {
        _configuration = configuration;
    }
    
    public List<Command> getSolution(String toStickName)
    {
        // Prepare result list
        List<Command> commands = new LinkedList<Command>();

        // Get stick of starting stick
        String fromStickName = _configuration.getInitialStickName();
        
        // Generate set of free sticks
        Set<String> sticks = new HashSet<String>(_configuration.getStickNames());
        sticks.remove(_configuration.getInitialStickName());
        sticks.remove(toStickName);
        
        findSolutions(commands, fromStickName, toStickName, sticks, _configuration.getDiscCount());
        
        return commands;
    }

    private static void findSolutions(List<Command> commands, String fromStickName, String toStickName, Set<String> freeSticks, int height)
    {
        // Check the trivial case (# of free stick greater or equal # of discs - 1)
        if(freeSticks.size() >= height - 1)
        {
            // Spread all but the last remaining disc over the free sticks
            // (and remember the stick, were they are put)
            int count = 0;
            LinkedList<String> tempSticks = new LinkedList<String>();
            for(String stickName : freeSticks)
            {
                commands.add(new Command(fromStickName, stickName));
                
                tempSticks.addFirst(stickName);
                
                if(++count == height - 1)
                {
                    break;
                }
            }
            
            // Move the last disc
            commands.add(new Command(fromStickName, toStickName));
            
            // Put the spread disc in the reverse order back on the new stick
            for(String stickName : tempSticks)
            {
                commands.add(new Command(stickName, toStickName));
            }
        }
        else
        {
            // Get the first one of the free sticks
            String freeStickName = freeSticks.iterator().next();

            // Exchange this stick with the TO stick in the list of free sticks
            freeSticks.remove(freeStickName);
            freeSticks.add(toStickName);
            
            // Make the recursive call for the one disc smaller tower
            findSolutions(commands, fromStickName, freeStickName, freeSticks, height - 1);
            
            // Move the lowest remaining disc directly to the target stick
            commands.add(new Command(fromStickName, toStickName));
            
            // Exchange this TO with the FROM stick in the list of free sticks
            freeSticks.remove(toStickName);
            freeSticks.add(fromStickName);
            
            // Put back the one disc smaller tower from the free stick to the new TO stick
            findSolutions(commands, freeStickName, toStickName, freeSticks, height - 1);

            // Restore the original free stick set by exchanging the FROM stick with the taken free stick
            freeSticks.remove(fromStickName);
            freeSticks.add(freeStickName);
        }
    }
}

package de.javagimmicks.games.towers.view.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import de.javagimmicks.games.towers.model.Disc;
import de.javagimmicks.games.towers.model.Game;
import de.javagimmicks.games.towers.model.GameConfiguration;
import de.javagimmicks.games.towers.view.layout.MultiRowPaneLayout;
import de.javagimmicks.games.towers.view.layout.TowerGamePaneLayout;


public class TowerGamePane extends JLayeredPane
{
    private static final long serialVersionUID = -1061648488856653119L;

    private final Game _game;
    private final GameConfiguration _configuration;
    private final ArrayList<DiscLabel> _discs;
    private final TowerGamePaneLayout _layout;
    
    public TowerGamePane(Game game)
    {
        // Remember game and game configuration
        _game = game;
        _configuration = game.getGameConfiguration();
        
        int discCount = _configuration.getDiscCount();
        int stickCount = _configuration.getStickCount();
        
        _discs = new ArrayList<DiscLabel>(discCount);
        _layout = new MultiRowPaneLayout(discCount, stickCount, 3);
        
        for(int i = 0; i < discCount; ++i)
        {
            DiscLabel discToAdd = createDisc(i + 1, discCount); 
            
            _discs.add(discToAdd);
        }

        setPreferredSize(_layout.getPaneSize());
        
        int stickCounter = 0;
        for(String stickName : _configuration.getStickNames())
        {
            JLabel baseDisc = createBaseDisc(stickName, discCount);
            setDiscLocation(baseDisc, stickCounter++, 0, discCount + 1);
            
            add(baseDisc);
        }
        
        // Create the disc labels and add them to the pane
        for(DiscLabel disc : _discs)
        {
            add(disc);
        }

        redraw();
    }
    
    public Game getGame()
    {
        return _game;
    }
    
    public TowerGamePaneLayout getPaneLayout()
    {
        return _layout;
    }

    public void redraw()
    {
    	SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
		        // Set the disc postitions
		        int stickNum = 0;
		        for(String stickName : _configuration.getStickNames())
		        {
		            Stack<Disc> stick = _game.getStick(stickName);

		            int height = 1;
		            for(Disc disc : stick)
		            {
		                int width = disc.getWidth();
		                setDiscLocation(stickNum, height, _discs.get(width - 1));

		                ++height;
		            }
		            
		            ++stickNum;
		        }
			}
    	});
    }
    
    private DiscLabel createDisc(int width, int discCount)
    {
        DiscLabel result = new DiscLabel(width);
        
        int iToneIndex = width - 1;
        int iRedColor = 245 - (int)(130.0 * iToneIndex / discCount);
        int iGreenColor = 245 - (int)(130.0 * iToneIndex / discCount);
        int iBlueColor = 245 - (int)(50.0 * iToneIndex / discCount);

        layoutAsDisc(result);
        result.setBackground(new Color(iRedColor, iGreenColor, iBlueColor));
        result.setSize(new Dimension(_layout.getDiscWidthBase() * width, _layout.getDiscHeight()));
        
        return result;
    }
    
    private JLabel createBaseDisc(String name, int discCount)
    {
        JLabel result = new JLabel(name);

        layoutAsDisc(result);
        result.setBackground(Color.BLACK);
        result.setForeground(Color.WHITE);
        
        result.setSize(new Dimension(_layout.getDiscWidthBase() * (discCount + 1), _layout.getDiscHeight()));
        
        return result;
    }
    
    private void layoutAsDisc(JLabel label)
    {
        label.setBorder(BorderFactory.createEtchedBorder());
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
    }
    
    private void setDiscLocation(JLabel discLabel, int section, int height, int widthIndex)
    {
        Point basePoint = _layout.getSectionPoint(section, height);
        int discIndent = getDiscIndent(widthIndex);
        basePoint.translate(discIndent, 0);
        
        discLabel.setLocation(basePoint);
    }
    
    private void setDiscLocation(int section, int height, DiscLabel oDisc)
    {
        setDiscLocation(oDisc, section, height, oDisc.getWidthIndex());
    }
    
    private int getDiscIndent(int widthIndex)
    {
        return ((widthIndex) * _layout.getDiscWidthBase() / 2) * -1;
    }
    
}

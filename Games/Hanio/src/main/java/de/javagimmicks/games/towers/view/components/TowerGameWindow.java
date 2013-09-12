package de.javagimmicks.games.towers.view.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import de.javagimmicks.games.towers.controller.UserGameMouseListener;
import de.javagimmicks.games.towers.model.Game;
import de.javagimmicks.games.towers.model.GameConfiguration;
import de.javagimmicks.games.towers.model.GameException;
import de.javagimmicks.games.towers.solver.Command;
import de.javagimmicks.games.towers.solver.Solver;


public class TowerGameWindow extends JFrame
{
    private static final long serialVersionUID = -6897079791045327216L;

    private static final int SMALL_GAP = 5;
    private static final int BIG_GAP = 10;

    private static final Dimension SMALL_HGAP = new Dimension(SMALL_GAP, 0);
    private static final Dimension BIG_HGAP = new Dimension(BIG_GAP, 0);

    private final JPanel _windowContent;
    private final JButton _demoButton;
    private final JButton _playButton;
    private final JSpinner _discField;
    private final JSpinner _stickField;
    private final JSpinner _speedField;
    private final JTextField _stepCounterField;
    
    private int _stepCounter;
    private JScrollPane _gamePanel;
    private Thread _demoThread;
    
    public TowerGameWindow()
    {
        super("Towers of Hanoi");
        
        _playButton = new JButton("Play");
        _playButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                startGame();
            }
        });
        
        _demoButton = new JButton("Demo");
        _demoButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                startDemo();
            }
        });
        
        // Create the spinners
        SpinnerNumberModel discNumberModel = new SpinnerNumberModel(3, 2, 10, 1);
        _discField = new JSpinner(discNumberModel);

        SpinnerNumberModel stickNumberModel = new SpinnerNumberModel(3, 3, 9, 1);
        _stickField = new JSpinner(stickNumberModel);
        
        SpinnerNumberModel speedNumberModel = new SpinnerNumberModel(5, 1, 10, 1);
        _speedField = new JSpinner(speedNumberModel);

        // Create a box for the settings (with a nice titled border)
        Box bottomBar = Box.createHorizontalBox();
        bottomBar.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Settings"),
                    BorderFactory.createEmptyBorder(SMALL_GAP, SMALL_GAP, SMALL_GAP, SMALL_GAP)
                ));
        
        // Add the disc setting area
        bottomBar.add(new JLabel("Discs:"));
        bottomBar.add(Box.createRigidArea(SMALL_HGAP));
        bottomBar.add(_discField);

        bottomBar.add(Box.createRigidArea(BIG_HGAP));
        
        // Add the stick setting area
        bottomBar.add(new JLabel("Sticks:"));
        bottomBar.add(Box.createRigidArea(SMALL_HGAP));
        bottomBar.add(_stickField);
        
        createSeparatingArea(bottomBar);
        
        // Add the play button
        bottomBar.add(_playButton);

        createSeparatingArea(bottomBar);

        // Add the demo button
        bottomBar.add(Box.createRigidArea(SMALL_HGAP));
        bottomBar.add(_demoButton);

        bottomBar.add(Box.createRigidArea(BIG_HGAP));

        // Add the demo speed setting area
        bottomBar.add(new JLabel("Speed"));
        bottomBar.add(Box.createRigidArea(SMALL_HGAP));
        bottomBar.add(_speedField);

        // Create the step counter text field
        _stepCounterField = new JTextField();
        _stepCounterField.setEditable(false);
        _stepCounterField.setColumns(4);
        _stepCounterField.setHorizontalAlignment(JTextField.CENTER);
        _stepCounterField.setBorder(null);
        Font stepCounterFont = _stepCounterField.getFont();
        stepCounterFont = new Font(stepCounterFont.getName(), Font.BOLD, stepCounterFont.getSize() * 3);
        _stepCounterField.setFont(stepCounterFont);
        setStepCounterFieldContent();

        // Create a titled pane for it and place it there
        JPanel stepCounterBar = new JPanel(new BorderLayout());
        stepCounterBar.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Step counter"),
                    BorderFactory.createEmptyBorder(SMALL_GAP, SMALL_GAP, SMALL_GAP, SMALL_GAP)
                ));
        stepCounterBar.add(_stepCounterField, BorderLayout.CENTER);
        
        // Create the window content JPanel and add the boxes at the respective locations
        _windowContent = new JPanel(new BorderLayout(BIG_GAP, BIG_GAP));
        _windowContent.setBorder(BorderFactory.createEmptyBorder(BIG_GAP, BIG_GAP, BIG_GAP, BIG_GAP));
        _windowContent.add(bottomBar, BorderLayout.SOUTH);
        _windowContent.add(stepCounterBar, BorderLayout.EAST);
     
        // Set the window content
        getContentPane().add(_windowContent);
        pack();
        
        startGame();
    }
    
    public void incStepCounter()
    {
        SwingUtilities.invokeLater(new Runnable(){
            public void run()
            {
            	++_stepCounter;
            	setStepCounterFieldContent();
            }
        });
    }
    
    private void setStepCounterFieldContent()
    {
        _stepCounterField.setText(String.valueOf(_stepCounter));
    }
    
    private static void createSeparatingArea(Box box)
    {
        box.add(Box.createHorizontalGlue());
        box.add(Box.createRigidArea(BIG_HGAP));
        box.add(Box.createHorizontalGlue());
        box.add(new JSeparator(JSeparator.VERTICAL));
        box.add(Box.createHorizontalGlue());
        box.add(Box.createRigidArea(BIG_HGAP));
        box.add(Box.createHorizontalGlue());
    }
    
    private void startDemo()
    {
        // Get game configuration and create a new game of it
        GameConfiguration gc = getGameConfiguration();
        final Game game = gc.createNewGame();
        
        // Create a new pane for the current game
        final TowerGamePane gamePane = new TowerGamePane(game);
        gamePane.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Demo"),
                    BorderFactory.createEmptyBorder(SMALL_GAP, SMALL_GAP, SMALL_GAP, SMALL_GAP)
                ));
        
        // Reset existing game and set the game pane as new game content
        reset();
        setNewGameContent(gamePane);

        // Get the name of the last stick
        List<String> stickNames = gc.getStickNames();
        String lastStickName = stickNames.get(stickNames.size() - 1);
        
        // Calculate the solution
        final List<Command> solution = new Solver(gc).getSolution(lastStickName);
        
        final int delay = 1000 - ((Integer)_speedField.getValue() - 1) * 100;
        
        // Setup a new thread for sending the demo commands
        _demoThread = new Thread()
        {
            public void run()
            {
                for(Command command : solution)
                {
                    // Sleep a half second
                    try
                    {
                        Thread.sleep(delay);
                    }
                    catch(InterruptedException e)
                    {
                        break;
                    }
                    
                    // Execute the next command
                    try
                    {
                        command.executeOn(game);
                        incStepCounter();
                    }
                    catch(GameException e)
                    {
                        break;
                    }
                    
                    // Redraw the disc labels
                    gamePane.redraw();
                }
            }
        };
        
        // Run the demo thread
        _demoThread.start();
    }
    
    private void startGame()
    {
        // Get game configuration and create a new game of it
        GameConfiguration gc = getGameConfiguration();
        final Game game = gc.createNewGame();
        
        // Create a new pane for the current game
        TowerGamePane gamePane = new TowerGamePane(game);
        gamePane.setBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createTitledBorder("Game"),
                    BorderFactory.createEmptyBorder(SMALL_GAP, SMALL_GAP, SMALL_GAP, SMALL_GAP)
                ));
        
        // Add the controller for the user (only a MouseListener)
        gamePane.addMouseListener(new UserGameMouseListener(this, gamePane));

        // Reset existing game and set the game pane as new game content
        reset();
        setNewGameContent(gamePane);
    }
    
    private GameConfiguration getGameConfiguration()
    {
        int discCount = (Integer)_discField.getValue();
        int stickCount = (Integer)_stickField.getValue();
        
        LinkedList<String> stickNames = new LinkedList<String>();
        for(int i = 1; i <= stickCount; ++i)
        {
            stickNames.add("Stick " + String.valueOf(i));
        }
        
        return new GameConfiguration(stickNames, stickNames.getFirst(), discCount);
    }
    
    private void setNewGameContent(TowerGamePane gamePane)
    {
        // Create new game content (place it in a scroll pane without border)
        _gamePanel = new JScrollPane(gamePane);
        _gamePanel.setBorder(null);
        
        // Add the new game content and reorganize the window layout
        _windowContent.add(_gamePanel, BorderLayout.CENTER);
        pack();
    }
    
    private void reset()
    {
        // Reset the step counter
        _stepCounter = 0;
        setStepCounterFieldContent();
        
        // Remove old game content, if existing
        if(_gamePanel != null)
        {
            _windowContent.remove(_gamePanel);
            _gamePanel = null;
        }
     
        // Clean demo thread, if existing
        if(_demoThread != null)
        {
            if(_demoThread.isAlive())
            {
                _demoThread.interrupt();
            }
            
            _demoThread = null;
        }
    }
}

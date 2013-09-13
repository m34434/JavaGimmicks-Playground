package de.javagimmicks.games.jotris;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.javagimmicks.games.jotris.controller.StandardControlKeyListener;
import de.javagimmicks.games.jotris.controller.StartGameAction;
import de.javagimmicks.games.jotris.controller.StopGameAction;
import de.javagimmicks.games.jotris.model.JoTrisModel;
import de.javagimmicks.games.jotris.model.TileFactory;
import de.javagimmicks.games.jotris.model.impl.DefaultJoTrisModel;
import de.javagimmicks.games.jotris.model.impl.DefaultTileFactory;
import de.javagimmicks.games.jotris.model.impl.TileUtil;
import de.javagimmicks.games.jotris.view.JoTrisGrid;
import de.javagimmicks.games.jotris.view.JoTrisPreviewGrid;
import de.javagimmicks.games.jotris.view.JoTrisRowsField;
import de.javagimmicks.games.jotris.view.JoTrisScoreField;

public class JoTrisApplet extends JApplet
{
   private static final long serialVersionUID = -6330813054905517666L;  

   private JoTrisModel _model;
   
   public JoTrisApplet()
   {
   }

   @Override
   public void init()
   {
      // Create the TileFactory to use
      TileFactory tileFactory = DefaultTileFactory.getDefaultInstance();

      // Prepare the Model
      _model = new DefaultJoTrisModel(16, 8, tileFactory);
      
      // Create the JoTris GUI components for the model
      JoTrisGrid grid = new JoTrisGrid(_model, 22);
      grid.setBorder(BorderFactory.createLoweredBevelBorder());
      JoTrisPreviewGrid previewGrid = new JoTrisPreviewGrid(_model, TileUtil.getCommonMinimalFormat(tileFactory.getPrototypes()), 22);
      JoTrisRowsField rowsBox = new JoTrisRowsField(_model);
      JoTrisScoreField scoreBox = new JoTrisScoreField(_model);
      
      // Setup panels for statistics information
      JPanel panelRowsCompleted = new JPanel(new GridLayout(1, 2, 5, 5));
      panelRowsCompleted.add(new JLabel("Rows:"));
      panelRowsCompleted.add(rowsBox);
      
      JPanel panelScore = new JPanel(new GridLayout(1, 2, 5, 5));
      panelScore.add(new JLabel("Score:"));
      panelScore.add(scoreBox);
      
      JPanel panelStatistics = new JPanel(new GridLayout(1, 2, 2, 2));
      panelStatistics.add(panelRowsCompleted);
      panelStatistics.add(panelScore);
      
      JPanel panelPreview = new JPanel(new GridLayout(1, 2, 5, 5));
      panelPreview.add(new JLabel("Preview:"));
      panelPreview.add(previewGrid);

      JPanel panelTop = new JPanel();
      panelTop.setLayout(new BoxLayout(panelTop, BoxLayout.Y_AXIS));
      panelTop.add(panelStatistics);
      panelTop.add(panelPreview);
      
      // Setup panel with start/stop actions
      JButton buttonStart = new JButton(new StartGameAction(_model, "Start"));
      JButton buttonStop = new JButton(new StopGameAction(_model, "Stop"));
      buttonStart.setFocusable(false);
      buttonStop.setFocusable(false);

      JPanel panelStartStop = new JPanel(new GridLayout(1, 2, 2, 2));
      panelStartStop.add(buttonStart);
      panelStartStop.add(buttonStop);

      Run.setBorder(panelRowsCompleted);
      Run.setBorder(panelScore);
      Run.setBorder(panelPreview);
      
      // Setup a winow for displaying the grid
      JPanel panelWindowContent = new JPanel(new BorderLayout(5, 5));
      panelWindowContent.add(panelTop, BorderLayout.NORTH);
      panelWindowContent.add(grid, BorderLayout.CENTER);
      panelWindowContent.add(panelStartStop, BorderLayout.SOUTH);

      this.getContentPane().add(panelWindowContent);
      this.addKeyListener(new StandardControlKeyListener(_model, false));
   }

   @Override
   public void destroy()
   {
      _model.setAutoMoveEnabled(false);
      _model.stopGame();
      _model = null;
   }

   @Override
   public void start()
   {
      _model.setAutoMoveEnabled(true);
   }

   @Override
   public void stop()
   {
      _model.setAutoMoveEnabled(false);
   }
}

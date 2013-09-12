package de.javagimmicks.games.inkognito.client.net.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import de.javagimmicks.games.inkognito.client.net.NetworkClient;
import de.javagimmicks.games.inkognito.client.net.NetworkPlayer;
import de.javagimmicks.games.inkognito.model.Card;
import de.javagimmicks.games.inkognito.model.CardType;
import de.javagimmicks.games.inkognito.model.Location;

public class GUINetworkPlayer extends JFrame implements NetworkPlayer
{
	private static final long serialVersionUID = -4077484836430833144L;

	public static void main(String[] args)
	{
		String sHost;
		int iPort;
		
		if(args.length == 2)
		{
			sHost = args[0];
			iPort = Integer.parseInt(args[1]);
		}
		else
		{
			sHost = "localhost";
			iPort = 6201;
		}
		
		new GUINetworkPlayer(sHost, iPort);
	}
	
	private final JList<Object> m_oMainLog;
	private final JTextField m_oCommandField = new JTextField();
	
	private final JButton m_oSubmitCommand = new JButton("Send command:");
	private final JButton m_oStartButton = new JButton("Start new game");
	
	private final JToolBar m_oPlayerToolBar = new JToolBar();	
	private final JTabbedPane m_oLogPane = new JTabbedPane();
	
	private final Map<String, JList<Object>> m_oLogLists = new HashMap<String, JList<Object>>();
	
	private final String m_sHost;
	private final int m_iPort;
	
	private GameThread m_oGameThread;
	
	public GUINetworkPlayer(String sHost, int iPort)
	{
		super("Inkognito GUI client");
		
		m_sHost = sHost;
		m_iPort = iPort;
		
		m_oMainLog = createLogList();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		initComponents();
		pack();
		setSize(new Dimension(700, 500));
		setVisible(true);
	}
	
	private void logMain(Object oEntry)
	{
		ConsoleListModel<Object> oModel = (ConsoleListModel<Object>)m_oMainLog.getModel();
		oModel.add(oEntry);
	}
	
	private void logConsole(String sKey, Object oEntry)
	{
		JList<Object> oList = m_oLogLists.get(sKey);
		
		if(oList == null)
		{
			oList = createLogList();
			m_oLogLists.put(sKey, oList);
			m_oLogPane.addTab(sKey, new JScrollPane(oList));
		}
		
		ConsoleListModel<Object> oModel = (ConsoleListModel<Object>)oList.getModel();
		oModel.add(oEntry);
	}
	
	private void initComponents()
	{
		Border oStandardBorder = BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5));
		
		m_oStartButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				m_oStartButton.setEnabled(false);
				m_oCommandField.setText("");
				m_oMainLog.setModel(new ConsoleListModel<Object>(m_oMainLog));

				for(int i = m_oLogPane.getTabCount() - 1; i > 0 ; --i)
				{
					m_oLogPane.removeTabAt(i);
				}
				m_oLogLists.clear();
				
				for(int i = m_oPlayerToolBar.getComponentCount() - 1; i > 1; --i)
				{
					m_oPlayerToolBar.remove(i);
				}
				m_oPlayerToolBar.repaint();
				
				m_oGameThread = new GameThread();
				m_oGameThread.start();
			}
		});
		
		m_oSubmitCommand.setEnabled(false);
		m_oSubmitCommand.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				synchronized(GUINetworkPlayer.this)
				{
					GUINetworkPlayer.this.notify();
				}
			}
		});
		
		JButton oClearButton = new JButton("Clear");
		oClearButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				m_oCommandField.setText("");
			}
		});
		
		JToolBar oNameCards = new JToolBar();
		oNameCards.add(new JLabel("Names: "));
		oNameCards.addSeparator();
		for(Card oNameCard : Card.getCardsByType(CardType.Name))
		{
			oNameCards.add(new AddTextAction(oNameCard.toString()));
		}

		JToolBar oTelephoneCards = new JToolBar();
		oTelephoneCards.add(new JLabel("Telephones: "));
		oTelephoneCards.addSeparator();
		for(Card oTelephoneCard : Card.getCardsByType(CardType.Telephone))
		{
			oTelephoneCards.add(new AddTextAction(oTelephoneCard.toString()));
		}
		
		JToolBar oLocationCards = new JToolBar();
		oLocationCards.add(new JLabel("Locations: "));
		oLocationCards.addSeparator();
		for(Location oLocation : Location.values())
		{
			oLocationCards.add(new AddTextAction(oLocation.toString()));
		}

		m_oPlayerToolBar.add(new JLabel("Players: "));
		m_oPlayerToolBar.addSeparator();
		
		m_oLogPane.addTab("Main log", new JScrollPane(m_oMainLog));
		
		JPanel oCommandPanel = new JPanel(new BorderLayout(10, 10));
		oCommandPanel.add(m_oSubmitCommand, BorderLayout.WEST);
		oCommandPanel.add(m_oCommandField, BorderLayout.CENTER);
		oCommandPanel.add(oClearButton, BorderLayout.EAST);

		oNameCards.setBorder(oStandardBorder);
		oTelephoneCards.setBorder(oStandardBorder);
		oLocationCards.setBorder(oStandardBorder);
		m_oPlayerToolBar.setBorder(oStandardBorder);
		oCommandPanel.setBorder(oStandardBorder);
		
		JPanel oButtons = new JPanel(new GridLayout(5, 1, 10, 10));
		oButtons.add(oLocationCards);
		oButtons.add(oNameCards);
		oButtons.add(oTelephoneCards);
		oButtons.add(m_oPlayerToolBar);
		oButtons.add(oCommandPanel);
		
		JPanel oWindowContent = new JPanel(new BorderLayout(10, 10));
		oWindowContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		oWindowContent.add(oButtons, BorderLayout.NORTH);
//		oWindowContent.add(new JScrollPane(m_oMainLog));
		oWindowContent.add(m_oLogPane);
		oWindowContent.add(m_oStartButton, BorderLayout.SOUTH);
		
		getContentPane().add(oWindowContent);
	}
	
	private static JList<Object> createLogList()
	{
		JList<Object> oResult = new JList<Object>();
		oResult.setModel(new ConsoleListModel<Object>(oResult));
		oResult.setCellRenderer(new ListRenderer<Object>());
		oResult.setEnabled(false);
		oResult.setVisibleRowCount(20);
		
		return oResult;
	}
	
	public String process(String sMessage)
	{
		if(sMessage == null || sMessage.length() == 0)
		{
			return null;
		}
		
		final String[] arrMessage = sMessage.split(" ");
		String sMessageType = arrMessage[0];
		Request oRequest = new Request(sMessage);

		boolean bNotification = sMessageType.endsWith("!");
		boolean bRequest = sMessageType.endsWith("?");
		
		if(bNotification)
		{
			if("seefrom!".equals(sMessageType))
			{
				String sKey = arrMessage[1] + " (see)";
				logConsole(sKey, oRequest);
			}
			else if("moveto!".equals(sMessageType))
			{
				String sKey = arrMessage[1] + " (move)";
				logConsole(sKey, oRequest);
			}
			else if("names!".equals(sMessageType))
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						for(int i = 1; i < arrMessage.length; ++i)
						{
							m_oPlayerToolBar.add(new AddTextAction(arrMessage[i]));
						}
					}
				});
			}
			
			logMain(oRequest);
			return null;
		}
		else if(bRequest)
		{
			logMain(oRequest);
			
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					m_oSubmitCommand.setEnabled(true);
					m_oCommandField.setText("");
				}
			});
			
			synchronized (this)
			{
				try
				{
					this.wait();
				}
				catch (InterruptedException e)
				{
					return null;
				}
				finally
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							m_oSubmitCommand.setEnabled(false);
						}
					});
				}
			}
			
			String sAnswer = m_oCommandField.getText();
			Answer oAnswer = new Answer(sAnswer);

			if("showto?".equals(sMessageType))
			{
				String sWho = arrMessage[1];
				String sKey = ("envoy".equals(sWho) ? arrMessage[2] : sWho) + " (show)";
				
				logConsole(sKey, oAnswer);
			}
			else if("moveto?".equals(sMessageType))
			{
				String sKey = "Self (move)";
				logConsole(sKey, oAnswer);
			}
			
			logMain(oAnswer);
			return sAnswer;
		}
		
		return null;
	}
	
	private class GameThread extends Thread
	{
		public void run()
		{
			try
			{
				NetworkClient.joinNetworkGame(m_sHost, m_iPort, GUINetworkPlayer.this);
			}
			catch (IOException ex)
			{
				JOptionPane.showMessageDialog(GUINetworkPlayer.this, "Connection error", "Error occured", JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
			finally
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						m_oStartButton.setEnabled(true);
					}
				});
			}
		}
	}
	
	private static class ConsoleListModel<E> extends AbstractListModel<E>
	{
		private static final long serialVersionUID = -1291969235452272493L;

		private final ArrayList<E> m_oEntries = new ArrayList<E>();
		private final JList<E> m_oAssociatedList;
		
		public ConsoleListModel(final JList<E> oAssociatedList)
		{
			m_oAssociatedList = oAssociatedList;
		}

		public E getElementAt(int index)
		{
			return m_oEntries.get(index);
		}

		public int getSize()
		{
			return m_oEntries.size();
		}
		
		public void add(E o)
		{
			final int iSize = m_oEntries.size();
			
			m_oEntries.add(o);
			
			fireIntervalAdded(this, iSize, iSize);

			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					m_oAssociatedList.ensureIndexIsVisible(iSize);
					SwingUtilities.updateComponentTreeUI(m_oAssociatedList);
				}
			});
		}
	}
	
	private class Request
	{
		private final String m_sContent;

		public Request(final String oContent)
		{
			m_sContent = oContent;
		}

		public String toString()
		{
			return m_sContent;
		}
	}

	private class Answer
	{
		private final String m_sContent;

		public Answer(final String oContent)
		{
			m_sContent = oContent;
		}

		public String toString()
		{
			return m_sContent;
		}
	}

	private class AddTextAction extends AbstractAction
	{
		private static final long serialVersionUID = 2026114121212669558L;

		public AddTextAction(String sText)
		{
			super(sText);
		}

		public void actionPerformed(ActionEvent e)
		{
			String sNewText = getValue(Action.NAME).toString();
			String sCurrentText = m_oCommandField.getText();
			
			if(sCurrentText.length() == 0)
			{
				m_oCommandField.setText(sNewText);
			}
			else
			{
				StringBuffer oBuffer = new StringBuffer()
					.append(sCurrentText)
					.append(' ')
					.append(sNewText);
				m_oCommandField.setText(oBuffer.toString());
			}
		}
	}
	
	private static class ListRenderer<E> implements ListCellRenderer<E>
	{
		public Component getListCellRendererComponent(JList<? extends E> oList, E oValue, int iIndex, boolean bSelected, boolean bHasFocus)
		{
			JPanel oResult = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
			oResult.setBackground(Color.WHITE);

			Border oBorder = BorderFactory.createEtchedBorder();
			boolean bRequest = oValue instanceof Request;
			
			String[] arrValues = oValue.toString().split(" ");
			for(int i = 0; i < arrValues.length; ++i)
			{
				Color oBackgroundColor;
				if(bRequest)
				{
					if(i == 0)
					{
						oBackgroundColor = GREEN;
					}
					else
					{
						oBackgroundColor = BLUE;
					}
				}
				else
				{
					oBackgroundColor = RED;
				}
				
				JLabel oLabel = new JLabel(arrValues[i]);
				oLabel.setBorder(oBorder);
				oLabel.setOpaque(true);
				oLabel.setBackground(oBackgroundColor);
				
				oResult.add(oLabel);
			}
			oResult.setBorder(oBorder);			
			return oResult;
		}
		
		private final Color GREEN = new Color(127, 255, 127);
		private final Color RED = new Color(255, 127, 127);
		private final Color BLUE = new Color(127, 127, 255);
	}
}

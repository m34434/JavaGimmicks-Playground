package de.javagimmicks.apps.chat.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import de.javagimmicks.apps.chat.client.ChatClient;
import de.javagimmicks.apps.chat.client.ChatClientListener;
import de.javagimmicks.apps.chat.client.ClientException;
import de.javagimmicks.apps.chat.client.MessageEvent;
import de.javagimmicks.apps.chat.client.UserEvent;


public class FrameChat extends JFrame
{
   private static final long serialVersionUID = -7473291329814778231L;

   private final static String LINE_SEP = System.getProperty("line.separator");

   private final ChatClient _client;
  
   protected final JTextPane _txtMessages = new JTextPane();
   protected final DefaultListModel _usersModel = new DefaultListModel();
   protected final JTextField _txtMessage = new JTextField();
   protected final JButton _btnSend = new JButton("Send");
   
   private final Map<String, Color> _userColors = new TreeMap<String, Color>();
   
   public FrameChat(ChatClient client) throws ClientException
   {
      super("GimmicksChat [" + client.getUserName() + "]");
      
      _client = client;
      _client.addListener(new ClientListener());
      
      _txtMessages.setEditable(false);

      _btnSend.addActionListener(new SendActionListener());

      final JList usersList = new JList(_usersModel);
      usersList.setCellRenderer(new UserListCellRenderer(_userColors));
      usersList.setBorder(BorderFactory.createCompoundBorder(
         usersList.getBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

      final JScrollPane scrollPaneUsers = new JScrollPane(usersList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scrollPaneUsers.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      
      final JSplitPane pnlMessages = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
      pnlMessages.setResizeWeight(1.0);
      pnlMessages.setLeftComponent(new JScrollPane(_txtMessages));
      pnlMessages.setRightComponent(scrollPaneUsers);
      
      final JPanel pnlSend = new JPanel(new BorderLayout(10, 10));
      pnlSend.add(_txtMessage, BorderLayout.CENTER);
      pnlSend.add(_btnSend, BorderLayout.EAST);
      
      final JPanel pnlMain = new JPanel(new BorderLayout(10, 10));
      pnlMain.add(pnlMessages, BorderLayout.CENTER);
      pnlMain.add(pnlSend, BorderLayout.SOUTH);
      
      addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowOpened(WindowEvent e)
         {
            pnlMessages.setDividerLocation(0.8);
            _txtMessage.grabFocus();
         }

         @Override
         public void windowClosing(WindowEvent e)
         {
            _client.disconnect();
         }
      });
      
      getRootPane().setDefaultButton(_btnSend);
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      getContentPane().add(pnlMain);
      setSize(new Dimension(800, 600));
      
      _client.connect();
   }
   
   private void message(String messageText, AttributeSet attributes)
   {
      Document chatDocument = _txtMessages.getDocument();

      try
      {
         chatDocument.insertString(chatDocument.getLength(), messageText, attributes);
      }
      catch (BadLocationException e)
      {
      }

      _txtMessages.setCaretPosition(chatDocument.getLength());
   }
   
   private void message(String messageText)
   {
      message(messageText, null);
   }

   private class SendActionListener implements ActionListener
   {
      private static final String COMMAND_WHISPER = "^w ";

      public void actionPerformed(ActionEvent e)
      {
         final String text = _txtMessage.getText();
         
         if(text == null || text.length() == 0)
         {
            return;
         }
         
         try
         {
            if(text.startsWith(COMMAND_WHISPER))
            {
               final String userName = new StringTokenizer(text.substring(COMMAND_WHISPER.length())).nextToken();
               final String message = text.substring(COMMAND_WHISPER.length() + userName.length() + 1).trim();
               
               _client.whisper(message, userName);
               _txtMessage.setText("");
            }
            else
            {
               _client.send(text);
               _txtMessage.setText("");
            }
         }
         catch(ClientException ex)
         {
            ex.printStackTrace();
         }
      }
   }

   private class ClientListener implements ChatClientListener
   {
      public void connectionClosed(ChatClient client)
      {
         message("[Connection closed]");
         _btnSend.setEnabled(false);
      }

      public void messageReceived(ChatClient client, MessageEvent event)
      {
         SimpleAttributeSet userAttributes = new SimpleAttributeSet();
         StyleConstants.setBold(userAttributes, true);
         StyleConstants.setForeground(userAttributes, event.getUserColor());

         StringBuilder userTextBuilder = new StringBuilder().append(event.getUserName());
         if(event.isWhispered())
         {
            userTextBuilder.append(" [whispered]");
         }
         userTextBuilder.append(":").append(LINE_SEP);

         String messageText = new StringBuilder()
            .append(event.getMessage())
            .append(LINE_SEP)
            .append(LINE_SEP)
            .toString();
         
         message(userTextBuilder.toString(), userAttributes);
         message(messageText);
      }
      
      public void userJoined(ChatClient client, UserEvent event)
      {
         final String userName = event.getUserName(); 
         
         _userColors.put(userName, event.getUserColor());
         _usersModel.addElement(userName);
      }

      public void userLeft(ChatClient client, UserEvent event)
      {
         final String userName = event.getUserName(); 

         _usersModel.removeElement(userName);
         _userColors.remove(userName);
      }
   }
}

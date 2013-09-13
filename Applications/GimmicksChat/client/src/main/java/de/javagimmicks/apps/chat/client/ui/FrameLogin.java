package de.javagimmicks.apps.chat.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import de.javagimmicks.apps.chat.client.ChatClient;
import de.javagimmicks.apps.chat.client.ClientException;

public class FrameLogin extends JFrame
{
   private static final long serialVersionUID = 4488959252265620456L;

   protected final JTextField _txtHost = new JTextField();
   protected final JTextField _txtUsername = new JTextField();
   protected final JPasswordField _txtPassword = new JPasswordField();
   protected final JButton _btnColor = new JButton();
   
   public static void main(String[] args)
   {
      new FrameLogin().setVisible(true);
   }
   
   public FrameLogin()
   {
      super("GimmicksChat login");
      
      _txtHost.setText("localhost");
      
      final JPanel pnlData = new JPanel(new GridLayout(4, 2, 10, 10));
      pnlData.setBorder(BorderFactory.createTitledBorder("Specify login data"));
      pnlData.add(new JLabel("Username"));
      pnlData.add(_txtUsername);
      pnlData.add(new JLabel("Password"));
      pnlData.add(_txtPassword);
      pnlData.add(new JLabel("Choose color"));
      pnlData.add(_btnColor);
      pnlData.add(new JLabel("GimmicksChat host"));
      pnlData.add(_txtHost);
      
      final JButton btnLogin = new JButton(new LoginAction());;

      final JPanel pnlMain = new JPanel(new BorderLayout(10, 10));
      pnlMain.add(pnlData, BorderLayout.CENTER);
      pnlMain.add(btnLogin, BorderLayout.SOUTH);
      
      ColorChooserActionListener colorListener = new ColorChooserActionListener();
      _btnColor.addActionListener(colorListener);
      _btnColor.setBackground(Color.BLACK);
      
      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      getRootPane().setDefaultButton(btnLogin);
      getContentPane().add(pnlMain);
      pack();
   }

   private class ColorChooserActionListener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)
      {
         Color chosenColor = JColorChooser.showDialog(FrameLogin.this, "Choose color", _btnColor.getBackground());
         _btnColor.setBackground(chosenColor == null ? Color.BLACK : chosenColor);
      }
   }

   private class LoginAction extends AbstractAction
   {
      private static final long serialVersionUID = -1295133205702949258L;

      public LoginAction()
      {
         super("Login");
      }
      
      public void actionPerformed(ActionEvent e)
      {
         final String host = _txtHost.getText();
         if(host == null || host.length() == 0)
         {
            return;
         }
         
         final String username = _txtUsername.getText();
         if(username == null || username.length() == 0 || username.startsWith("["))
         {
            return;
         }
   
         final String password = new String(_txtPassword.getPassword());
         if(password == null)
         {
            return;
         }
         
         try
         {
            startClient(host, username, password);
         }
         catch (Exception ex)
         {
            JOptionPane.showMessageDialog(FrameLogin.this, ex.getMessage(), "Error occured", JOptionPane.ERROR_MESSAGE);
         }
         finally
         {
            setVisible(false);
            dispose();
         }
      }
      
      private void startClient(String host, String username, String password)
         throws ClientException
      {
         final ChatClient client = new ChatClient(username, password, _btnColor.getBackground(), host);
         
         new FrameChat(client).setVisible(true);
      }
   }
}

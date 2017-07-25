package edu.flash3388.flashlib.gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

import edu.flash3388.flashlib.dashboard.Remote;
import edu.flash3388.flashlib.dashboard.Remote.RemoteHost;
import edu.flash3388.flashlib.dashboard.Remote.User;
import edu.flash3388.flashlib.util.FlashUtil;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ShellWindow extends Stage{

	public static enum ChannelType{
		SSH, SFTP, SCP
	}
	
	private Channel channel;
	private TextArea textArea;
	private OutputStream outStream;
	private InStream inStream;
	private StringBuilder enteredVal = new StringBuilder();
	
	public ShellWindow(Stage owner, String chN, RemoteHost host, User user){
		initOwner(owner);
		setTitle(chN + "-" + user.getUsername() + "@" + host.getHostname() + ":" + 22);
		setResizable(false);
		setOnCloseRequest((e)->disconnect());
		createScene();
	}
	private void enterText(){
		inStream.setInput(enteredVal.toString());
		enteredVal.delete(0, enteredVal.length());
	}
	private boolean validateDelete(){
		return enteredVal.length() > 0 && textArea.getCaretPosition() > textArea.getLength() - enteredVal.length();
	}
	private void createScene(){
		textArea = new TextArea();
		textArea.setPrefSize(500, 300);
		textArea.setStyle("-fx-control-inner-background:#000000;");
		//textArea.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		textArea.setFont(Font.font(10));
		textArea.addEventFilter(KeyEvent.KEY_PRESSED, (e)->{
			if(e.getCode() == KeyCode.ENTER){
				enteredVal.append("\r");
				enterText();
			}
			else if(e.getCode() == KeyCode.BACK_SPACE || e.getCode() == KeyCode.DELETE){
				if(!validateDelete())
					e.consume();
				else {
					int pos = textArea.getLength() - textArea.getCaretPosition();
					enteredVal.delete(pos, pos+1);
				}
			}
			else enteredVal.append(e.getText());
		});
		
		outStream = new OutputStream(){
			@Override
			public void write(int b) throws IOException {
				textArea.appendText(String.valueOf((char)b));
			}
		};
		inStream = new InStream();
		
		VBox vbox = new VBox();
		vbox.getChildren().add(textArea);
		setScene(new Scene(vbox, 500, 300));
	}
	private void openChannel(RemoteHost host, User user, String channel){
		UserInfo ui = new MyUserInfo(){
			PrintStream pStream = new PrintStream(outStream);
			Scanner in = new Scanner(inStream);
			  public void showMessage(String message){ 
				  pStream.println(message);
			  }
			  public boolean promptYesNo(String str){ 
				  pStream.println(str);
				  String answer = in.next();
				  return answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("y");
			  }
		};
		boolean con = host.getSession() != null && host.getSession().isConnected();
		if(!con)
			con = host.openSession(user, 3000);
		if(con){
			host.getSession().setUserInfo(ui);
			this.channel = host.openChannel(channel, 3000);
			if(this.channel != null){
				FlashUtil.getLog().log("Channel "+channel+" Connected - "+user.getUsername()+"@"+host.getHostname());
				this.channel.setInputStream(inStream);
				this.channel.setOutputStream(outStream);
			}
		}
	}
	private void disconnect(){
		if (channel != null && channel.isConnected() && !channel.isClosed()) 
			channel.disconnect();
	}
	private static String channelFromType(ChannelType t){
		switch (t) {
			case SSH: return Remote.CHANNEL_SHELL;
			case SCP:
				break;
			case SFTP: return Remote.CHANNEL_SFTP;
		}
		return null;
	}
	public static void showShellWindow(Stage owner, ChannelType t){
		Object[] obj = ChannelInputWindow.showInput();
		if(obj == null)
			return;
		
		RemoteHost host = (RemoteHost) obj[0];
		User user = (User) obj[1];
		String chN = channelFromType(t);
		ShellWindow shell = new ShellWindow(owner, chN, host, user);
		shell.show();
		shell.openChannel(host, user, chN);
	}
	
	private static class InStream extends InputStream{

		private char[] input = new char[0];
		private int index = 0;
		
		@Override
		public int read() throws IOException {
			if(index >= input.length) return -1;
			return input[index++];
		}
		@Override
		 public int available() throws IOException {
	        return input.length - index;
	    }
		
		public void setInput(String s){
			index = 0;
			input = s.toCharArray();
		}
	}
	public static abstract class MyUserInfo implements UserInfo, UIKeyboardInteractive{
		public String getPassword(){ return null; }
		public boolean promptYesNo(String str){ return false; }
		public String getPassphrase(){ return null; }
		public boolean promptPassphrase(String message){ return false; }
		public boolean promptPassword(String message){ return false; }
		public void showMessage(String message){ }
		public String[] promptKeyboardInteractive(String destination,
                        String name,
                        String instruction,
                        String[] prompt,
                        boolean[] echo){
			return null;
		}
	}
}

package edu.flash3388.flashlib.dashboard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import edu.flash3388.flashlib.io.FileStream;
import edu.flash3388.flashlib.util.FlashUtil;

public class Remote {

	public static class RemoteHost{
		private List<User> users = new ArrayList<User>(2);
		private String hostname;
		private Session session;
		
		public RemoteHost(String hostname){
			this.hostname = hostname;
		}
		
		public void setHostname(String hostname){
			this.hostname = hostname;
		}
		public String getHostname(){
			return hostname;
		}
		
		public void addUser(User user){
			users.add(user);
		}
		public void addUser(String username, String password){
			users.add(new User(username, password));
		}
		public boolean removeUser(User user){
			return users.remove(user);
		}
		public int getUsersCount(){
			return users.size();
		}
		public User[] getUsers(){
			return users.toArray(new User[0]);
		}
		public User getUser(int index){
			return users.get(index);
		}
		public User getUser(String username){
			for (int i = 0; i < users.size(); i++) {
				if(users.get(i).getUsername().equals(username))
					return users.get(i);
			}
			return null;
		}
		
		public Channel openChannel(String channel, int timeout){
			if(session == null || !session.isConnected()) return null;
			try {
				Channel ch = Remote.openChannel(session, channel);
				ch.connect(timeout);
				return ch.isConnected()? ch : null;
			} catch (JSchException e) {
				FlashUtil.getLog().reportError(e.getMessage());
				return null;
			}
		}
		public void closeSession(){
			if(session != null && session.isConnected())
				session.disconnect();
			session = null;
		}
		public Session getSession(){
			return session;
		}
		public boolean openSession(User user, int timeout){
			closeSession();
			try {
				session = Remote.openSession(hostname, user.username, user.password);
				session.setConfig("StrictHostKeyChecking", "no");
				session.connect(timeout);
				return session.isConnected();
			} catch (JSchException e) {
				FlashUtil.getLog().reportError(e.getMessage());
				return false;
			}
		}
	}
	public static class User{
		
		private String username;
		private String password;
		
		public User(String username, String password){
			this.username = username;
			this.password = password;
		}
		
		public String getUsername(){
			return username;
		}
		public String getPassword(){
			return password;
		}
		public void setUsername(String username){
			this.username = username;
		}
		public void setPassword(String password){
			this.password = password;
		}
	}
	
	public static final String CHANNEL_SHELL = "shell";
	public static final String CHANNEL_EXECUTOR = "exec";
	
	private static JSch jsch;
	private static List<RemoteHost> hosts = new ArrayList<RemoteHost>(5);
	
	public static void initializeJSCH(){
		jsch = new JSch();
		FlashUtil.getLog().log("Jsch initialized: "+JSch.VERSION);
	}
	
	public static Session openSession(String host, String username) throws JSchException{
		return jsch.getSession(username, host, 22);
	}
	public static Session openSession(String host, String username, String password) throws JSchException{
		Session s = jsch.getSession(username, host, 22);
		s.setPassword(password);
		return s;
	}
	
	public static Channel openShellChannel(Session session) throws JSchException{
		return openChannel(session, CHANNEL_SHELL);
	}
	public static Channel openChannel(Session session, String channel) throws JSchException{
		if(!session.isConnected())
			session.connect();
		return session.openChannel(channel);
	}
	
	public static Channel openChannel(String host, String username, String password, String channelKey,
			InputStream inStream, OutputStream outStream, UserInfo info, int connectionTimeout){
		try {
			Session session = openSession(host, username, password);
			session.setUserInfo(info);
			session.connect(connectionTimeout);
			Channel channel = openChannel(session, channelKey);
			channel.setInputStream(inStream);
			channel.setOutputStream(outStream);
			channel.connect(connectionTimeout);
			return channel;
		} catch (JSchException e) {
		}
		return null;
	}
	
	public static void addRemoteHost(RemoteHost host){
		if (getRemoteHost(host.getHostname()) != null) {
			
			return;
		}
		hosts.add(host);
	}
	public static void addRemoteHost(String hostname){
		addRemoteHost(new RemoteHost(hostname));
	}
	public static int getHostCount(){
		return hosts.size();
	}
	public static RemoteHost[] getHosts(){
		return hosts.toArray(new RemoteHost[0]);
	}
	public static RemoteHost getRemoteHost(int index){
		return hosts.get(index);
	}
	public static RemoteHost getRemoteHost(String hostname){
		for (int i = 0; i < hosts.size(); i++) {
			if(hosts.get(i).getHostname().equals(hostname))
				return hosts.get(i);
		}
		return null;
	}
	public static void closeSessions(){
		for (int i = 0; i < hosts.size(); i++) {
			RemoteHost host = hosts.get(i);
			if(host.getSession() != null && host.getSession().isConnected())
				host.getSession().disconnect();
		}
	}
	
	public static void loadHosts(String file) throws NullPointerException, IOException{
		File fileW = new File(file);
		if(!fileW.exists())
			return;
		String[] lines = FileStream.readAllLines(file); 
		if(lines == null || lines.length < 1) 
			return;
		boolean foundHost = false;
		RemoteHost currentHost = null;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			String[] splits;
			if(line.startsWith("host:") && !foundHost){
				splits = line.split(":");
				if(splits.length < 2) continue;
				String hostname = splits[1];
				currentHost = new RemoteHost(hostname);
				foundHost = true;
			}else if(foundHost && line.startsWith("user:")){
				splits = line.split(":");
				if(splits.length < 2) continue;
				splits = splits[1].split(",");
				if(splits.length < 1) continue;
				User user = new User(splits[0], splits.length > 1? splits[1] : "");
				currentHost.addUser(user);
			}else if(foundHost && line.equals("done")){
				foundHost = false;
				hosts.add(currentHost);
			}
		}
	}
	public static void saveHosts(String file){
		String lines = "";
		RemoteHost[] remoteHosts = getHosts();
		for (int i = 0; i < remoteHosts.length; i++) {
			RemoteHost host = remoteHosts[i];
			lines += "host:"+host.getHostname()+"\n";
			User[] users = host.getUsers();
			for (int j = 0; j < users.length; j++) {
				User user = users[i];
				lines+="\tuser:"+user.getUsername()+","+user.getPassword()+"\n";
			}
			lines+="done\n";
		}
		FileStream.writeLine(file, lines);
	}
}

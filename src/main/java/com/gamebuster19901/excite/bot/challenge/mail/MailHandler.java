package com.gamebuster19901.excite.bot.challenge.mail;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.bot.user.Wii.WiiCode;
import com.gamebuster19901.excite.util.file.File;

public class MailHandler {
	private static final Logger LOGGER = Logger.getLogger(MailHandler.class.getName());
	public static final String SERVER = "rc24.xyz";
	
	public static void main(String[] args) throws IOException {
		receive();
	}
	
	public static void receive() throws IOException {
		File secretFile = new File("./mail.secret");
		String wiiID;
		String password;
		HttpGet request;
		BufferedReader fileReader = null;
		InputStreamReader mailReader = null;
		try {
			fileReader = new BufferedReader(new FileReader(secretFile));
			wiiID = fileReader.readLine();
			password = fileReader.readLine();
			CloseableHttpClient client = HttpClients.createDefault();
			request = new HttpGet("https://mtw." + SERVER + "/cgi-bin/receive.cgi?mlid=" + wiiID + "&passwd=" + password + "&maxsize=11534336");
			
			CloseableHttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			if(entity != null) {
				LOGGER.log(Level.INFO, "Sent mail fetch request");
				InputStream contentStream = entity.getContent();
				mailReader = new InputStreamReader(contentStream);
				char[] data = new char[1];
				StringBuilder content = new StringBuilder();
				while(mailReader.read(data) != -1) {
					content.append(data);
				}
				LOGGER.log(Level.INFO, content.toString());
				parseMail(content.toString());
			}
			else {
				LOGGER.log(Level.INFO, "Response was null");
			}
		}
		finally {
			wiiID = null;
			password = null;
			request = null;
			if(fileReader != null) {
				try {
					fileReader.close();
				}
				finally {
					if(mailReader != null) {
						mailReader.close();
					}
				}
			}
		}
	}
	
	private static void parseMail(String mailData) {
		ArrayList<Mail> mail = new ArrayList<Mail>();
		String delimiter = mailData.substring(0, mailData.indexOf('\r'));
		LOGGER.log(Level.INFO, "Delimiter is: " + delimiter);
		String[] emails = mailData.split(delimiter);
		for(String content : emails) {
			try {
				content = content.substring(30);
				mail.add(analyzeMail(content));
			}
			catch(Exception e) {
				LOGGER.log(Level.WARNING, "Couldn't analayze a mail item: ", e);
			}
		}
	}

	private static Mail analyzeMail(String content) throws MessagingException {
		Session session = Session.getInstance(new Properties());
		InputStream data = new ByteArrayInputStream(content.getBytes());
		MimeMessage message = new MimeMessage(session, data);
		
		LOGGER.log(Level.INFO, "Analyzing mail from: " + message.getFrom()[0]);
		WiiCode wiiCode = Wii.getWiiCodeFromEmail(content);
		if(wiiCode == null) {
			LOGGER.log(Level.INFO, "Ignoring non-wii mail");
			return null;
		}
		
		String subject = message.getSubject() != null ? message.getSubject() : "";
		if(subject.equals("WC24 Cmd Message")) { //friend request
			//if not already registered
				return new FriendRequestMail(wiiCode); //send them a code on their wii and tell them to send it to @Excite#8562 to confirm registration
		}
		
		String[] header = message.getHeader("X-Wii-AppId");
		
		if(header.length > 0 && header[0].equals("1-52583345-0001")) {
			//if user is registered
			    //process mail
			//else
			    //refund if challenge
					//send them a code on their wii and tell them to send it to @Excite#8562 to confirm registration
			    
			return new Mail(wiiCode);
		}
		
		return null;
	}
	
	
	
}

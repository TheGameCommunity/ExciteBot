package com.gamebuster19901.excite.bot.mail;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;
import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.bot.user.Wii.InvalidWii;
import com.gamebuster19901.excite.game.challenge.InvalidChallenge;
import com.gamebuster19901.excite.game.challenge.Rewardable;
import com.gamebuster19901.excite.util.file.File;

import static com.gamebuster19901.excite.bot.database.Table.*;
import static com.gamebuster19901.excite.bot.database.Column.*;

public class Mailbox {
	private static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile("Content-Type:.*");
	static final Logger LOGGER = Logger.getLogger(Mailbox.class.getName());
	public static final String SERVER = "rc24.xyz";
	public static final String APP_ID_HEADER = "X-Wii-AppId";
	public static final String APP_ID = "1-52583345-0001";
	public static final String BOUNDARY = "t9Sf4yfjf1RtvDu3AA";
	
	public static void receive() throws IOException, MessagingException {
		File secretFile = new File("./mail.secret");
		String wiiID;
		String password;
		HttpGet request;
		BufferedReader fileReader = null;
		InputStreamReader mailReader = null;
		CloseableHttpClient client = null;
		try {
			fileReader = new BufferedReader(new FileReader(secretFile));
			wiiID = fileReader.readLine();
			password = fileReader.readLine();
			client = HttpClients.createDefault();
			request = new HttpGet("https://mtw." + SERVER + "/cgi-bin/receive.cgi?mlid=" + wiiID + "&passwd=" + password + "&maxsize=11534336");
			
			CloseableHttpResponse response = client.execute(request);
			LOGGER.log(Level.INFO, "Sent mail fetch request");
			HttpEntity entity = response.getEntity();
			if(entity != null) {
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
			try {
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
			catch(Throwable t) {
				LOGGER.log(Level.SEVERE, "An exception occurred when closing readers", t);
			}
			finally {
				if(client != null) {
					try {
						client.close();
					}
					catch(Throwable t) {
						LOGGER.log(Level.SEVERE, "An exception occurred when closing the HTTP connection to mail server", t);
					}
				}
			}
		}
	}
	
	private static void parseMail(String mailData) throws MessagingException, IOException {
		String delimiter = mailData.substring(0, mailData.indexOf('\r'));
		LOGGER.log(Level.INFO, "Delimiter is: " + delimiter);
		ArrayList<String> emails = new ArrayList<String>();
		emails.addAll(Arrays.asList(mailData.split(delimiter)));
		emails.removeIf((predicate) -> {return predicate.trim().isEmpty() || predicate.equals("--");});
		LinkedHashSet<MailResponse> responses = new LinkedHashSet<MailResponse>();
		for(String content : emails) {
			MimeMessage message = new MimeMessage(null, new ByteArrayInputStream(content.getBytes()));
			MimeMessage innerMessage1 = new MimeMessage(null, message.getInputStream());
			MimeMessage innerMessage2 = new MimeMessage(null, innerMessage1.getInputStream());
			System.out.println(innerMessage2.getContent().toString());
			
			for(Header header : Collections.list(innerMessage2.getAllHeaders())) {
				System.out.println(header.getName() + ": " + header.getValue());
			}
			LinkedHashSet<MailResponse> response = null;
			try {
				response = analyzeMail(Wii.getWii("1056185520598803"), innerMessage2);
				if(response != null) {
					responses.addAll(response);
				}
			}
			catch(Exception e) {
				LOGGER.log(Level.WARNING, "Couldn't analayze a mail item: \"" + content + "\"", e);
				continue;
			}
		}
		
		
	}
	
	private static LinkedHashSet<MailResponse> analyzeMail(Wii responder, MimeMessage prompt) throws MessagingException {
		LinkedHashSet<MailResponse> responses = new LinkedHashSet<MailResponse>();
		Session session = Session.getInstance(new Properties());
		
		Address[] from = prompt.getFrom();
		LOGGER.log(Level.INFO, "Analyzing mail from: " + (from != null ? from[0] : from));
		if(from == null) {
			responses.add(new NoResponse(prompt));
			return responses;
		}
		
		Wii sender = Wii.getWii(prompt.getFrom()[0].toString());
		if(sender instanceof InvalidWii) {
			LOGGER.log(Level.INFO, "Ignoring non-wii mail");
			responses.add(new NoResponse(prompt));
			return responses;
		}
		
		if(!sender.isKnown()) {
			try {
				Insertion.insertInto(WIIS).setColumns(WII_ID).to(sender.getWiiCode().toString()).prepare(ConsoleContext.INSTANCE).execute();
			} catch (SQLException e) {
				throw new MessagingException("Database error", e);
			}
		}
		
		String[] appheaders = prompt.getHeader(APP_ID_HEADER);
		String app = "";
		if(appheaders.length > 0) {
			app = appheaders[0];
		}
		Rewardable attachment = InvalidChallenge.INSTANCE;
		if(app.equals(APP_ID)) {
			attachment = analyzeIngameMail(prompt, sender);
		}
		
		if(sender.getOwner() instanceof UnknownDiscordUser) { //if wii is not registered
			MailResponse friendResponse = new AddFriendResponse(responder, sender, prompt);
			LOGGER.info("Sending friend request to " + sender);
			MailResponse codeResponse = new DiscordCodeResponse(responder, sender, prompt);
			LOGGER.info("Sending verification discord code to " + sender);
			
			responses.add(friendResponse);
			if(attachment.getReward() > 0) {
				responses.add(new RefundResponse(responder, prompt, attachment));
			}
			responses.add(codeResponse);
		}
		else { //excitebot is not currently accepting mail from anything other than Excitebots
			LOGGER.log(Level.INFO, "Excitebot is not currently accepting mail from anything other than Excitebots");
			responses.add(new NoResponse(prompt));
		}
		return responses;
	}
	
	/**
	 * We try to send all of the mail at once. If it fails, send responses individually to isolate which response is causing the issue.
	 * @param responses
	 */
	public static void sendResponses(LinkedHashSet<MailResponse> responses) {
		CloseableHttpClient client = HttpClients.createDefault();
		String wiiID;
		String password;
		HttpPost request;
		BufferedReader fileReader = null;
		try {
			File secretFile = new File("./mail.secret");
			fileReader = new BufferedReader(new FileReader(secretFile));
			wiiID = fileReader.readLine();
			password = fileReader.readLine();
			request = new HttpPost("https://mtw." + SERVER + "/cgi-bin/send.cgi?mlid=" + wiiID + "&passwd=" + password + "&maxsize=11534336");
			request.addHeader("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			
			String from = wiiID + "@rc24.xyz";
			
			String s = 					
				"Content-Disposition: form-data; name=\"mlid\"\r\n"
				+ "mlid="+wiiID+"\r\n"
				+ "passwd="+password+"\r\n"
				+ "--" + BOUNDARY + "\r\n";
			
			for(MailResponse response : responses) {
				if(response instanceof MailReplyResponse) {
					MailReplyResponse reply = (MailReplyResponse) response;
					reply.initVars();
					
					
					s = s + response.getResponse();
				}
			}
			
			s = s 
				+ "--\r\n";
		}
		catch(Throwable t) {
			t.printStackTrace();
			sendResponsesOneByOne(responses);
		}
	}
	
	public static void sendResponsesOneByOne(LinkedHashSet<MailResponse> responses) {
		
	}
	
	public static Rewardable analyzeIngameMail(MimeMessage message, Wii wii) {
		return InvalidChallenge.INSTANCE;
	}
	
}

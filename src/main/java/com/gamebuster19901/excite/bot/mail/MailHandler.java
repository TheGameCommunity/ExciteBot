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
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.user.Nobody;
import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.bot.user.Wii.InvalidWii;
import com.gamebuster19901.excite.game.challenge.InvalidChallenge;
import com.gamebuster19901.excite.game.challenge.Rewardable;
import com.gamebuster19901.excite.util.file.File;

import static com.gamebuster19901.excite.bot.database.Table.*;
import static com.gamebuster19901.excite.bot.database.Column.*;

public class MailHandler {
	private static final Pattern CONTENT_TYPE_PATTERN = Pattern.compile("Content-Type:.*");
	static final Logger LOGGER = Logger.getLogger(MailHandler.class.getName());
	public static final String SERVER = "rc24.xyz";
	public static final String APP_ID_HEADER = "X-Wii-AppId";
	public static final String APP_ID = "1-52583345-0001";
	
	public static void receive() throws IOException {
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
				parseMail(Wii.getWii(wiiID), content.toString());
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
	
	private static void parseMail(Wii responder, String mailData) {
		String delimiter = mailData.substring(0, mailData.indexOf('\r'));
		LOGGER.log(Level.INFO, "Delimiter is: " + delimiter);
		ArrayList<String> emails = new ArrayList<String>();
		emails.addAll(Arrays.asList(mailData.split(delimiter)));
		emails.removeIf((predicate) -> {return predicate.isEmpty() || predicate.equals("--\r\n");});
		for(String content : emails) {
			content = CONTENT_TYPE_PATTERN.matcher(content).replaceFirst("");
			content = content.trim();
			MailResponse response = null;
			try {
				response = analyzeMail(responder, content);
			}
			catch(Exception e) {
				LOGGER.log(Level.WARNING, "Couldn't analayze a mail item: \"" + content + "\"", e);
				continue;
			}
			try {
				response.respond();
			}
			catch(Throwable t) {
				try {
					MailResponse failureResponse = response.handleResponseFailure(t);
					failureResponse.respond();
				} catch (Throwable e) {
					LOGGER.log(Level.SEVERE, "Could not respond to mail.", t);
				}
			}
		}
	}

	private static MailResponse analyzeMail(Wii responder, String content) throws MessagingException {
		Session session = Session.getInstance(new Properties());
		InputStream data = new ByteArrayInputStream(content.getBytes());
		MimeMessage message = new MimeMessage(session, data);
		
		Address[] from = message.getFrom();
		LOGGER.log(Level.INFO, "Analyzing mail from: " + from);
		if(from == null) {
			return new NoResponse(message);
		}
		
		Wii sender = Wii.getWii(message.getFrom()[0].toString());
		if(sender instanceof InvalidWii) {
			LOGGER.log(Level.INFO, "Ignoring non-wii mail");
			return new NoResponse(message);
		}
		
		if(!sender.isKnown()) {
			try {
				Insertion.insertInto(WIIS).setColumns(WII_ID).to(sender.getWiiCode().toString()).prepare(ConsoleContext.INSTANCE).execute();
			} catch (SQLException e) {
				throw new MessagingException("Database error", e);
			}
		}
		
		String[] appheaders = message.getHeader(APP_ID_HEADER);
		String app = "";
		if(appheaders.length > 0) {
			app = appheaders[0];
		}
		Rewardable attachment = InvalidChallenge.INSTANCE;
		if(app.equals(APP_ID)) {
			attachment = analyzeIngameMail(message, sender);
		}
		
		if(sender.getOwner() instanceof Nobody) { //if wii is not registered
			MailResponse response = new DiscordCodeResponse(responder, sender, message);
			
			if(attachment.getReward() > 0) {
				MultiResponse multiResponse = new MultiResponse(message);
				multiResponse.addResponse(new RefundResponse(responder, message, attachment));
				multiResponse.addResponse(response);
			}
			return response;
		}
		else { //excitebot is not currently accepting mail from anything other than Excitebots
			LOGGER.log(Level.INFO, "Excitebots is not currently accepting mail from anything other than Excitebots");
			return new NoResponse(message);
		}
	}
	
}

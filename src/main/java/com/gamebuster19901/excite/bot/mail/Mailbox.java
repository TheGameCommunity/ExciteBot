package com.gamebuster19901.excite.bot.mail;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.gamebuster19901.excite.bot.audit.MailAudit;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.user.UnknownDiscordUser;
import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.bot.user.Wii.InvalidWii;
import com.gamebuster19901.excite.game.challenge.InvalidChallenge;
import com.gamebuster19901.excite.game.challenge.Rewardable;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.gamebuster19901.excite.util.TimeUtils;

import java.io.File;

import static com.gamebuster19901.excite.bot.database.Table.*;
import static com.gamebuster19901.excite.bot.database.Column.*;

public class Mailbox {
	static final Logger LOGGER = Logger.getLogger(Mailbox.class.getName());
	public static final String SERVER = "rc24.xyz";
	public static final String APP_ID_HEADER = "X-Wii-AppId";
	public static final String EXCITEBOTS = "1-52583345-0001";
	public static final String FRIEND_REQUEST = "0-00000001-0001";
	public static final String WII_MESSAGE = "2-48414541-0001";
	public static final String BOUNDARY = "t9Sf4yfjf1RtvDu3AA";
	public static final Base64.Decoder DECODER = Base64.getDecoder();
	
	public static final File MAILBOX;
	public static final File INBOX;
	public static final File INBOX_ERRORED;
	public static final File OUTBOX;
	public static final File OUTBOX_ERRORED;
	public static final EmailAddress ADDRESS;
	static {
		LOGGER.setLevel(Main.LOG_LEVEL);
		MAILBOX = new File("./run/Mailbox");
		INBOX = new File("./run/Mailbox/Inbox");
		INBOX_ERRORED = new File("./run/Mailbox/Inbox/Errored");
		OUTBOX = new File("./run/Mailbox/Outbox");
		OUTBOX_ERRORED = new File("./run/Mailbox/Outbox/Errored");
		
		if(!MAILBOX.exists()) {
			MAILBOX.mkdirs();
		}
		if(!INBOX.exists()) {
			INBOX.mkdirs();
		}
		if(!INBOX_ERRORED.exists()) {
			INBOX_ERRORED.mkdirs();
		}
		if(!OUTBOX.exists()) {
			OUTBOX.mkdirs();
		}
		if(!OUTBOX_ERRORED.exists()) {
			OUTBOX_ERRORED.mkdirs();
		}
		
		if(!(MAILBOX.exists() && INBOX.exists() && INBOX_ERRORED.exists() && OUTBOX.exists() && OUTBOX_ERRORED.exists())) {
			throw new IOError(new FileNotFoundException("Could not create necessary mailbox files"));
		}
		File secretFile = new File("./mail.secret");
		String wiiID;
		BufferedReader fileReader = null;
		try {
			fileReader = new BufferedReader(new FileReader(secretFile));
			ADDRESS = Wii.getWii(fileReader.readLine());
		}
		catch(Throwable t) {
			throw new Error(t);
		}
		finally {
			if(fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					throw new Error(e);
				}
			}
		}
	}
	
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
			StatusLine line = response.getStatusLine();
			if(line != null) {
				int statusCode = line.getStatusCode();
				if(statusCode >= 300) {
					LOGGER.warning("Unexpected status code " + statusCode + ". Skipping mail retrieval.");
					return;
				}
			}
			else {
				LOGGER.severe("Did not receive a status response?! Skipping mail retrieval.");
				return;
			}
			LOGGER.log(Level.FINEST, "Sent mail fetch request");
			HttpEntity entity = response.getEntity();
			if(entity != null) {
				InputStream contentStream = entity.getContent();
				mailReader = new InputStreamReader(contentStream);
				char[] data = new char[1];
				StringBuilder content = new StringBuilder();
				while(mailReader.read(data) != -1) {
					content.append(data);
				}
				LOGGER.log(Level.FINEST, content.toString());
				parseMail(content.toString());
			}
			else {
				LOGGER.log(Level.FINEST, "Response was null");
			}
		}
		catch (SocketException e) {
			LOGGER.warning("Socket Exception... Skipping mail retrieval");
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
		String delimiter;
		try {
			delimiter = mailData.substring(0, mailData.indexOf('\r'));
		}
		catch(Throwable t) {
			FileWriter f = new FileWriter(new File("./badMail.email"));
			f.write(mailData);
			f.close();
			LOGGER.severe(mailData);
			throw t;
		}
		LOGGER.log(Level.FINEST, "Delimiter is: " + delimiter);
		ArrayList<String> emails = new ArrayList<String>();
		emails.addAll(Arrays.asList(mailData.split(delimiter)));
		emails.removeIf((predicate) -> {return predicate.trim().isEmpty() || predicate.equals("--");});
		LinkedHashSet<MailResponse> responses = new LinkedHashSet<MailResponse>();
		int i = 1;
		for(String content : emails) {
			FileOutputStream writer = null;
			MimeMessage message = new MimeMessage(null, new ByteArrayInputStream(content.getBytes()));
			MimeMessage innerMessage1 = new MimeMessage(null, message.getInputStream());
			MimeMessage innerMessage2 = new MimeMessage(null, innerMessage1.getInputStream());
			ByteArrayOutputStream stringStream = new ByteArrayOutputStream();
			innerMessage2.writeTo(stringStream);
			String email = new String(stringStream.toByteArray()).trim();
			if(email.isEmpty() || email.contains("This part is ignored.") && email.contains("cd=100")) {
				continue;
			}
			
			/*
				for(javax.mail.Header header : Collections.list(innerMessage2.getAllHeaders())) {
					System.out.println(header.getName() + ": " + header.getValue());
				}
			*/
			
			LinkedHashSet<MailResponse> response = null;
			try {
				response = analyzeMail(Wii.getWii("1056185520598803"), innerMessage2);
				if(response != null) {
					responses.addAll(response);
				}
				Address from = innerMessage2.getFrom() != null ? innerMessage2.getFrom()[0] : null;
				
				File file = new File(INBOX.getAbsolutePath() + "/" + from + "/" + TimeUtils.getDBDate(Instant.now()) + "(" + i++ + ")" + ".email");
				file.getParentFile().mkdirs();
				writer = new FileOutputStream(file);
				innerMessage2.writeTo(writer);
				MailAudit.addMailAudit(ConsoleContext.INSTANCE, innerMessage2, true, file);
			}
			catch(Exception e) {
				LOGGER.log(Level.WARNING, "Couldn't analayze a mail item: \"" + content + "\"", e);
				continue;
			}
			finally {
				if(writer != null) {
					writer.close();
				}
			}
		}
		sendResponses(responses);
		
	}
	
	private static LinkedHashSet<MailResponse> analyzeMail(Wii responder, MimeMessage prompt) throws MessagingException {
		LinkedHashSet<MailResponse> responses = new LinkedHashSet<MailResponse>();
		Session session = Session.getInstance(new Properties());
		
		Address[] from = prompt.getFrom();
		LOGGER.log(Level.FINEST, "Analyzing mail from: " + (from != null ? from[0] : from));
		if(from == null) {
			LOGGER.warning("Mail had no sender.");
			responses.add(new NoResponse(prompt));
			return responses;
		}
		
		EmailAddress senderEmail = (EmailAddress)(Object)prompt.getFrom()[0];
		Wii sender = Wii.getWii(senderEmail.toString());
		if(sender instanceof InvalidWii) {
			LOGGER.log(Level.WARNING, "Ignoring non-wii mail");
			responses.add(new NoResponse(prompt));
			return responses;
		}
		else {
			boolean wasKnown;
			if(!(wasKnown = sender.isKnown())) {
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
			if(app.equals(EXCITEBOTS)) {
				attachment = analyzeIngameMail(prompt, sender);
			}
			
			if(sender.getOwner() instanceof UnknownDiscordUser) { //if wii is not registered
				if(app.equals(FRIEND_REQUEST) && !wasKnown) {
					MailResponse friendResponse = new AddFriendResponse(responder, sender, prompt);
					LOGGER.finest("Sending friend request to " + sender.getEmail());
					MailResponse codeResponse = new DiscordCodeResponse(responder, sender, prompt);
					LOGGER.finest("Sending verification discord code to " + sender.getEmail());
					
					responses.add(friendResponse);
					responses.add(codeResponse);
				}

				if(attachment.getReward() > 0) {
					//responses.add(new RefundResponse(responder, prompt, attachment));
				}
				
			}
			else { //excitebot is not currently accepting mail from anything other than Excitebots
				LOGGER.log(Level.WARNING, "Excitebot is not currently accepting mail from anything other than Excitebots");
				responses.add(new NoResponse(prompt));
			}
		}
		
		if(responses.isEmpty()) {
			LOGGER.warning("No responses added?! Manually adding NoResponse.");
			responses.add(new NoResponse(prompt));
		}
		
		return responses;
	}
	
	/**
	 * We try to send all of the mail at once.
	 * @param responses
	 */
	public static void sendResponses(LinkedHashSet<MailResponse> responses) {
		CloseableHttpClient client = HttpClients.createDefault();
		String wiiID;
		String password;
		HttpPost request;
		BufferedReader fileReader = null;
		String s = "String uninitialized";
		try {
			File secretFile = new File("./mail.secret");
			fileReader = new BufferedReader(new FileReader(secretFile));
			wiiID = fileReader.readLine();
			password = fileReader.readLine();
			request = new HttpPost("https://mtw." + SERVER + "/cgi-bin/send.cgi?mlid=" + wiiID + "&passwd=" + password + "&maxsize=11534336");
			request.addHeader("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			s = 					
				"Content-Disposition: form-data; name=\"mlid\"\n"
				+ "mlid="+wiiID+"\n"
				+ "passwd="+password+"\n"
				+ "--" + BOUNDARY + "\n";
			
			int i = 1;
			for(MailResponse response : responses) {
				FileOutputStream writer;
				LOGGER.finest(response.getClass().getName());
				if(response instanceof MailReplyResponse) {
					MailReplyResponse reply = (MailReplyResponse) response;
					reply.initVars();
					File file = new File(OUTBOX.getAbsolutePath() + "/" + reply.getResponder().getEmail() + "/" + reply.getRespondee().getEmail() + "/" + TimeUtils.getDBDate(Instant.now()) + "(" + i +")" + ".email");
					file.getParentFile().mkdirs();
					writer = new FileOutputStream(file);
					writer.write(reply.getResponse().getBytes());
					MailAudit.addMailAudit(ConsoleContext.INSTANCE, (MailReplyResponse)response, false, file);
					

					reply.setVar("mailNumber", "m" + i++);
					
					
					s = s + response.getResponse() + "\n";
				}
			}
			
			if(i == 1) {
				//System.out.println("No responses.");
				return;
			}
			
			s = s.trim() 
				+ "--";
			
			StringEntity e = new StringEntity(s);
			request.setEntity(e);
			
			/* debug code
				org.apache.http.Header[] headers = request.getAllHeaders();
				for(org.apache.http.Header header : headers) {
					System.err.println(header.getName() + ": " + header.getValue());
				}
				System.err.println(s);
			*/
			
			CloseableHttpResponse response = client.execute(request);
			if(response != null) {
				ByteArrayOutputStream logStream = new ByteArrayOutputStream();
				response.getEntity().writeTo(logStream);
				LOGGER.log(Level.FINEST, logStream.toString());
				logStream.close();
			}
		}
		catch(Throwable t) {
			LOGGER.log(Level.FINEST, "Failed to send emails", t);
			File errored = new File(OUTBOX_ERRORED.getAbsolutePath() + "/" + TimeUtils.getDBDate(Instant.now()) + " " + t.getClass().getSimpleName() + ".email");
			try {
				errored.getParentFile().mkdirs();
				FileWriter writer = new FileWriter(errored);
				writer.write(s + "\n\n====STACKTRACE====\n\n" + StacktraceUtil.getStackTrace(t));
				writer.close();
			} catch (IOException e) {
				throw new IOError(e);
			}
		}
		finally {
			if(fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					throw new IOError(e);
				}
			}
		}
	}
	
	public static LinkedHashSet<MailResponse> packResponses(MailResponse...mailResponses) {
		LinkedHashSet<MailResponse> responses = new LinkedHashSet<MailResponse>();
		responses.addAll(Arrays.asList(mailResponses));
		return responses;
	}
	
	public static Rewardable analyzeIngameMail(MimeMessage message, Wii wii) {
		return InvalidChallenge.INSTANCE;
	}
	
}

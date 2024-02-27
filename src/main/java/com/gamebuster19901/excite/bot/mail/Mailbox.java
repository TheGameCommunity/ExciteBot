package com.gamebuster19901.excite.bot.mail;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.QueueInputStream;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;

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
import java.sql.SQLException;
import java.time.Instant;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.gamebuster19901.excite.bot.audit.MailAudit;
import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.bot.user.Wii.InvalidWii;
import com.gamebuster19901.excite.util.StacktraceUtil;
import com.gamebuster19901.excite.util.TimeUtils;
import com.thegamecommunity.excite.modding.game.mail.ExciteMail;
import com.thegamecommunity.excite.modding.game.mail.Mail;
import com.thegamecommunity.excite.modding.game.mail.WC24MultiMessage;
import com.thegamecommunity.excite.modding.game.mail.WiiMail;

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
	public static final ElectronicAddress ADDRESS;
	public static final Session SESSION = Session.getInstance(new Properties());
	static {
		LOGGER.setLevel(Level.FINEST);
		//ConsoleHandler consoleHandler = new ConsoleHandler();
		//consoleHandler.setLevel(Level.FINEST);
		//LOGGER.addHandler(consoleHandler);
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
				byte[] data = entity.getContent().readAllBytes();
				InputStream contentStream = new ByteArrayInputStream(data);
				try {
					WC24MultiMessage mail = (WC24MultiMessage) Mail.getMail(contentStream);
					parseMail(mail);
				}
				catch(Exception e) {
					File file = new File("./badmail");
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(data);
					fos.close();
					throw new MessagingException("\"Could not parse incoming mail.", e);
				}
			}
			else {
				LOGGER.log(Level.FINEST, "Response was null");
			}
		}
		catch (IOException e) {
			LOGGER.warning("IOException... Skipping mail retrieval");
			e.printStackTrace();
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
	
	private static void parseMail(WC24MultiMessage mailData) throws MessagingException, IOException {
		LinkedHashSet<MailResponse> responses = new LinkedHashSet<MailResponse>();
		for(int i = 0; i < mailData.getMessages().size(); i++) {
			try {
				LinkedHashSet<MailResponse> response;
				Mail mail = mailData.getMessages().get(i);
				Message message = mail.getAsMessage();
				Address from = message.getFrom() != null ? message.getFrom()[0] : null;
				if(from == null) {
					throw new MessagingException("Refusing to process message without a sender.");
				}
				File file = new File(INBOX.getAbsolutePath() + "/" + from + "/" + TimeUtils.getDBDate(Instant.now()) + "(" + i + ")" + ".email");
				
				try (FileOutputStream writer = new FileOutputStream(file)) {
					message.writeTo(writer);
				}
				MailAudit.addMailAudit(ConsoleContext.INSTANCE, message, true, file);
				
				response = analyzeMail(Wii.getWii("1056185520598803"), mail);
				if(response != null) {
					responses.addAll(response);
				}
				
			}
			catch(Exception e) {
				LOGGER.log(Level.WARNING, "Couldn't analayze mail #" + (i + 1) , e);
			}


		}
		sendResponses(responses);
		
	}
	
	private static LinkedHashSet<MailResponse> analyzeMail(Wii responder, Mail prompt) throws MessagingException {
		try {
			LinkedHashSet<MailResponse> responses = new LinkedHashSet<MailResponse>();
			Message promptMessage = prompt.getAsMessage();
			
			Address[] from = promptMessage.getFrom();
			LOGGER.log(Level.FINEST, "Analyzing mail from: " + (from != null ? from[0] : from));
			if(from == null) {
				LOGGER.log(Level.INFO, "Ignoring mail received without a 'from' header.");
				responses.add(new NoResponse(promptMessage));
				return responses;
			}
			
			ElectronicAddress senderEmail = new EmailAddress(promptMessage.getFrom()[0]);
			
			if(prompt instanceof WiiMail) {
				Wii sender = Wii.getWii(senderEmail);
				if(sender instanceof InvalidWii) {
					LOGGER.warning("Got a WiiMail from a non-wii email address??");
					LOGGER.warning("Ignoring wii mail from non-wii email address! " + sender);
					responses.add(new NoResponse(promptMessage));
					return responses;
				}
				
				boolean wasKnown;
				if(!(wasKnown = sender.isKnown())) {
					try {
						Insertion.insertInto(WIIS).setColumns(WII_ID).to(sender.getWiiCode().toString()).prepare(ConsoleContext.INSTANCE).execute();
					} catch (SQLException e) {
						throw new MessagingException("Database error", e);
					}
				}
				if(prompt instanceof ExciteMail) {
					//TODO
					return responses;
				}
				else if (promptMessage.getHeader(APP_ID_HEADER)[0].equals(FRIEND_REQUEST)) {
					MailResponse friendResponse = new AddFriendResponse(responder, sender, promptMessage);
					LOGGER.finest("Sending friend request to " + sender.getEmail());
					MailResponse codeResponse = new DiscordCodeResponse(responder, sender, promptMessage);
					LOGGER.finest("Sending verification discord code to " + sender.getEmail());
					responses.add(friendResponse);
					responses.add(codeResponse);
					return responses;
				}
				else {
					System.out.println("Ignoring mail from non-excitebots game" + promptMessage.getHeader(APP_ID_HEADER));
					LOGGER.finest("Ignoring mail from non-excitebots game " + promptMessage.getHeader(APP_ID_HEADER)[0]);
					responses.add(new NoResponse(promptMessage));
					return responses;
				}
			}
			else {
				LOGGER.log(Level.FINEST, "Ignoring non-wii mail from " + from[0]);
				responses.add(new NoResponse(promptMessage));
				return responses;
			}
		}
		catch(Exception e) {
			throw new MessagingException("Unable to analyze mail.", e);
		}

	}
	
	/**
	 * We try to send all of the mail at once.
	 * @param responses
	 */
	public static void sendResponses(Set<MailResponse> responses) {
		CloseableHttpClient client = HttpClients.createDefault();
		String wiiID;
		String password;
		BufferedReader fileReader = null;
		String s = "UNINITIALIZED PAYLOAD";
		try {
			File secretFile = new File("./mail.secret");
			fileReader = new BufferedReader(new FileReader(secretFile));
			wiiID = fileReader.readLine();
			password = fileReader.readLine();
			String auth = "mlid=" + wiiID + "\r\npasswd=" + password;
			
			HttpPost post = new HttpPost("https://mtw." + SERVER + "/cgi-bin/send.cgi?");
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addTextBody("mlid", auth);
			
			int i = 1;
			Iterator<MailResponse> iterator = responses.iterator();
			while(iterator.hasNext()) {
				try {
					MailResponse m = iterator.next();
					if(m instanceof MailReplyResponse) {
						MailReplyResponse mail = (MailReplyResponse) m;
						File file = new File(OUTBOX.getAbsolutePath() + "/" + mail.getResponder().getEmail() + "/" + mail.getRespondee().getEmail() + "/" + TimeUtils.getDBDate(Instant.now()) + "(" + i + ")" + ".email");
						try (FileOutputStream fos = new FileOutputStream(file)) {
							mail.writeTo(new FileOutputStream(file));
							MailAudit.addMailAudit(ConsoleContext.INSTANCE, mail.getResponse(), false, file);
						}
					}
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					m.writeTo(baos);
					QueueInputStream in = IOUtils.copy(baos);
					
					InputStreamBody body = new InputStreamBody(in, ContentType.MULTIPART_FORM_DATA, "m" + i);
					FormBodyPartBuilder b = FormBodyPartBuilder.create("m" + i, body);
					builder.addPart(b.build());
					in.close();
				}
				catch(Throwable t) {
					t.printStackTrace();
				}
				finally {
					i++;
				}
			}
			
			post.setEntity(builder.build());
			File f = new File("./mailOut2");
			FileOutputStream fos = new FileOutputStream(f);
			post.getEntity().writeTo(fos);
			fos.close();

			//System.out.println(client.execute(post).getStatusLine());
			//Commands.DISPATCHER.getDispatcher().execute("stop", ConsoleContext.INSTANCE);
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
	
}

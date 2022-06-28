package com.gamebuster19901.excite.bot.mail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.security.auth.login.LoginException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;

import com.gamebuster19901.excite.bot.command.ConsoleContext;
import com.gamebuster19901.excite.bot.database.Insertion;
import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.bot.user.Wii.InvalidWii;
import com.gamebuster19901.excite.game.challenge.InvalidChallenge;
import com.gamebuster19901.excite.game.challenge.Rewardable;
import com.gamebuster19901.excite.util.TimeUtils;

import static com.gamebuster19901.excite.bot.database.Table.*;
import static com.gamebuster19901.excite.bot.database.Column.*;

import java.io.File;

public class Mailbox {
	static final Logger LOGGER = Logger.getLogger(Mailbox.class.getName());
	static final Pattern CONTENT_PATTERN  = Pattern.compile("Content-Type: .*");
	static final Pattern SEPARATOR_PATTERN = Pattern.compile("--(.*)");
	public static final String SERVER = "rc24.xyz";
	public static final String APP_ID_HEADER = "X-Wii-AppId";
	public static final String BOUNDARY = "t9Sf4yfjf1RtvDu3AA";
	public static final Base64.Decoder DECODER = Base64.getDecoder();
	public static final File MAILBOX;
	public static final File INBOX;
	public static final File INBOX_ERRORED;
	public static final File OUTBOX;
	public static final File OUTBOX_ERRORED;
	public static final Wii ADDRESS;
	static {
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
	
	public static void receive() throws IOException, MessagingException, LoginException {
		HttpClient client = HttpClients.createDefault();
		String[] credentials = credentials();
		String auth = "mlid=" + credentials[0] + "&passwd=" + credentials[1] + "&maxsize=11534336";
		HttpPost request = new HttpPost("https://mtw.rc24.xyz/cgi-bin/receive.cgi?" + auth);
		credentials[0] = null; credentials[1] = null; credentials = null; auth = null;
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		request.setEntity(builder.build());
		
		HttpResponse response = client.execute(request);
		HttpEntity e = response.getEntity();
		
		try (InputStream s = e.getContent()) {
			parseMail(new String(s.readAllBytes(), StandardCharsets.UTF_8));
		}

	}
	
	private static void parseMail(String mailData) throws MessagingException, IOException, LoginException {
		
		/*mailData = "--202206250551/5787513\n"
				+ "Content-Type: text/plain\n"
				+ "\n"
				+ "This part is ignored.\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "cd=100\n"
				+ "msg=Success.\n"
				+ "mailnum=1\n"
				+ "mailsize=291\n"
				+ "allnum=1\n"
				+ "\n"
				+ "--202206250551/5787513\n"
				+ "Content-Type: text/plain\n"
				+ "\n"
				+ "From: gamebuster1990@gmail.com\n"
				+ "Subject: t\n"
				+ "To: w1056185520598803@rc24.xyz\n"
				+ "MIME-Version: 1.0\n"
				+ "Content-Type: MULTIPART/mixed; BOUNDARY=\"202206250551/8681661\"\n"
				+ "\n"
				+ "--202206250551/8681661\n"
				+ "Content-Type: TEXT/plain; CHARSET=utf-8\n"
				+ "Content-Description: wiimail\n"
				+ "\n"
				+ "t\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "--202206250551/8681661--\n"
				+ "--202206250551/5787513--";*/
		/*
		mailData = "Content-Type: text/plain\n"
				+ "\n"
				+ "Date: 28 Jun 2022 04:43:46 -0000\n"
				+ "From: w0633303276188884@rc24.xyz\n"
				+ "To: w1056185520598803@rc24.xyz\n"
				+ "Message-Id: <0015400023FFC68CFF4D403D7045B@rc24.xyz>\n"
				+ "X-Wii-AppId: 2-48414541-0001\n"
				+ "X-Wii-Cmd: 00044001\n"
				+ "MIME-Version: 1.0\n"
				+ "Content-Type: text/plain; charset=utf-16be\n"
				+ "Content-Transfer-Encoding: base64\n"
				+ "\n"
				+ "AGYAeQB5AGQ=";
		*/
		String delimiter = mailData.split("\\R", 2)[0];
		System.out.println("Delimiter is " + delimiter);
		List<MailResponse> responses = new ArrayList<MailResponse>();
		
		System.out.println(mailData);
		
		String[] mails = mailData.split(delimiter);
		
		int i = 0;
		for(String s : mails) {
			if(s.isBlank() || s.replace("-", "").isBlank() || i == 1) {
				System.out.println("skipping " + i++);
				continue;
			}

			s = CONTENT_PATTERN.matcher(s).replaceFirst(""); //remove content-type
			s = s.trim();
			System.err.println("-----" + i++);
			System.err.println(s);
			MimeMessage message = new MimeMessage(Session.getInstance(System.getProperties()), new ByteArrayInputStream(s.getBytes()));
			System.out.println(message.getContent().getClass().getCanonicalName());
			responses.addAll(getResponse(message));
		}
		
		if(responses.size() > 0) {
			System.out.println(sendResponses(responses));
		}
	}
	
	private static List<MailResponse> getResponse(MimeMessage message) throws MessagingException, IOException {
		ArrayList<MailResponse> ret = new ArrayList<MailResponse>();
		Address from = message.getFrom() != null ? message.getFrom()[0] : null;
		if(from == null) {
			ret.add(new NoResponse());
			return ret;
		}
		ElectronicAddress sender = new EmailAddress(from);
		Wii wii = Wii.getWii(sender);
		if(wii instanceof InvalidWii) {
			TextualMailResponse<ElectronicAddress> response = new TextualMailResponse<ElectronicAddress>(message);
			response.setText("Excitebot is not currently accepting mail from standard email addresses.");
			ret.add(response);
			return ret;
		}
		else {
			boolean wasKnown;
			if(!(wasKnown = wii.isKnown())) {
				try {
					Insertion.insertInto(WIIS).setColumns(WII_ID).to(wii.getWiiCode().toString()).prepare(ConsoleContext.INSTANCE).execute();
				}
				catch(SQLException e) {
					throw new MessagingException("Database Error", e);
				}
			}
				
			String[] appheaders = message.getHeader(APP_ID_HEADER);
			Applications app = null;
			if(appheaders.length > 0) {
				app = Applications.getApplicaiton(appheaders[0]);
			}
			
			switch(app) {
				case EXCITEBOTS:
					//attachment = analyzeIngameMail(sender);
					break;
				case FRIEND_REQUEST:
					AddFriendResponse friendResponse = new AddFriendResponse(wii);
					DiscordCodeResponse codeResponse = new DiscordCodeResponse(wii);
					ret.add(friendResponse);
					ret.add(codeResponse);
					break;
				case WII_MESSAGE:
					if(wii.equals(ADDRESS)) { //just in case
						ret.add(new NoResponse());
					}
					else{
						TextualMailResponse<Wii> response = new TextualMailResponse<Wii>(message);
						response.setText("You sent:\n\n" + message.getContent());
						ret.add(response);
					}
					break;
				default:
					TextualMailResponse<Wii> response = new TextualMailResponse<Wii>(message);
					response.setText("Received mail for unknown game " + appheaders[0]);
					break;
			}
			return ret;
		}
	}
	
	/**
	 * We try to send all of the mail at once.
	 * 
	 * @param responses
	 * @throws LoginException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws MessagingException 
	 */
	public static String sendResponses(List<MailResponse> responses) throws LoginException, ClientProtocolException, IOException, MessagingException {
		HttpClient client = HttpClients.createDefault();
		HttpPost request = new HttpPost("https://mtw.rc24.xyz/cgi-bin/send.cgi");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		
		String[] credentials = credentials();
		builder.addTextBody("mlid", "mlid=" + credentials[0] + "\r\npasswd=" + credentials[1]);
		credentials[0] = null; credentials[1] = null; credentials = null;
		FileOutputStream writer;
		
		int i = 1;
		for(MailResponse response : responses) {
			if(response instanceof MailReplyResponse) {

				MailReplyResponse reply = (MailReplyResponse) response;
				reply.setVar("mailNumber", "m" + ++i);

				reply.initVars();
				String text = response.getResponse();
				System.out.println(response.getResponse());
				builder.addTextBody("m" + i,  text);
			}
		}
		
		HttpEntity entity = builder.build();
		request.setEntity(entity);
		File file = new File(OUTBOX.getAbsolutePath() + "/" + ADDRESS.getEmail() + "/" + TimeUtils.getDBDate(Instant.now()) + "(" + i +")" + ".email");
		file.getParentFile().mkdirs();
		writer = new FileOutputStream(file);
		writer.write(entity.getContent().readAllBytes());
		writer.close();
		//MailAudit.addMailAudit(ConsoleContext.INSTANCE, (MailReplyResponse)response, false, file);
		
		HttpResponse response = client.execute(request);
		HttpEntity status = response.getEntity();
		try (InputStream s = status.getContent()) {
			return new String(s.readAllBytes(), StandardCharsets.UTF_8);
		}
		
	}
	
	public static List<MailResponse> packResponses(MailResponse...mailResponses ) {
		List<MailResponse> ret = new ArrayList<>();
		ret.addAll(Arrays.asList(mailResponses));
		return ret;
	}
	
	public static Rewardable analyzeIngameMail(MimeMessage message, Wii wii) {
		return InvalidChallenge.INSTANCE;
	}
	
	private static final String[] credentials() throws LoginException {
		try {
			final File secretFile = new File("./mail.secret");
			final BufferedReader fileReader = new BufferedReader(new FileReader(secretFile));
			final String[] ret = new String[] {fileReader.readLine(), fileReader.readLine()};
			fileReader.close();
			return ret;
		}
		catch(Exception e) {
			final LoginException loginException = new LoginException();
			loginException.initCause(e);
			throw loginException;
		}
	}
	
}

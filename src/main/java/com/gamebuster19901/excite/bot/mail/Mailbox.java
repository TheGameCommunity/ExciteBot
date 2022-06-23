package com.gamebuster19901.excite.bot.mail;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.security.auth.login.LoginException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;

import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.game.challenge.InvalidChallenge;
import com.gamebuster19901.excite.game.challenge.Rewardable;
import java.io.File;

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
		HttpPost request = new HttpPost("https://mtw.rc24.xyz/cgi-bin/receive.cgi");
		
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		String[] credentials = credentials();
		builder.addTextBody("mlid", credentials[0]);
		builder.addTextBody("passwd", credentials[1]);
		builder.addTextBody("maxsize", "11534336");
		credentials[0] = null; credentials[1] = null; credentials = null;
		
		HttpEntity e = builder.build();
		request.setEntity(e);
		
		try (InputStream s = e.getContent()) {
			parseMail(new String(s.readAllBytes(), StandardCharsets.UTF_8));
		}

	}
	
	private static void parseMail(String mailData) throws MessagingException, IOException, LoginException {
		LinkedHashSet<MailResponse> responses = new LinkedHashSet<MailResponse>();

		
		sendResponses(responses);
	}
	
	/**
	 * We try to send all of the mail at once.
	 * 
	 * @param responses
	 * @throws LoginException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String sendResponses(LinkedHashSet<MailResponse> responses) throws LoginException, ClientProtocolException, IOException {
		HttpClient client = HttpClients.createDefault();
		HttpPost request = new HttpPost("https://mtw.rc24.xyz/cgi-bin/send.cgi");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		
		String[] credentials = credentials();
		builder.addTextBody("mlid", credentials[0]);
		builder.addTextBody("passwd", credentials[1]);
		credentials[0] = null; credentials[1] = null; credentials = null;
		
		int i = 1;
		for(MailResponse response : responses) {
			String text = response.getResponse();
			builder.addTextBody("m" + i++,  text);
		}
		
		HttpEntity entity = builder.build();
		request.setEntity(entity);
		
		HttpResponse response = client.execute(request);
		try (InputStream s = entity.getContent()) {
			return new String(s.readAllBytes(), StandardCharsets.UTF_8);
		}
		
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

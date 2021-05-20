package com.gamebuster19901.excite.bot.mail;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Level;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.util.TimeUtils;
import com.gamebuster19901.excite.util.file.File;

public abstract class MailReplyResponse extends MailResponse {
	
	public static final String US_ASCII = "us-ascii";
	public static final String UTF16BE = "utf-16be";
	
	protected Wii responder;
	protected Wii respondee;
	protected FormBodyPartBuilder replyBuilder;
	protected FormBodyPart reply;
	
	public MailReplyResponse(Wii responder, Wii respondee, MimeMessage message) {
		super(message);
		this.responder = responder;
		this.respondee = respondee;
	}
	
	public MailReplyResponse(Wii responder, MimeMessage message) throws MessagingException {
		this(responder, Wii.getWii(message.getFrom()[0].toString()), message);
	}
	
	@Override
	public void respond() throws MessagingException {
		try {
			setReply(getResponseTemplate(responder, false).build());
			send();
		} catch (IOException e) {
			throw new MessagingException("An exception occured when sending the mail", e);
		}
	}

	protected final void send() throws MessagingException, IOException {
		if(reply == null) {
			throw new IllegalStateException("No reply set?!");
		}
		File secretFile = new File("./mail.secret");
		String wiiID;
		String password;
		HttpPost request;
		HttpPost dupe;
		BufferedReader fileReader = null;
		InputStreamReader mailReader = null;
		CloseableHttpClient client = null;
		try {
			client = HttpClients.createDefault();
			fileReader = new BufferedReader(new FileReader(secretFile));
			wiiID = fileReader.readLine();
			password = fileReader.readLine();
			client = HttpClients.createDefault();
			request = new HttpPost("https://mtw." + MailHandler.SERVER + "/cgi-bin/send.cgi?mlid=" + wiiID + "&passwd=" + password + "&maxsize=11534336");
			dupe = new HttpPost("https://mtw." + MailHandler.SERVER + "/cgi-bin/send.cgi?mlid=" + wiiID + "&passwd=" + password + "&maxsize=11534336");

			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addPart(reply);
			
			MultipartEntityBuilder dupePart = MultipartEntityBuilder.create();
			dupePart.addPart(getResponseTemplate(responder, false).build());
			
			request.setEntity(builder.build());
			dupe.setEntity(dupePart.build());
			CloseableHttpResponse response = client.execute(request);
			HttpEntity responseEntity = response.getEntity();
			if(responseEntity != null) {
				InputStream stream = responseEntity.getContent();
				String rawResponse = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
				ByteArrayOutputStream logStream = new ByteArrayOutputStream();
				dupe.getEntity().writeTo(logStream);
				MailHandler.LOGGER.log(Level.INFO, new String(logStream.toByteArray()));
				MailHandler.LOGGER.log(Level.INFO, rawResponse);
				logStream.close();
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
				MailHandler.LOGGER.log(Level.SEVERE, "An exception occurred when closing POST readers", t);
			}
			finally {
				if(client != null) {
					try {
						client.close();
					}
					catch(Throwable t) {
						MailHandler.LOGGER.log(Level.SEVERE, "An exception occurred when closing the HTTP POST connection to mail server", t);
					}
				}
			}
		}
	}
	
	protected final void setReply(FormBodyPart reply) {
		this.reply = reply;
	}
	
	protected FormBodyPartBuilder getResponseTemplate(Wii responder, boolean overwrite) throws MessagingException {
		if(replyBuilder == null) {
			FormBodyPartBuilder builder = FormBodyPartBuilder.create();
			if(overwrite) {
				replyBuilder = builder;
			}
			builder.addField("Date", TimeUtils.getRC24Date(Date.from(Instant.now())));
			builder.addField("From", responder.getRiiEmail());
			builder.addField("To", respondee.getRiiEmail());
			builder.setName("body");
			return builder;
		}
		return replyBuilder;
	}
	
}

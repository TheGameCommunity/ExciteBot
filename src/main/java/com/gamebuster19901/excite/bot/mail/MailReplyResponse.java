package com.gamebuster19901.excite.bot.mail;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.util.TimeUtils;
import com.gamebuster19901.excite.util.file.File;

public abstract class MailReplyResponse<T> extends MailResponse implements EMessage {
	
	public static final String US_ASCII = "us-ascii";
	public static final String UTF16BE = "utf-16be";
	
	protected Wii responder;
	protected Wii respondee;
	
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
			send();
		} catch (IOException e) {
			throw new MessagingException("An exception occured when sending the mail", e);
		}
	}

	protected final void send() throws MessagingException, IOException {
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

			MailHandler.LOGGER.log(Level.INFO, this.getClass().getCanonicalName());
			MailHandler.LOGGER.log(Level.INFO, getResponseTemplates(responder).size() + "");
			
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			for(Object response : getResponseTemplates(responder)) {
				builder.addPart(reply.build());
			}
			
			MultipartEntityBuilder dupeMultiPart = MultipartEntityBuilder.create();
			for (FormBodyPartBuilder dupeBuilder : getResponseTemplates(responder)) {
				dupeMultiPart.addPart(dupeBuilder.build());
			}
			
			request.setEntity(builder.build());
			request.getEntity().getContent();
			dupe.setEntity(dupeMultiPart.build());
			CloseableHttpResponse response = client.execute(request);
			HttpEntity responseEntity = response.getEntity();
			if(responseEntity != null) {
				InputStream stream = responseEntity.getContent();
				String rawResponse = new String(IOUtils.toByteArray(stream), StandardCharsets.UTF_8);
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
	
	protected List<T> getResponseTemplates(Wii responder) throws MessagingException {
		ArrayList<T> ret = new ArrayList<T>();
		ret.add(getDefaultResponseTemplate(responder));
		return ret;
	}
	
	protected abstract T getDefaultResponseTemplate(Wii responder);
	
}

package com.gamebuster19901.excite.bot.mail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.util.ThreadService;
import com.gamebuster19901.excite.util.file.File;

public abstract class MailReplyResponse extends MailResponse {
	
	public static final String US_ASCII = "us-ascii";
	
	protected Wii responder;
	protected MimeMessage reply;
	
	public MailReplyResponse(Wii responder, MimeMessage message) {
		super(message);
		this.responder = responder;
	}
	
	@Override
	public void respond() throws MessagingException {
		try {
			this.reply = getResponseTemplate(responder);
			send();
		} catch (IOException e) {
			throw new MessagingException("An exception occured when sending the mail", e);
		}
	}

	protected final void send() throws MessagingException, IOException{
		if(reply == null) {
			throw new IllegalStateException("No reply set?!");
		}
		File secretFile = new File("./mail.secret");
		String wiiID;
		String password;
		HttpPost request;
		BufferedReader fileReader = null;
		InputStreamReader mailReader = null;
		CloseableHttpClient client = null;
		PipedOutputStream out = new PipedOutputStream();
		PipedInputStream in= new PipedInputStream();
		try {
			client = HttpClients.createDefault();
			fileReader = new BufferedReader(new FileReader(secretFile));
			wiiID = fileReader.readLine();
			password = fileReader.readLine();
			client = HttpClients.createDefault();
			request = new HttpPost("https://mtw." + MailHandler.SERVER + "/cgi-bin/send.cgi?mlid=" + wiiID + "&passwd=" + password + "&maxsize=11534336");
			in.connect(out);
			Thread waitingThread = Thread.currentThread();
			boolean waiting = true;
			ThreadService.run(new Thread (() -> {
				try {
					reply.writeTo(out);
					out.close();
				} catch (IOException | MessagingException e) {
					MailHandler.LOGGER.log(Level.SEVERE, "", e);
				}
			}));
			HttpEntity entity = new InputStreamEntity(in);
			request.setEntity(entity);
			CloseableHttpResponse response = client.execute(request);
			in.close();
			HttpEntity responseEntity = response.getEntity();
			if(responseEntity != null) {
				InputStream stream = responseEntity.getContent();
				String rawResponse = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
				MailHandler.LOGGER.log(Level.INFO, rawResponse);
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
				if(in != null) {
					in.close();
				}
				if(out != null) {
					out.close();
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
	
	protected final void setReply(MimeMessage reply) {
		this.reply = reply;
	}
	
	protected MimeMessage getResponseTemplate(Wii responder) throws MessagingException {
		Session session = message.getSession();
		MimeMessage response = new MimeMessage(session);
		response.setFrom(responder.getEmail());
		return response;
	}
	
	protected MimeBodyPart genEmptyPart() {
		return new MimeBodyPart();
	}
	
	protected MimeBodyPart genTextPart(String text) throws MessagingException {
		return genTextPart(text, US_ASCII);
	}
	
	protected MimeBodyPart genTextPart(String text, String encoding) throws MessagingException {
		MimeBodyPart textPart = genEmptyPart();
		textPart.setText(text, encoding);
		return textPart;
	}
	
	protected MimeBodyPart genContentPart(DataHandler dataHandler) throws MessagingException {
		MimeBodyPart contentPart = genEmptyPart();
		contentPart.setDataHandler(dataHandler);
		return contentPart;
	}
	
}

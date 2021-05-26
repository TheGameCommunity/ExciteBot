package com.gamebuster19901.mailtest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;

import com.gamebuster19901.excite.bot.mail.MailHandler;
import com.gamebuster19901.excite.bot.user.Wii;
import com.gamebuster19901.excite.util.file.File;

public class Main {

	public static final Logger LOGGER = Logger.getLogger("mail");
	
	public static void main(String[] args) throws IOException {
		File secretFile = new File("./mail.secret");
		String wiiID;
		String password;
		HttpPost request;
		HttpPost dupe;
		BufferedReader fileReader = null;
		InputStreamReader mailReader = null;
		Wii responder = Wii.getWii("0633303276188884");
		try {
			fileReader = new BufferedReader(new FileReader(secretFile));
			wiiID = fileReader.readLine();
			password = fileReader.readLine();
			dupe = new HttpPost("https://mtw." + MailHandler.SERVER + "/cgi-bin/send.cgi?mlid=" + wiiID + "&passwd=" + password + "&maxsize=11534336");
			password = null;
			
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			
			FormBodyPartBuilder formBuilder1 = FormBodyPartBuilder.create();
			formBuilder1.setBody(new WiiMessageBody(responder, responder));
			formBuilder1.setBody(new StringBody("WC24 Cmd Message", Charset.forName("us-ascii")));
			
			formBuilder1.setName("formBuilder1");
			formBuilder1.setField("Content-Disposition", "");
			
			builder.addPart(formBuilder1.build());
			
			dupe.setEntity(builder.build());
			HttpEntity responseEntity = dupe.getEntity();
			if(responseEntity != null) {
				ByteArrayOutputStream logStream = new ByteArrayOutputStream();
				dupe.getEntity().writeTo(logStream);
				LOGGER.log(Level.INFO, new String(logStream.toByteArray()));
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
				LOGGER.log(Level.SEVERE, "An exception occurred when closing POST readers", t);
			}
		}
	}
	
}

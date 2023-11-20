package com.gamebuster19901.excite;

import org.apache.commons.io.IOUtils;

import com.thegamecommunity.excite.modding.game.mail.Mail;
import com.thegamecommunity.excite.modding.game.mail.WiiMail;

public class TestMail {

	private static final WiiMail mail;
	static {
		try {
			String data = 
					"MAIL_FROM: w1056185520598803@rc24.xyz\n"
					+ "RCPT TO: w0633303276188884@rc24.xyz\n"
					+ "From: w1056185520598803@rc24.xyz\n"
					+ "To: w0633303276188884@rc24.xyz\n"
					+ "Subject: WC24 Cmd Message\n"
					+ "X-Wii-AppId: 0-00000001-0001\n"
					+ "X-Wii-Cmd: 80010001\n"
					+ "Content-Type: multipart/mixed; boundary=Boundary-NWC24-03BEF87900066\n"
					+ "\n"
					+ "--Boundary-NWC24-03BEF87900066\n"
					+ "Content-Type: text/plain; charset=us-ascii\n"
					+ "Content-Transfer-Encoding: 7bit\n"
					+ "\n"
					+ "WC24 Cmd Message\n"
					+ "--Boundary-NWC24-03BEF87900066\n"
					+ "Content-Type: application/octet-stream;\n"
					+ " name=a0000102.dat\n"
					+ "Content-Transfer-Encoding: base64\n"
					+ "Content-Disposition: attachment;\n"
					+ " filename=a0000102.dat\n"
					+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n"
					+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n"
					+ "AAAAAAAAAAAAAAAAAAA=";
			mail = (WiiMail) Mail.getMail(IOUtils.toInputStream(data));
		}
		catch(Throwable t) {
			throw new Error(t);
		}
	}
	
	public static void main(String[] args) {

	}
	
}

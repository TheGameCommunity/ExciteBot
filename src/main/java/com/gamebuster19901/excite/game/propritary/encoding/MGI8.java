package com.gamebuster19901.excite.game.propritary.encoding;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class MGI8 extends Charset {

	protected MGI8() {
		super("MGI8", new String[]{"MGI-8"});
	}

	@Override
	public boolean contains(Charset cs) {
		return false;
	}

	@Override
	public CharsetDecoder newDecoder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CharsetEncoder newEncoder() {
		// TODO Auto-generated method stub
		return null;
	}

}

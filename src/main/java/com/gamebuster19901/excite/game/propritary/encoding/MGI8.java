package com.gamebuster19901.excite.game.propritary.encoding;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

import com.gamebuster19901.excite.game.cLanguage.Unsigned;

public class MGI8 extends Charset {

	public static final MGI8 MGI8 = new MGI8();
	
	protected MGI8() {
		super("MGI8", new String[]{"MGI-8", "mgi8", "mgi-8"});
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
	
	private static final class MGI8Decoder extends CharsetDecoder {

		protected MGI8Decoder() {
			super(MGI8, 0.5f, 0.5f);
		}

		@Override
		protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
			//iVar3 = 
			while(in.hasRemaining()) {
				if(in.limit() - (in.position() + 4) < 0) {
					throw new IllegalArgumentException("Overflow converting MGI-8 to UTF-8");
				}
				in.mark();
				@Unsigned byte pbVar4 = in.get();
				@Unsigned byte bVar6 = pbVar4;
				@Unsigned int uVar5;
				if((bVar6 & 0xf0) == 0xe0) {
					@Unsigned byte pbVar2 = in.get();
					@Unsigned byte pbVar1 = in.get();
					pbVar4 = in.get();
					uVar5 = pbVar1 & 0x3f | (pbVar2 & 0x3f) << 6 | (bVar6 & 0xf) << 0xc;
				}
				else if ((bVar6 & 0xe0) == 0xc0) {
					@Unsigned byte pbVar1 = in.get();
					pbVar4 = in.get();
					uVar5 = (bVar6 & 0x1f) << 6 | pbVar1 & 0xfffff83f;
				}
				else {
					uVar5 = in.get() & 0x7f;
				}
				
				if(0x20 < uVar5) {
					in.reset();
					if(((Short.toUnsignedInt(in.getShort()) << 8) >>> 8) + 0x18 <= uVar5) {
						uVar5 = 0x3f;
					}
					//uVar5 = iVar
				}
			}
			return null;
		}
		
	}

}

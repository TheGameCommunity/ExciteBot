package com.gamebuster19901.excite.mixin;

import org.apache.commons.io.output.NullWriter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = NullWriter.class, remap = false)
public class NullWriterMixin extends NullWriter{

	@Override
	@Overwrite
	public void write(String str) {
		System.out.println("poop");
	}
}

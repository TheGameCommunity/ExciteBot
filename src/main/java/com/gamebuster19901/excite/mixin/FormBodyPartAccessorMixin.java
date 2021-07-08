package com.gamebuster19901.excite.mixin;

import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.content.ContentBody;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = FormBodyPart.class, remap = false, priority = 0)
public interface FormBodyPartAccessorMixin {

	@Invoker("<init>")
	public static FormBodyPart create(String name, ContentBody body, Header header) {
		throw new UnsupportedOperationException("Constructor injection failed!");
	}
	
}

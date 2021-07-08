package com.gamebuster19901.excite.mixin;

import com.google.common.collect.ArrayListMultimap;

import org.apache.http.entity.mime.MinimalField;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinimalField.class, remap = false)
public class MinimalFieldMixin {

	private @Final @Unique ArrayListMultimap<String, String> subValues = ArrayListMultimap.create();
	
	@Unique
	public void addValuePair(String name, String value) {
		subValues.put(name, value);
	}
	
	@Inject(method = "toString(Ljava/lang/String;)V", at = @At("TAIL"))
	public void toString(CallbackInfoReturnable<String> info) {
		if(subValues.isEmpty()) {
			return;
		}
		StringBuilder buffer = new StringBuilder(info.getReturnValue());
		subValues.forEach((key, value) -> {buffer.append("\r\n " + key + "=\"" + value + "\"");});
		info.setReturnValue(buffer.toString());
	}
	
}

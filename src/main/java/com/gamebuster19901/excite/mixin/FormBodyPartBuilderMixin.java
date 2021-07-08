package com.gamebuster19901.excite.mixin;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.MIME;

import static org.apache.http.entity.mime.MIME.*;
import org.apache.http.entity.mime.MinimalField;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.util.Args;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.gamebuster19901.excite.bot.mail.mime.NoContentBody;
import com.gamebuster19901.excite.mixin_interfaces.FormBodyPartBuilderInterface;

@Mixin(value = FormBodyPartBuilder.class, remap = false, priority = 1)
public class FormBodyPartBuilderMixin implements FormBodyPartBuilderInterface{
	
	private @Shadow String name;
	private @Shadow ContentBody body;
	private @Shadow @Final Header header;
	private @Unique String transferEncoding = "8bit";
	private @Unique boolean forceContentDisposition = true;
	private @Unique boolean forceFileName = true;
	private @Unique boolean forceContentType = true;
	private @Unique boolean forceTransferEncoding = true;
	
	@Intrinsic //If apache ever implements this, then don't overwrite
	@Dynamic("Doesn't actually exist in apache mime, but might in the future")
	public FormBodyPartBuilderInterface removeField(String name, String value) {
		Args.notNull(name, "Field name");
		List<MinimalField> fields = this.header.getFields(name);
		List<MinimalField> fieldsToRemove = new ArrayList<MinimalField>();
		fields.forEach(field -> {
			if(field.getBody().equals(value)) {
				fieldsToRemove.add(field);
			}
		});
		fields.removeAll(fieldsToRemove);
		return this;
	}
	
	public FormBodyPartBuilderMixin forceContentDisposition(boolean value) {
		forceContentDisposition = value;
		return this;
	}
	
	public FormBodyPartBuilderMixin forceFileName(boolean value) {
		forceFileName = value;
		return this;
	}
	
	public FormBodyPartBuilderMixin forceContentType(boolean value) {
		forceContentType = value;
		return this;
	}
	
	public FormBodyPartBuilderMixin forceTransferEncoding(boolean value) {
		forceTransferEncoding = value;
		return this;
	}
	
	public FormBodyPartBuilderMixin setTransferEncoding(String encoding) {
		transferEncoding = encoding;
		return this;
	}
	
	@Inject(at = @At("HEAD"), method = "build()V")
	public FormBodyPart build(CallbackInfoReturnable<FormBodyPart> info) {
		if(body == null) {
			body = new NoContentBody();
		}
		Header header = new Header();
		for(MinimalField f : this.header.getFields()) {
			header.addField(f);
		}
		if(forceContentDisposition && header.getField(CONTENT_DISPOSITION) == null) {
			final StringBuilder builder = new StringBuilder();
			builder.append("form-data; name=\"");
			builder.append(encodeHeader(name));
			builder.append("\"");
			if(forceFileName && this.body.getFilename() != null) {
				builder.append("; filename=\"");
				builder.append(encodeHeader(this.body.getFilename()));
				builder.append("\"");
			}
			header.addField(new MinimalField(MIME.CONTENT_DISPOSITION, builder.toString()));
		}
		if (forceContentType && header.getField(CONTENT_TYPE) == null) {
			ContentType contentType = null;
			if(this.body instanceof AbstractContentBody) {
				contentType = ((AbstractContentBody)body).getContentType();
			}
			if(contentType != null) {
				header.addField(new MinimalField(CONTENT_TYPE, contentType.toString()));
			}
			else {
				StringBuilder builder = new StringBuilder();
				builder.append(this.body.getMimeType());
				if (this.body.getCharset() != null) {
					builder.append("; charset=");
					builder.append(this.body.getCharset());
				}
				header.addField(new MinimalField(CONTENT_TYPE, builder.toString()));
			}
		}
		if (forceTransferEncoding && header.getField(CONTENT_TRANSFER_ENC) == null) {
			header.addField(new MinimalField(CONTENT_TRANSFER_ENC, body.getTransferEncoding()));
		}
		
		return FormBodyPartAccessorMixin.create(name, body, header);
	}
	
	@Intrinsic(displace = true)
	public FormBodyPartBuilder flip() {
		return (FormBodyPartBuilder)(Object)this;
	}
	
	private static String encodeHeader(String headerName) {
		if (headerName == null) {
			throw new NullPointerException("Null header");
		}
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < headerName.length(); i++) {
			final char x = headerName.charAt(i);
			if (x == '"' || x == '\\' || x == '\r') {
				sb.append("\\");
			}
			sb.append(x);
		}
		return sb.toString();
	}
	
	@Inject(at = @At("HEAD"), cancellable = true, method = "encodeForHeader(Ljava/lang/String;)Ljava/lang/String;")
	private static void encodeHeaderRedirect(String headerName, CallbackInfoReturnable<String> info) {
		info.setReturnValue(MethodAccess.encodeHeader(headerName));
	}
	
	public static class MethodAccess {
		public static String encodeHeader(String headerName) {
			return FormBodyPartBuilderMixin.encodeHeader(headerName);
		}
	}
	
}

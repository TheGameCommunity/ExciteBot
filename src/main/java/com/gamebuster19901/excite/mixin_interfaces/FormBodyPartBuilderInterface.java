package com.gamebuster19901.excite.mixin_interfaces;

import org.apache.http.entity.mime.FormBodyPartBuilder;

public interface FormBodyPartBuilderInterface extends Flippable<FormBodyPartBuilder>{
	
	public FormBodyPartBuilderInterface removeField(String name, String value);
	
	public FormBodyPartBuilderInterface forceContentDisposition(boolean value);
	
	public FormBodyPartBuilderInterface forceFileName(boolean value);
	
	public FormBodyPartBuilderInterface forceContentType(boolean value);
	
	public FormBodyPartBuilderInterface forceTransferEncoding(boolean value);
	
	public FormBodyPartBuilderInterface setTransferEncoding(String encoding);
	
	public FormBodyPartBuilder flip();
	
}

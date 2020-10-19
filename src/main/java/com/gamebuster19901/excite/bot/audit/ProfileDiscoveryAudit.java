package com.gamebuster19901.excite.bot.audit;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.gamebuster19901.excite.Player;

public class ProfileDiscoveryAudit extends Audit {

	private static final int DB_VERSION = 0;
	
	public ProfileDiscoveryAudit(Player player) {
		super(getContext(player)); 
	}
	
	public ProfileDiscoveryAudit() {
		
	}
	
	@Override
	public Audit parseAudit(CSVRecord record) {
		super.parseAudit(record);
		//0-7 is audit
		int i = super.getRecordSize();
		i++; // 8 is ProfileDiscoveryAudit version
		
		
		return this;
	}
	
	@Override
	protected int getRecordSize() {
		return super.getRecordSize() + 1;
	}
	
	@Override
	public List<Object> getParameters() {
		List<Object> params = super.getParameters();
		params.addAll(Arrays.asList(new Object[] {new Integer(DB_VERSION)}));
		return params;
	}
	
}
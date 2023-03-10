package com.gamebuster19901.excite.bot.database;

public interface Function {
	public String sqlString();
	
	public static final class Lower implements Function {

		private final String value;
		
		public static final Lower of(Function function) {
			return new Lower(function);
		}
		
		public Lower(Function function) {
			this.value = function.sqlString();
		}
		
		@Override
		public String sqlString() {
			return "lower(" + value + ")";
		}
		
	}

}

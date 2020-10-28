package com.gamebuster19901.excite.bot.database;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.gamebuster19901.excite.bot.command.MessageContext;
import com.gamebuster19901.excite.util.DataMethod;
import com.gamebuster19901.excite.util.DataPoint;

public abstract class SQLSerializeable {
	
	@SuppressWarnings("rawtypes")
	private transient HashMap<String, DataPoint> dataPoints = new HashMap<String, DataPoint>();
	
	protected SQLSerializeable() {
		setDataPoints();
	}
	
	public abstract Table getTable();
	
	protected void updateWhere(MessageContext context, String[] columns, Object[] values, String where) throws SQLException{
		if(columns.length == values.length) {
			PreparedStatement ps = context.getConnection().prepareStatement("UPDATE ? SET ? WHERE ?");
			
			int i = 0;
			String[] params = new String[columns.length];
			for(String s : columns) {
				params[i] = columns[i] + " = " + values[i];
				i++;
			}
			
			String updates = String.join(", ", params);
			
			ps.setString(1, getTable().toString());
			ps.setString(2, updates);
			ps.setString(3, where);
			
			ps.execute();
		}
		else {
			throw new IllegalArgumentException("Columns and values must be the same length!");
		}
	}
	
	protected void insert(MessageContext context) throws SQLException {
		into(context, "INTO");
	}
	
	@Deprecated
	protected void replace(MessageContext context) throws SQLException {
		into(context, "REPLACE");
	}
	
	@SuppressWarnings("rawtypes")
	protected void into(MessageContext context, String command) throws SQLException {
		String statement = "? INTO ? (PARAMS) VALUES (PARAMS)";
		if(dataPoints.size() < 1) {
			throw new IllegalStateException();
		}
		
		String[] paramHolder = new String[dataPoints.size()];
		Arrays.fill(paramHolder, "?");
		String params = String.join(", ", paramHolder);
		
		PreparedStatement ps = context.getConnection().prepareStatement(statement.replace("PARAMS", params));
		int columnIndex = 1;
		int valueIndex = dataPoints.size();
		
		ps.setString(columnIndex, command);
		valueIndex++;
		
		ps.setString(columnIndex++, getTable().toString());
		valueIndex++;
		
		for(Map.Entry<String, DataPoint> data : dataPoints.entrySet()) {
			DataPoint column = data.getValue();
			Object value = data.getValue().getValue();
			ps.setString(columnIndex++, column.getName());
			ps.setString(valueIndex++, value.toString());
		}
		ps.execute();
	}
	
	/**
	 * This should generally not be overridden
	 */
	@Deprecated
	protected void setDataPoints() {
		Class<?> clazz = getClass();
		while(SQLSerializeable.class.isAssignableFrom(clazz)) {
			fieldLoop:
			for(Field f : clazz.getDeclaredFields()) {
				if(Modifier.isStatic(f.getModifiers()) || Modifier.isTransient(f.getModifiers())) {
					continue fieldLoop;
				}
				f.setAccessible(true);
				if(dataPoints.put(f.getName(), new DataPoint<Field>(this, f)) != null) {
					throw new Error("Unresolved compilation problem:\n\nMultiple data points with same name: " + f.getName());
				}
			}
			methodLoop:
			for(Method m : clazz.getDeclaredMethods()) {
				if(Modifier.isStatic(m.getModifiers()) || !m.isAnnotationPresent(DataMethod.class)) {
					continue methodLoop;
				}
				if(m.getParameterCount() != 0) {
					throw new Error("Unresolved compilation problem:\n\n@DataPoint can only be placed on methods with no paramaters! \n\nClass: " + m.getDeclaringClass() + "\n\nMethod: " + m);
				}
				if(m.getReturnType() == void.class) {
					throw new Error("Unresolved compilation problem:\n\n@DataPoint cannot be placed on a void method.");
				}
				m.setAccessible(true);
				if(dataPoints.put(m.getName(), new DataPoint<Method>(this, m)) != null) {
					throw new Error("Unresolved compilation problem:\n\nMultiple data points with same name: " + m.getName());
				}
			}
			clazz = clazz.getSuperclass();
		}
	}
}

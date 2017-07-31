package com.webcollector.test.crawer.utils;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.ResultSetWrappingSqlRowSetMetaData;

public class DAORowMapper<T> implements RowMapper<T> {
	
	static protected enum ClassType {
		STRING, INT, LONG, BOOLEAN, DOUBLE, UTILDATE, CALENDAR, SQLTIMESTAMP, SQLDATE, SQLTIME;
	}
	
	private Class<? extends T> rowObjClass;
	private boolean direct;
	static protected Map<String, String> classNameCastMap = new HashMap<String, String>();
	static protected Map<String, DAORowMapper.ClassType> classNameMap = new HashMap<String, DAORowMapper.ClassType>();
	
	static {
		DAORowMapper.classNameCastMap.put("java.lang.String", ",java.lang.String,");
		DAORowMapper.classNameCastMap.put("java.lang.Integer", ",double,java.lang.Double,int,java.lang.Integer,boolean,java.lang.Boolean,");
		DAORowMapper.classNameCastMap.put("java.math.BigInteger", ",double,java.lang.Double,int,java.lang.Integer,boolean,java.lang.Boolean,");
		DAORowMapper.classNameCastMap.put("java.lang.Long", ",double,java.lang.Double,long,java.lang.Long,int,java.lang.Integer,boolean,java.lang.Boolean,");
		DAORowMapper.classNameCastMap.put("java.lang.Boolean", ",boolean,java.lang.Boolean,");
		DAORowMapper.classNameCastMap.put("java.math.BigDecimal", ",double,java.lang.Double,long,java.lang.Long,int,java.lang.Integer,boolean,java.lang.Boolean,");
		DAORowMapper.classNameCastMap.put("java.lang.Double", ",double,java.lang.Double,long,java.lang.Long,int,java.lang.Integer,boolean,java.lang.Boolean,");
		DAORowMapper.classNameCastMap.put("java.lang.Float", ",double,java.lang.Double,long,java.lang.Long,int,java.lang.Integer,boolean,java.lang.Boolean,");
		DAORowMapper.classNameCastMap.put("java.sql.Timestamp", ",java.sql.Timestamp,java.sql.Date,java.util.Calendar,java.util.Date,java.lang.String,");
		DAORowMapper.classNameCastMap.put("java.sql.Date", ",java.sql.Timestamp,java.sql.Date,java.util.Calendar,java.util.Date,java.lang.String,");
		DAORowMapper.classNameCastMap.put("java.sql.Time", ",java.sql.Time,java.util.Calendar,java.util.Date,java.lang.String,");
		
		DAORowMapper.classNameMap.put("java.lang.String", ClassType.STRING);
		DAORowMapper.classNameMap.put("int", ClassType.INT);
		DAORowMapper.classNameMap.put("java.lang.Integer", ClassType.INT);
		DAORowMapper.classNameMap.put("long", ClassType.LONG);
		DAORowMapper.classNameMap.put("java.lang.Long", ClassType.LONG);
		DAORowMapper.classNameMap.put("boolean", ClassType.BOOLEAN);
		DAORowMapper.classNameMap.put("java.lang.Boolean", ClassType.BOOLEAN);
		DAORowMapper.classNameMap.put("double", ClassType.DOUBLE);
		DAORowMapper.classNameMap.put("java.lang.Double", ClassType.DOUBLE);
		DAORowMapper.classNameMap.put("java.util.Date", ClassType.UTILDATE);
		DAORowMapper.classNameMap.put("java.util.Calendar", ClassType.CALENDAR);
		DAORowMapper.classNameMap.put("java.sql.Timestamp", ClassType.SQLTIMESTAMP);
		DAORowMapper.classNameMap.put("java.sql.Date", ClassType.SQLDATE);
		DAORowMapper.classNameMap.put("java.sql.Time", ClassType.SQLTIME);
	}
	
	public DAORowMapper(Class<? extends T> rowObjClass) {
		super();
		this.rowObjClass = rowObjClass;
		this.direct = this.isDirectClass();
	}

	public DAORowMapper(Class<? extends T> rowObjClass, boolean direct) {
		super();
		this.rowObjClass = rowObjClass;
		boolean directClass = this.isDirectClass();
		this.direct = directClass ? true : direct;
	}
	
	private final boolean isDirectClass() {
		if (this.rowObjClass == null) return false;
		return DAORowMapper.classNameMap.get(this.rowObjClass.getName()) != null;
	}

	@SuppressWarnings(value = "unchecked")
	public T mapRow(ResultSet rs, int index){
		T object = null;
		try {
			Method[] methods = null;
			//获取列数据	
			ResultSetWrappingSqlRowSetMetaData wapping = new ResultSetWrappingSqlRowSetMetaData(rs.getMetaData());
			int columnCount = wapping.getColumnCount();
			if (this.direct){
				Object value = null;
				int columnIndex = 1;
				if(columnCount == 1){
					String columnClassName = wapping.getColumnClassName(columnIndex);//列被封装的java类型名称
					if (DAORowMapper.classNameCastMap.get(columnClassName).indexOf(this.rowObjClass.getName()) != -1 || this.rowObjClass.getName().equals("java.lang.Object")) {
						DAORowMapper.ClassType classType = DAORowMapper.classNameMap.get(this.rowObjClass.getName());
						if (classType != null){
							if(rs.getObject(columnIndex) != null || classType == DAORowMapper.ClassType.STRING){
								switch (classType) {
								case STRING:
									value = rs.getString(columnIndex);
									if(value == null){
										value = "";
									}else{
										value = rs.getString(columnIndex);
									}
									break;
								case INT:	
									value = rs.getInt(columnIndex);
									break;
								case LONG:
									value = rs.getLong(columnIndex);
									break;
								case BOOLEAN:
									value = rs.getBoolean(columnIndex);
									break;
								case DOUBLE:
									value = rs.getDouble(columnIndex);
									break;
								case UTILDATE:
									value = new java.util.Date(rs.getTimestamp(columnIndex).getTime());
									break;
								case CALENDAR:
									Calendar targetValue = Calendar.getInstance();
									targetValue.setTimeInMillis(rs.getTimestamp(columnIndex).getTime());
									value = targetValue;
									break;
								case SQLTIMESTAMP:
									value = rs.getTimestamp(columnIndex);
									break;
								case SQLDATE:
									value = rs.getDate(columnIndex);
									break;
								case SQLTIME:
									value = rs.getTime(columnIndex);
									break;
								}
							}
						}
					}
				}
				return (T)value;
			}else{
				object = this.rowObjClass.newInstance();
				methods = this.rowObjClass.getMethods(); //获取数据保存对象所有的公开方法，包括继承的方法
				for (int columnIndex = 0; columnIndex++ != columnCount;){
					String columnClassName = wapping.getColumnClassName(columnIndex);//列被封装的java类型名称
					//找到和当前字段名称一致的对象属性设置方法，然后赋值
					String columnName = wapping.getColumnLabel(columnIndex);
					for (Method method : methods) {
						Object value = null;
						//通过方法名以及参数类型来过滤掉不匹配的方法，过滤之后剩下的就是对应的 setter
						String methodName = method.getName();
						if (methodName != null && methodName.equalsIgnoreCase("set".concat(columnName))){
							//获取参数类型
							Class<?>[] params = method.getParameterTypes();
							if (params.length == 1) {
								if (DAORowMapper.classNameCastMap.get(columnClassName).indexOf("," + params[0].getName() + ",") != -1 || params[0].getName().equals("java.lang.Object")) {
									DAORowMapper.ClassType classType = DAORowMapper.classNameMap.get(params[0].getName());
									if (classType != null){
										if(rs.getObject(columnIndex) != null || classType == DAORowMapper.ClassType.STRING){
											switch (classType) {
											case STRING:
												value = rs.getString(columnIndex);
												if(value == null){
													value = "";
												}else{
													if(columnClassName.equals("java.sql.Timestamp")){
														SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
														value = sdf.format(rs.getTimestamp(columnIndex));
													}else{
														value = rs.getString(columnIndex);
													}
												}
												break;
											case INT:	
												value = rs.getInt(columnIndex);
												break;
											case LONG:
												value = rs.getLong(columnIndex);
												break;
											case BOOLEAN:
												value = rs.getBoolean(columnIndex);
												break;
											case DOUBLE:
												value = rs.getDouble(columnIndex);
												break;
											case UTILDATE:
												value = new java.util.Date(rs.getTimestamp(columnIndex).getTime());
												break;
											case CALENDAR:
												Calendar targetValue = Calendar.getInstance();
												targetValue.setTimeInMillis(rs.getTimestamp(columnIndex).getTime());
												value = targetValue;
												break;
											case SQLTIMESTAMP:
												value = rs.getTimestamp(columnIndex);
												break;
											case SQLDATE:
												value = rs.getDate(columnIndex);
												break;
											case SQLTIME:
												value = rs.getTime(columnIndex);
												break;
											}
										}else{
											break;
										}
									}
								}
							}
						}
						//执行 setter
						if(value != null){
							method.invoke(object, value);
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			object = null;
		}
		return object;
	}

}
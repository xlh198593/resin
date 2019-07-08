package com.winterframework.generator.provider.db.model;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import com.winterframework.generator.GeneratorProperties;
import com.winterframework.generator.util.ActionScriptDataTypesUtils;
import com.winterframework.generator.util.DatabaseDataTypesUtils;
import com.winterframework.generator.util.GLogger;
import com.winterframework.generator.util.JdbcType;
import com.winterframework.generator.util.StringHelper;
import com.winterframework.generator.util.TestDataGenerator;

/**
 * 
 * @author abba
 * @email xuhengbiao@gmail.com
 */
public class Column {
	protected static String[] defaultColumnNames = new String[] { "id","companyNo", "delStatus", "createTime", "updateTime",
			"creatorId", "updatorId", "remark" };
	private static Map<String,String> map=new HashMap<String,String>();
    static {
    			map.put("byte","_byte");
    			map.put("long","_long");
    			map.put("short","_short");
    			map.put("int","_int");
    			map.put("int","_integer");
    			map.put("double","_double");
    			map.put("float","_float");
    			map.put("boolean","_boolean");
    			map.put("java.lang.String","string");
    			map.put("java.lang.Byte","byte");
    			map.put("java.lang.Long","long");
    			map.put("java.lang.Short","short");
    			map.put("java.lang.Integer","int");
    			map.put("java.lang.Integer","integer");
    			map.put("java.lang.Double","double");
    			map.put("java.lang.Float","float");
    			map.put("java.lang.Boolean","boolean");
    			map.put("java.util.Date","date");
    			map.put("java.math.BigDecimal","decimal");
    			map.put("java.math.BigDecimal","bigdecimal");
    			map.put("java.lang.Object","object");
    			map.put("java.util.Map","map");
    			map.put("java.util.HashMap","hashmap");
    			map.put("java.util.List","list");
    			map.put("java.util.ArrayList","arraylist");
    			map.put("java.util.Collection","collection");
    			map.put("java.util.Iterator","iterator");
    };
	/**
	 * Reference to the containing table
	 */
	private final Table _table;

	/**
	 * The java.sql.Types type
	 */
	private final int _sqlType;

	/**
	 * The sql typename. provided by JDBC driver
	 */
	private final String _sqlTypeName;

	/**
	 * The name of the column
	 */
	private final String _sqlName;
	/**
	 * The name of the column
	 */
	private final String _aliasSqlName;

	/**
	 * True if the column is a primary key
	 */
	private final boolean _isPk;

	/**
	 * True if the column is a foreign key
	 */
	private boolean _isFk;

	/**
	 * @todo-javadoc Describe the column
	 */
	private final int _size;

	/**
	 * @todo-javadoc Describe the column
	 */
	private final int _decimalDigits;

	/**
	 * True if the column is nullable
	 */
	private final boolean _isNullable;

	/**
	 * True if the column is indexed
	 */
	private final boolean _isIndexed;

	/**
	 * True if the column is unique
	 */
	private final boolean _isUnique;

	/**
	 * Null if the DB reports no default value
	 */
	private final String _defaultValue;

	/**
	 * The comments of column
	 */
	private final String _remarks;

	/**
	 * Get static reference to Log4J Logger
	 */

	//	String description;
	//
	//	String humanName;
	//
	//	int order;
	//
	//	boolean isHtmlHidden;
	//
	//	String validateString;

	/**
	 * Describe what the DbColumn constructor does
	 * 
	 * @param table
	 *            Describe what the parameter does
	 * @param sqlType
	 *            Describe what the parameter does
	 * @param sqlTypeName
	 *            Describe what the parameter does
	 * @param sqlName
	 *            Describe what the parameter does
	 * @param size
	 *            Describe what the parameter does
	 * @param decimalDigits
	 *            Describe what the parameter does
	 * @param isPk
	 *            Describe what the parameter does
	 * @param isNullable
	 *            Describe what the parameter does
	 * @param isIndexed
	 *            Describe what the parameter does
	 * @param defaultValue
	 *            Describe what the parameter does
	 * @param isUnique
	 *            Describe what the parameter does
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for constructor
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for method parameter
	 */
	public Column(Table table, int sqlType, String sqlTypeName, String sqlName, String aliasSqlName, int size,
			int decimalDigits, boolean isPk, boolean isNullable, boolean isIndexed, boolean isUnique,
			String defaultValue, String remarks) {
		_table = table;
		_sqlType = sqlType;
		_sqlName = sqlName;
		_aliasSqlName = aliasSqlName;
		_sqlTypeName = sqlTypeName;
		_size = size;
		_decimalDigits = decimalDigits;
		_isPk = isPk;
		_isNullable = isNullable;
		_isIndexed = isIndexed;
		_isUnique = isUnique;
		_defaultValue = defaultValue;
		_remarks = remarks;

		GLogger.debug(sqlName + " isPk -> " + _isPk);

	}

	/**
	 * Gets the SqlType attribute of the Column object
	 * 
	 * @return The SqlType value
	 */
	public int getSqlType() {
		return _sqlType;
	}

	/**
	 * Gets the Table attribute of the DbColumn object
	 * 
	 * @return The Table value
	 */
	public Table getTable() {
		return _table;
	}

	/**
	 * Gets the Size attribute of the DbColumn object
	 * 
	 * @return The Size value
	 */
	public int getSize() {
		return _size;
	}

	/**
	 * Gets the DecimalDigits attribute of the DbColumn object
	 * 
	 * @return The DecimalDigits value
	 */
	public int getDecimalDigits() {
		return _decimalDigits;
	}

	/**
	 * Gets the SqlTypeName attribute of the Column object
	 * 
	 * @return The SqlTypeName value
	 */
	public String getSqlTypeName() {
		return _sqlTypeName;
	}

	/**
	 * Gets the SqlName attribute of the Column object
	 * 
	 * @return The SqlName value
	 */
	public String getSqlName() {
		return _aliasSqlName;
	}

	public String getDbSqlName() {
		return _sqlName;
	}

	/**
	 * Gets the SqlName attribute of the Column object
	 * 
	 * @return The SqlName value
	 */
	public String getAliasSqlName() {
		return _aliasSqlName;
	}

	public String getUnderscoreName() {
		return getSqlName().toLowerCase();
	}

	/**
	 * Gets the Pk attribute of the Column object
	 * 
	 * @return The Pk value
	 */
	public boolean isPk() {
		return _isPk;
	}

	/**
	 * Gets the Fk attribute of the Column object
	 * 
	 * @return The Fk value
	 */
	public boolean isFk() {
		return _isFk;
	}

	/**
	 * Gets the Nullable attribute of the Column object
	 * 
	 * @return The Nullable value
	 */
	public final boolean isNullable() {
		return _isNullable;
	}

	/**
	 * Gets the Indexed attribute of the DbColumn object
	 * 
	 * @return The Indexed value
	 */
	public final boolean isIndexed() {
		return _isIndexed;
	}

	/**
	 * Gets the Unique attribute of the DbColumn object
	 * 
	 * @return The Unique value
	 */
	public boolean isUnique() {
		return _isUnique;
	}

	/**
	 * Gets the DefaultValue attribute of the DbColumn object
	 * 
	 * @return The DefaultValue value
	 */
	public final String getDefaultValue() {
		return _defaultValue;
	}

	public final String getRemarks() {
		String remark = _remarks;
		if (remark != null) {
			if ((remark.indexOf("\r\n")) > -1) {
				return remark.substring(0, remark.indexOf("\r\n"));
			} else if ((remark.indexOf("\n")) > -1) {
				return remark.substring(0, remark.indexOf("\n"));
			}
		}
		return remark;
	}

	public final String getFullRemarks() {
		return _remarks;
	}

	/**
	 * Describe what the method does
	 * 
	 * @return Describe the return value
	 * @todo-javadoc Write javadocs for method
	 * @todo-javadoc Write javadocs for return value
	 */
	@Override
	public int hashCode() {
		return (getTable().getSqlName() + "#" + getSqlName()).hashCode();
	}

	/**
	 * Describe what the method does
	 * 
	 * @param o
	 *            Describe what the parameter does
	 * @return Describe the return value
	 * @todo-javadoc Write javadocs for method
	 * @todo-javadoc Write javadocs for method parameter
	 * @todo-javadoc Write javadocs for return value
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof Column) {
			Column other = (Column) o;
			if (getSqlName().equals(other.getSqlName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Describe what the method does
	 * 
	 * @return Describe the return value
	 * @todo-javadoc Write javadocs for method
	 * @todo-javadoc Write javadocs for return value
	 */
	@Override
	public String toString() {
		return getSqlName();
	}

	/**
	 * Describe what the method does
	 * 
	 * @return Describe the return value
	 * @todo-javadoc Write javadocs for method
	 * @todo-javadoc Write javadocs for return value
	 */
	protected final String prefsPrefix() {
		return "tables/" + getTable().getSqlName() + "/columns/" + getSqlName();
	}

	/**
	 * Sets the Pk attribute of the DbColumn object
	 * 
	 * @param flag
	 *            The new Pk value
	 */
	void setFk(boolean flag) {
		_isFk = flag;
	}

	////////////////修改过
	public String getColumnName() {
		return StringHelper.makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(getSqlName()));
	}

	public String getAliasColumnName() {
		return StringHelper.makeAllWordFirstLetterUpperCase(StringHelper.toUnderscoreName(this.getAliasSqlName()));
	}

	public String getColumnNameFirstLower() {
		return StringHelper.uncapitalize(getColumnName());
	}

	public String getColumnNameLowerCase() {
		return getColumnName().toLowerCase();
	}

	/**
	 * @deprecated use getColumnNameFirstLower() instead
	 */
	@Deprecated
	public String getColumnNameLower() {
		return getColumnNameFirstLower();
	}

	public String getJdbcSqlTypeName() {
		String result = JdbcType.getJdbcSqlTypeName(getSqlType());
		//if(result == null) throw new RuntimeException("jdbcSqlTypeName is null column:"+getSqlName()+" sqlType:"+getSqlType());
		return result;
	}

	public String getColumnAlias() {
		return StringHelper.emptyIf(getRemarks(), getColumnNameFirstLower());
	}

	public String getConstantName() {
		return StringHelper.toUnderscoreName(getSqlName()).toUpperCase();
	}

	public boolean getIsNotIdOrVersionField() {
		return !isPk();
	}

	public String getValidateString() {
		String result = getNoRequiredValidateString();
		if (!isNullable()) {
			result = "required " + result;
		}
		return result;
	}

	public String getNoRequiredValidateString() {
		String result = "";
		if (getSqlName().indexOf("mail") >= 0) {
			result += "validate-email ";
		}
		if (DatabaseDataTypesUtils.isFloatNumber(getSqlType(), getSize(), getDecimalDigits())) {
			result += "validate-number ";
		}
		if (DatabaseDataTypesUtils.isIntegerNumber(getSqlType(), getSize(), getDecimalDigits())) {
			result += "validate-integer ";
			if (getJavaType().indexOf("Short") >= 0) {
				result += "max-value-" + Short.MAX_VALUE;
			} else if (getJavaType().indexOf("Integer") >= 0) {
				result += "max-value-" + Integer.MAX_VALUE;
			} else if (getJavaType().indexOf("Byte") >= 0) {
				result += "max-value-" + Byte.MAX_VALUE;
			}
		}
		//		if(DatabaseDataTypesUtils.isDate(getSqlType(), getSize(), getDecimalDigits())) {
		//			result += "validate-date ";
		//		}
		return result;
	}

	public boolean getIsStringColumn() {
		return DatabaseDataTypesUtils.isString(getSqlType(), getSize(), getDecimalDigits());
	}

	public boolean getIsDateTimeColumn() {
		return DatabaseDataTypesUtils.isDate(getSqlType(), getSize(), getDecimalDigits());
	}

	public boolean getIsDate() {
		return getSqlType() == Types.DATE;
	}

	public boolean getIsTime() {
		return getSqlType() == Types.TIME;
	}

	public boolean getIsDateTime() {
		return getSqlType() == Types.TIMESTAMP;
	}

	public boolean getIsBooleanColumn() {
		return getJavaType().equals("java.lang.Boolean");
	}

	public boolean getIsNumberColumn() {
		return DatabaseDataTypesUtils.isFloatNumber(getSqlType(), getSize(), getDecimalDigits())
				|| DatabaseDataTypesUtils.isIntegerNumber(getSqlType(), getSize(), getDecimalDigits());
	}

	public boolean isHtmlHidden() {
		return isPk() && _table.isSingleId();
	}
	public String getShortJavaType(){
		return getJavaType().replace("java.lang.", "").replace("java.util.","");
	}
	public String getJavaType() {
		if (this.isPk()) {
			return "java.lang.Long";
		}
		if(this.getSqlTypeName().equalsIgnoreCase("int")){
			
            if(this.getColumnName().equalsIgnoreCase("creatorId")
            		||this.getColumnName().equalsIgnoreCase("updatorId")
            		||this.getColumnName().equalsIgnoreCase("versionNumber")){
            	return "java.lang.Long";
			}
            
			if(!this.getColumnName().equalsIgnoreCase("priceTypeId") && this.getColumnName().endsWith("Id")){
				return "java.lang.Long";
			}
			
		}
		String normalJdbcJavaType = DatabaseDataTypesUtils.getPreferredJavaType(getSqlType(), getSize(),
				getDecimalDigits());
		return GeneratorProperties.getProperty("java_typemapping." + normalJdbcJavaType, normalJdbcJavaType).trim();
	}
	public String getJavaTypeAlias(){
		String type=getJavaType();
		String javaType=map.get(type);
		return javaType==null?type:javaType;
	}

	public String getAsType() {
		return ActionScriptDataTypesUtils.getPreferredAsType(getJavaType());
	}

	public String getTestData() {
		return new TestDataGenerator().getDBUnitTestData(getColumnName(), getJavaType(), getSize(), this.isPk() ? false
				: this.isFk() ? false : this.isNullable());
	}

}

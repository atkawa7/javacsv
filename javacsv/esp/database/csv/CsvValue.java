package esp.database.csv;

import java.util.*;
import java.text.*;

public class CsvValue implements Cloneable{
	static final char integer='i';
	static final char string='s';
	static final char bool='b';
	static final char date='D';
	static final char time='t';
	static final char doub='d';
	static final char undef='u';
	static final char auto='a';
	AutoNumber an;
	SimplisticDateFormat format=new SimplisticDateFormat();

	private Object _value;
	char _type=undef;
	private boolean autoIncrement=false;

	public void setType(char type){
		if((type != integer) && autoIncrement){
			autoIncrement=false;
		}
		_type=type;
	}
	public char getType(){
		return _type;
	}
	public void setRaw(Object value){
		if(value==null && autoIncrement){
			_value=new Integer(an.getNext());
		}
		else if(value instanceof String){
			switch(_type){
				case integer:
					setInt(Integer.parseInt((String)value));
					break;
				case doub:
					setDouble(Double.parseDouble((String)value));
					break;
				case bool:
					setBoolean(Boolean.getBoolean((String)value));
					break;
				case date:
				case time:
//					try{
						setDate(format.parse((String)value));
//					}
//					catch(ParseException pe){
//						setString("");
//					}
					break;
				default:
					setString((String)value);
			}
		}
		else{
			_value=value;
		}
	}
	public Object getRaw(){
		return _value;
	}
	String unescape(String value){
		String esc[]={"\\r","\\n","\\t"};
		String rep[]={"\r","\n","\t"};
		for(int i=0;i<esc.length;i++){
			int indx=value.indexOf(esc[i]);
			while(indx>-1){
				value=value.substring(0,indx)+rep[i]+
				value.substring(indx+esc[i].length());
				indx=value.indexOf(esc[i]);
			}
		}
		return value;
	}
	String escape(String value){
		String rep[]={"\\r","\\n","\\t"};
		String esc[]={"\r","\n","\t"};
		for(int i=0;i<esc.length;i++){
			int indx=value.indexOf(esc[i]);
			while(indx>-1){
				value=value.substring(0,indx)+rep[i]+
				value.substring(indx+esc[i].length());
				indx=value.indexOf(esc[i]);
			}
		}
		return value;
	}
	public void setString(String value){
		_value=unescape(value);
	}
	public String getString(){
		return _value.toString();
	}
	public void setInt(int value){
		_value=new Integer(value);
	}
	public int getInt(){
		if(_value instanceof Integer){
			return ((Integer)_value).intValue();
		}
		return 0;
	}
	public void setDouble(double value){
		_value=new Double(value);
	}
	public double getDouble(){
		if(_value instanceof Double){
			return ((Double)_value).doubleValue();
		}
		return 0;
	}
	public void setDate(Date value){
		_value=value;
	}
	public Date getDate(){
		if(_value instanceof Date){
			return (Date)_value;
		}
		return null;
	}
	public void setBoolean(boolean value){
		_value=new Boolean(value);
	}
	public boolean getBoolean(){
		if(_value instanceof Boolean){
			return ((Boolean)_value).booleanValue();
		}
		return false;
	}
	public void setCurrency(double value){
		_value=new Double(value);
	}
	public double getCurrency(){
		return getDouble();
	}
	public Object clone(){
		CsvValue cln=new CsvValue();
		cln.setRaw(_value);
		cln.setType(_type);
		cln.autoIncrement=autoIncrement;
		cln.an=an;
		cln.format=format;
		return cln;
	}
	String saveFormat(){
		if(_type==string){
			return escape((String)_value);
		}
		return toString();
	}
	public String toString(){
		if(_value==null){
			return "";
		}
		if(_value instanceof Date){
			return format.format((Date)_value,_type);
		}
		return _value.toString();
	}
	public String toHeaderString(){
		String ret=_value.toString();
		ret+="^"+_type;
		if(autoIncrement){
			ret+="^a"+an.getCurrent();
		}
		return ret;
	}
	public static CsvValue parseField(String info){
		CsvValue field=new CsvValue();
		StringTokenizer st=new StringTokenizer(info,"^");
		field.setString(st.nextToken());
		while(st.hasMoreTokens()){
			String stMod=st.nextToken();
			char mod=stMod.charAt(0);
			switch(mod){
				case date:
				case time:
//					field.format=new SimpleDateFormat(stMod.substring(1));
				case integer:
				case string:
				case bool:
				case doub:
					field.setType(mod);
					continue;
				case auto:
					field.autoIncrement=true;
					field.an=new AutoNumber(Integer.parseInt(stMod.substring(1)));
				default: continue;
			}
		}
		return field;
	}
}

package esp.database.csv;

import java.util.*;

public class SimplisticDateFormat{
	public Date parse(String in){
		Date ret=new Date();
		StringTokenizer st=new StringTokenizer(in,"-/");
		if(st.countTokens()==3){
			ret.setMonth(Integer.parseInt(st.nextToken()));
			ret.setDate(Integer.parseInt(st.nextToken()));
			ret.setYear(Integer.parseInt(st.nextToken()));
		}
		else{
			int hourmod=0;
			if(in.toUpperCase().endsWith("PM")){
				hourmod=12;
			}
			in=in.substring(0,in.length()-2);
			st=new StringTokenizer(in,":");
			ret.setHours(Integer.parseInt(st.nextToken())+hourmod);
			ret.setMinutes(Integer.parseInt(st.nextToken()));
		}
		return ret;
	}
	public String format(Date d, char type){
		String ret="";
		if(type=='D'){
			ret+=d.getMonth()+"/";
			ret+=d.getDate()+"/";
			ret+=d.getYear();
		}
		else if(type=='t'){
			boolean pm=false;
			if(d.getHours()>12){
				ret+=d.getHours()-12;
				pm=true;
			}
			else{
				ret+=d.getHours();
			}
			ret+=":"+d.getMinutes();
			if(pm){
				ret +="PM";
			}
			else{
				ret+="AM";
			}
		}
		return ret;
	}
}

package esp.database.csv;

import java.io.*;
import java.net.*;
import java.util.*;

public class Csv{
	Vector records;
	Vector fields;
	int cursor=-1;
	Object source=null;

	/*****************************************************
	 *  Create a new empty Csv table
	 * 
	 *****************************************************/
	public Csv(){
		records=new Vector();
		fields=new Vector();
	}

	/*****************************************************
	 * Create a CSV table from the given instance of
	 * java.io.InputStream by wrapping
	 * an instance of  java.io.Reader around it.
	 * 
	 *****************************************************/
	public Csv(InputStream is) throws IOException{
		this(new InputStreamReader(is));
	}

	/*****************************************************
	 * Create a CSV table from the given instance of
	 * java.io.Reader
	 * 
	 *****************************************************/
	public Csv(Reader r) throws IOException{
		this();
		BufferedReader br=new BufferedReader(r);
		String line=br.readLine();
		if(line==null){
			throw new IOException("Empty Data Source");
		}
		StringTokenizer st=new StringTokenizer(line,",");
		while(st.hasMoreTokens()){
			fields.addElement(CsvValue.parseField(st.nextToken()));
		}
		while((line=br.readLine())!=null){
			boolean setVal=false;
			Hashtable record=new Hashtable();
			int fieldNo=0;
			st=new StringTokenizer(line,",",true);
			while(st.hasMoreTokens()){
				CsvValue field=(CsvValue)fields.elementAt(fieldNo);
				if(record.get(field)==null){
					record.put(field,field.clone());
				}
				String val=st.nextToken();
				if(val.equals(",")){
					if(!setVal){
						((CsvValue)record.get(field)).setRaw(null);
					}
					fieldNo++;
					setVal=false;
					continue;
				}
				if(val.startsWith("\"")){
					if(val.length()==1)
						val+=st.nextToken();
					while(!val.endsWith("\"")){
						val+=st.nextToken();
					}
				}
				((CsvValue)record.get(field)).setRaw(val);
				setVal=true;
			}
			records.addElement(record);
		}
		r.close();
	}

	/*****************************************************
	 * Convience function for reading a cvs file from the
	 * given location which can be either a file path or
	 * a url.
	 * 
	 *****************************************************/
	public static Csv fromString(String location) throws IOException{
		Object source;
		Csv ret=new Csv();
		try{
			URL src=new URL(location);
			if(src.getProtocol().equalsIgnoreCase("file")){
				source=new File(src.getFile());
			}
			else{
				source=src;
			}
			ret=new Csv(src.openStream());
		}
		catch(MalformedURLException e){
			source=new File(location);
			ret=new Csv(new FileReader((File)source));
		}
		ret.source=source;
		System.out.println(source);
		return ret;
	}

	/*****************************************************
	 * Get the record that is at the current cursor
	 * position
	 * 
	 *****************************************************/
	public Hashtable getRecord(){
		return (Hashtable)records.elementAt(cursor);
	}

	/*****************************************************
	 * Get the raw value of the data field named field
	 * in the current record.
	 * 
	 *****************************************************/
	public Object getRaw(Object field){
		Hashtable record=getRecord();
		return record.get(field);
	}

	/*****************************************************
	 * Advance the cursor to the next record
	 * 
	 *****************************************************/
	public boolean next(){
		if(cursor<records.size()-1){
			cursor++;
			return true;
		}
		return false;
	}

	/*****************************************************
	 * move the cursor to the previous record
	 * 
	 *****************************************************/
	public boolean prev(){
		if(cursor>0 && records.size()>0){
			cursor--;
			return true;
		}
		return false;
	}

	/*****************************************************
	 * move the cursor to the first record
	 * 
	 *****************************************************/
	public boolean first(){
		if(records.size()>0){
			cursor=0;
			return true;
		}
		return false;
	}

	/*****************************************************
	 * move the cursor to the first record
	 * 
	 *****************************************************/
	public boolean reset(){
		return first();
	}

	/*****************************************************
	 * move the cursor to the last record
	 * 
	 *****************************************************/
	public boolean last(){
		if(records.size()>0){
			cursor=records.size()-1;
			return true;
		}
		return false;
	}

	/*****************************************************
	 * move the cursor to the record specified by index
	 * 
	 *****************************************************/
	public boolean moveTo(int index){
		if(index<records.size()){
			cursor=index;
			return true;
		}
		return false;
	}

	/*****************************************************
	 * return the number of records in the table
	 * 
	 *****************************************************/
	public int countRecords(){
		return records.size();
	}

	/*****************************************************
	 * return the field names of the table as an 
	 * enumeration.
	 *****************************************************/
	public Enumeration getFields(){
		return fields.elements();
	}

	/*****************************************************
	 * Rename field named oldname to name.
	 * 
	 *****************************************************/
	public void renameField(String oldname, String name){
		renameField(fields.indexOf(getByName(oldname)),name);
	}

	/*****************************************************
	 * Delete the record at position index.
	 * 
	 *****************************************************/
	public void delete(int index){
		if(index>=records.size()){
			return;
		}
		records.removeElementAt(index);
	}

	/*****************************************************
	 * Append a new record to the end of the csv.
	 * 
	 *****************************************************/
	public void append(){
		Hashtable record=new Hashtable();
		Enumeration e=getFields();
		while(e.hasMoreElements()){
			CsvValue field=(CsvValue)e.nextElement();
			CsvValue val=(CsvValue)field.clone();
			val.setRaw(null);
			record.put(field,val);
		}
		records.addElement(record);
		cursor=records.size()-1;
	}

	/*****************************************************
	 * Set the value of field in the current record
	 * 
	 *****************************************************/
	public void setValue(String field,String value){
		setValue(cursor,getByName(field),value);
	}

	/*****************************************************
	 * Remove field name from all records.
	 * 
	 *****************************************************/
	public void delField(Object name){
		int indx=fields.indexOf(name);
		if(indx<0){
			return;
		}
		fields.removeElementAt(indx);
	}

	/*****************************************************
	 * Add the new field name to all records and set the
	 * value to null.
	 * 
	 *****************************************************/
	public void addField(String name) throws Exception{
		CsvValue field=CsvValue.parseField(name);
		if(getByName(field.toString())!=null){
			throw new Exception("Field "+field+" already exists in this table");
		}
		fields.addElement(field);
		for(int i=0;i<records.size();i++){
			Hashtable record=(Hashtable)records.elementAt(i);
			record.put(field,field.clone());
			setValue(i,field,null);
		}
	}

	/*****************************************************
	 * retrieve all the records from the csv that match
	 * criteria in the form of fieldname=value and return
	 * them as a new instalce of csv.
	 * 
	 *****************************************************/
	public Csv getRecords(String criteria){
		StringTokenizer st=new StringTokenizer(criteria,"=");
		if(st.countTokens()!=2){
			return null;
		}
		CsvValue field=getByName(st.nextToken());
		criteria=st.nextToken();
		Csv ret=new Csv();
		ret.fields=fields;
		for(int i=0;i<records.size();i++){
			Hashtable record=(Hashtable)records.elementAt(i);
			if(record.get(field).toString().equals(criteria)){
				ret.records.addElement(records.elementAt(i));
			}
		}
		return ret;
	}

	/*****************************************************
	 * Save the csv file to the file it was created from.
	 *****************************************************/
	public void save() throws IOException{
		if(source==null){
			throw new IOException("CVS source is not set");
		}
		else if(source instanceof URL){
			throw new IOException("Cannot save to URL "+source);
		}
		else{
			write(new FileWriter((File)source));
		}
	}
	/*****************************************************
	 * Write the csv to the given java.io.OutputStream
	 * by wrapping an instance of java.io.Writer around
	 * it and calling write(Writer).
	 * 
	 *****************************************************/

	public void write(OutputStream os) throws IOException{
		write(new OutputStreamWriter(os));
	}

	/*****************************************************
	 * Write the cvs to the given instance of
	 * java.io.Writer.
	 * 
	 *****************************************************/
	public void write(Writer ow) throws IOException{
		PrintWriter bw=new PrintWriter(ow);
		Enumeration e=getFields();
		while(e.hasMoreElements()){
			bw.print(((CsvValue)e.nextElement()).toHeaderString());
			if(e.hasMoreElements()){
				bw.print(",");
			}
		}
		bw.println();
		for(int i=0;i<records.size();i++){
			Hashtable rec=(Hashtable)records.elementAt(i);
			e=getFields();
			while(e.hasMoreElements()){
				CsvValue val=(CsvValue)rec.get(e.nextElement());
				if(val !=null){
					bw.print(val.saveFormat());
				}
				if(e.hasMoreElements()){
					bw.print(",");
				}
			}
			bw.println();
		}
		bw.close();
	}

	CsvValue getByName(String name){
		for(int i=0;i<fields.size();i++){
			if(fields.elementAt(i).toString().equals(name)){
				return (CsvValue)fields.elementAt(i);
			}
		}
		return null;
	}

	void renameField(int id, String name){
		((CsvValue)fields.elementAt(id)).setString(name);
	}

	void headToRecord(){
		Hashtable record=new Hashtable();
		for(int i=0;i<fields.size();i++){
			record.put(
				fields.elementAt(i),
				((CsvValue)fields.elementAt(i)).clone()
			);
		}
		records.addElement(record);
	}

	void setValue(int rec, CsvValue field,String value){
		Hashtable record=(Hashtable)records.elementAt(rec);
		((CsvValue)record.get(field)).setRaw(value);
	}

}

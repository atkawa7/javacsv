import esp.database.csv.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class CsvTest{
	public static void main(String arg[]){
		try{
			Csv myCsv=Csv.fromString("contacts.csv");
//			myCsv=myCsv.getRecords("Symb=MSFT");
			while(myCsv.next()){
				Enumeration e=myCsv.getFields();
				while(e.hasMoreElements()){
					Object f=e.nextElement();
					System.out.print(f+"=");
					System.out.println(myCsv.getRaw(f));
					myCsv.setValue("home","na");
				}
				System.out.println();
			}
			System.out.println();
//			myCsv.append();
//			myCsv.setValue("field2","10");
			try{
				myCsv.addField("id^i^a0");
			}
			catch(Exception e){
				e.printStackTrace();
			}
			myCsv.write(new FileOutputStream(new File("test1.csv")));
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
	}
}


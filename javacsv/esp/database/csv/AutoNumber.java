package esp.database.csv;
public class AutoNumber{
	int value;

	public AutoNumber(int initial){
		value=initial;
	}
	public int getNext(){
		int current=value;
		value++;
		return current;
	}
	public int getCurrent(){
		return value;
	}
}

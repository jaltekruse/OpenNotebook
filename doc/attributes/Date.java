package doc.attributes;

import java.util.Calendar;

public class Date implements Comparable<Date>{

	private static final int[] numDaysInMonth =
		{ 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	
	private static final int minDate = 1500;
			
	private int month;
	private int day;
	private int year;
	
	public Date(){
		try{
			year = Calendar.getInstance().get(Calendar.YEAR);
			month =  Calendar.getInstance().get(Calendar.MONTH) + 1;
			day =  Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		}
		catch(Exception ex){
			month = 1;
			day = 5;
			year = 2011;
		}
	}
	
	public Date( int month, int day, int year){
		this.month = month;
		this.day = day;
		this.year = year;
	}
	
	@Override
	public int compareTo(Date other) {
		if ( other.getYear() > year){
			return -1;
		}
		else if ( other.getYear() < year){
			return 1;
		}
		else
		{// years are equal
			if ( other.getMonth() > month){
				return -1;
			}
			else if ( other.getMonth() < month){
				return 1;
			}
			else{
				if ( other.getDay() > day){
					return -1;
				}
				else if ( other.getDay() < day){
					return 1;
				}
				else{
					return 0;
				}
			}
		}
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) throws AttributeException {
		if (month < 0 || month > 12){
			throw new AttributeException("Invalid month entered.");
		}
		this.month = month;
	}

	public int getDay() {
		return day;
	}


	public void setDay(int day) throws AttributeException {
		if (month == 0){
			throw new AttributeException("Set month first, so that the day " +
					"of the month can be checked for validity.");
		}
		if (day < 0 || day > numDaysInMonth[month - 1]){
			throw new AttributeException("Invalid day entered.");
		}
		this.day = day;
	}

	public int getYear() {
		return year;
	}
	
	public String toString(){
		return month + "/" + day + "/" + year;
	}

	public void setYear(int year) throws AttributeException {
		if ( year < minDate){
			throw new AttributeException("Invalid year entered.");
		}
		if ( year < 0 || year > Calendar.getInstance().get(Calendar.YEAR)){
			throw new AttributeException("Invalid year entered.");
		}
		if ( year < 20){
			year += 2000;
		}
		else if (year > 50 && year < 100){
			year += 1900;
		}
		this.year = year;
	}
}

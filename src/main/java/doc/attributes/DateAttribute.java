package doc.attributes;


public class DateAttribute extends MathObjectAttribute<Date> {

	public static final String DATE_MUST_BE_IN_THE_FORMAT_MM_DD_YYYY = "Date must be in the format mm/dd/yyyy";

	public DateAttribute(String n) {
		super(n);
	}
	
	public DateAttribute(String n, boolean userEditable) {
		super(n, userEditable);
	}
	
	public DateAttribute(String n, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
	}
	
	public DateAttribute(String n, Date date) {
		super(n, date);
	}
	public DateAttribute(String n, Date date, boolean userEditable) {
		super(n, date, userEditable);
	}
	public DateAttribute(String n, Date date, boolean userEditable, boolean studentEditable) {
		super(n, date, userEditable, studentEditable);
	}

	@Override
	public Date readValueFromString(String s) throws AttributeException {
		if ( s == null || s.equals("null")){
			return new Date();
		}
		String[] monthDayYear = s.split("/");
		Date newDate = new Date();
		for ( int i = 0; i < 2 ; i++){
			if ( monthDayYear[i].length() > 2 || monthDayYear[i].isEmpty()){
				throw new AttributeException(DATE_MUST_BE_IN_THE_FORMAT_MM_DD_YYYY);
			}
		}
		if ( ! (monthDayYear[2].length() == 4 || monthDayYear[2].length() == 2) ){
			throw new AttributeException(DATE_MUST_BE_IN_THE_FORMAT_MM_DD_YYYY);
		}
		try {
			newDate.setMonth(Integer.parseInt(monthDayYear[0]));
			newDate.setDay(Integer.parseInt(monthDayYear[1]));
			newDate.setYear(Integer.parseInt(monthDayYear[2]));
		}catch (AttributeException ex){
			throw ex;
		}catch (NumberFormatException ex){
			throw new AttributeException(DATE_MUST_BE_IN_THE_FORMAT_MM_DD_YYYY);
		}
		return newDate;
		
	}

	@Override
	public String getType() {
		return DATE;
	}

	@Override
	public void resetValue() {
		setValue(new Date());
	}

	@Override
	public DateAttribute clone(){
		try {
			DateAttribute newDate = new DateAttribute(getName());
			if ( this.getValue() != null) {
				newDate.setValue(new Date(getValue().getMonth(), getValue().getDay(), getValue().getYear()));
			}
			copyRootManagedFields(newDate);
			return newDate;
		} catch (Exception ex){
			// impossible error, values are taken out of a date, so they will
			// be valid
			throw new RuntimeException(ex);
		}
	}
}

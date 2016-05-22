package doc.attributes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailAttribute extends MathObjectAttribute<String>{
	
	private static final String EMAIL_PATTERN = 
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@" +
            "[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);;
	private static Matcher matcher;
	  
	public EmailAttribute(String n) {
		super(n);
	}
	
	public EmailAttribute(String n, boolean userEditable) {
		super(n, userEditable);
	}
	
	public EmailAttribute(String n, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
	}
	
	
	public EmailAttribute(String n, String s) throws AttributeException {
		super(n);
		setValue(s);
	}
	
	public EmailAttribute(String n, String s, boolean userEditable) throws AttributeException {
		super(n, userEditable);
		setValue(s);
	}

	public EmailAttribute(String n, String s, boolean userEditable, boolean studentEditable) throws AttributeException {
		super(n, userEditable, studentEditable);
		setValue(s);
	}
	
	@Override
	public String getType() {
		return EMAIL_ATTRIBUTE;
	}

	@Override
	public String readValueFromString(String s) throws AttributeException {
		matcher = pattern.matcher(s);
		if (matcher.matches()){
			return s;
		}
		else{
			throw new AttributeException("malformed E-mail");
		}
	}

	@Override
	public void resetValue() {
		setValue("");
	}

	@Override
	public MathObjectAttribute clone(){
		try {
			MathObjectAttribute newEmail = new EmailAttribute(getName(), getValue());
			copyRootManagedFields(newEmail);
			return newEmail;
		} catch (AttributeException e) {
			// impossible error, values are taken out of an e-mail, so they will
			// be valid
			throw new RuntimeException(e);
		}
	}
}

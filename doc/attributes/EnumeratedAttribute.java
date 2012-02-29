/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */
package doc.attributes;

public class EnumeratedAttribute extends MathObjectAttribute<String>{
	
	private String[] possibleValues;
	
	public EnumeratedAttribute(String n, String[] possibleValues) {
		super(n);
		setValue("");
		setPossibleValues(possibleValues);
	}
	
	public EnumeratedAttribute(String n, boolean userEditable, String[] possibleValues) {
		super(n, userEditable);
		setValue("");
		setPossibleValues(possibleValues);
	}
	
	public EnumeratedAttribute(String n, boolean userEditable, boolean studentEditable, String[] possibleValues) {
		super(n, userEditable, studentEditable);
		setValue("");
		setPossibleValues(possibleValues);
	}
	
	public EnumeratedAttribute(String n, String s, String[] possibleValues) {
		super(n);
		setValue(s);
		setPossibleValues(possibleValues);
	}
	
	public EnumeratedAttribute(String n, String s, boolean userEditable, String[] possibleValues) {
		super(n, userEditable);
		setValue(s);
		setPossibleValues(possibleValues);
	}
	
	public EnumeratedAttribute(String n, String s, boolean userEditable,
			boolean studentEditable, String[] possibleValues) {
		super(n, userEditable, studentEditable);
		setValue(s);
		setPossibleValues(possibleValues);
	}
	
	@Override
	public String getType() {
		return ENUMERATED_ATTRIUBTE;
	}

	@Override
	public String readValueFromString(String s) throws AttributeException {
		for (String str : possibleValues){
			if (s.equals(str)){
				return s;
			}
		}
		throw new AttributeException("The value does not match one of the" +
				" enumerated values allowed for this attribute.");
	}

	@Override
	public void resetValue() {
		setValue("");
	}

	public String[] getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(String[] possibleValues) {
		this.possibleValues = possibleValues;
	}

}

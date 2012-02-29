/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc.attributes;


public class StringAttribute extends MathObjectAttribute<String>{
	
	public StringAttribute(String n) {
		super(n);
		setValue("");
	}
	
	public StringAttribute(String n, boolean userEditable) {
		super(n, userEditable);
		setValue("");
	}
	
	public StringAttribute(String n, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		setValue("");
	}
	
	public StringAttribute(String n, String s) {
		super(n);
		setValue(s);
	}
	
	public StringAttribute(String n, String s, boolean userEditable) {
		super(n, userEditable);
		setValue(s);
	}
	
	public StringAttribute(String n, String s, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		setValue(s);
	}
	
	
	@Override
	public String getType() {
		return STRING_ATTRIBUTE;
	}

	@Override
	public String readValueFromString(String s) throws AttributeException {
		return s;
	}

	@Override
	public void resetValue() {
		setValue("");
	}

}

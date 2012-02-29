/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc.attributes;


public class VariableNameAttribute extends MathObjectAttribute<String>{
	
	public VariableNameAttribute(String n) {
		super(n);
		setValue("");
	}
	
	public VariableNameAttribute(String n, boolean userEditable) {
		super(n, userEditable);
		setValue("");
	}
	
	public VariableNameAttribute(String n, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		setValue("");
	}
	
	public VariableNameAttribute(String n, String s) {
		super(n);
		setValue(s);
	}
	
	public VariableNameAttribute(String n, String s, boolean userEditable) {
		super(n, userEditable);
		setValue(s);
	}
	
	public VariableNameAttribute(String n, String s, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		setValue(s);
	}
	
	
	@Override
	public String getType() {
		return VAR_NAME_ATTRIBUTE;
	}

	@Override
	public String readValueFromString(String s) throws AttributeException {
		if ( s.length() != 1){
			throw new AttributeException("Variables must be one chracter in length.");
		}
		if ( ! Character.isLetter(s.charAt(0))){
			throw new AttributeException("Variable name must be a letter.");
		}
		else{
			return s;
		}
	}

	@Override
	public void resetValue() {
		setValue("");
	}

}

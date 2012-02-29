/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc.attributes;


public class BooleanAttribute extends MathObjectAttribute<Boolean> {
	
	public BooleanAttribute(String n){
		super(n);
	}
	
	public BooleanAttribute(String n, boolean b){
		super(n);
		// this needs to have the next line instead of calling a different
		// super constructor because of collision with superclass constructor
		// designed to just set an attribute name and userEditable variable
		setValue(b);
	}
	
	public BooleanAttribute(String n, boolean b, boolean userEditable){
		super(n, userEditable);
		// need this next line, see comment in above constructor
		setValue(b);
	}
	
	public BooleanAttribute(String n, boolean b, boolean userEditable, boolean studentEditable){
		super(n, b, userEditable, studentEditable);
	}

	@Override
	public String getType() {
		return BOOLEAN_ATTRIBUTE;
	}

	@Override
	public Boolean readValueFromString(String s) throws AttributeException {
		if (s.equals("true")){
			return true;
		}
		else if (s.equals("false")){
			return false;
		}
		throw new AttributeException(getName() + " must be 'true' or 'false' without single quotes");
	}

	@Override
	public void resetValue() {
		setValue(false);
	}
}

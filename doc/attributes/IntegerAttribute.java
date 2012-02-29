/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc.attributes;


public class IntegerAttribute extends MathObjectAttribute<Integer> {
	
	private int minimum, maximum;
	
	public static int LIMIT_NOT_SET = Integer.MAX_VALUE;
	
	public IntegerAttribute(String n, int min, int max) {
		super(n);
		minimum = min;
		maximum = max;
	}
	
	public IntegerAttribute(String n, int min, int max, boolean userEditable) {
		super(n, userEditable);
		minimum = min;
		maximum = max;
	}
	
	public IntegerAttribute(String n, int min, int max, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		minimum = min;
		maximum = max;
	}
	
	public IntegerAttribute(String n, int val, int min, int max){
		super(n);
		minimum = min;
		maximum = max;
		setValue(val);
	}

	public IntegerAttribute(String n, int val, int min, int max, boolean userEditable){
		super(n, userEditable);
		minimum = min;
		maximum = max;
		setValue(val);
	}

	public IntegerAttribute(String n, int val, int min, int max, boolean userEditable, boolean studentEditable){
		super(n, userEditable, studentEditable);
		minimum = min;
		maximum = max;
		setValue(val);
	}
	
	public IntegerAttribute(String n){
		super(n);
		minimum = LIMIT_NOT_SET;
		maximum = LIMIT_NOT_SET;
	}
	
	public IntegerAttribute(String n, boolean userEditable){
		super(n, userEditable);
		minimum = LIMIT_NOT_SET;
		maximum = LIMIT_NOT_SET;
	}
	
	public IntegerAttribute(String n, boolean userEditable, boolean studentEditable){
		super(n, userEditable, studentEditable);
		minimum = LIMIT_NOT_SET;
		maximum = LIMIT_NOT_SET;
	}
	
	public IntegerAttribute(String n, int val){
		super(n);
		minimum = LIMIT_NOT_SET;
		maximum = LIMIT_NOT_SET;
		setValue(val);
	}
	
	public IntegerAttribute(String n, int val, boolean userEditable){
		super(n, userEditable);
		minimum = LIMIT_NOT_SET;
		maximum = LIMIT_NOT_SET;
		setValue(val);
	}
	
	
	public IntegerAttribute(String n, int val, boolean userEditable, boolean studentEditable){
		super(n, userEditable, studentEditable);
		minimum = LIMIT_NOT_SET;
		maximum = LIMIT_NOT_SET;
		setValue(val);
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public int getMaximum() {
		return maximum;
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	public int getMinimum() {
		return minimum;
	}

	@Override
	public String getType() {
		return INTEGER_ATTRIBUTE;
	}

	@Override
	public Integer readValueFromString(String s) throws AttributeException {
		// TODO Auto-generated method stub
		try{
			int val = Integer.parseInt(s);
			if ( (val >= minimum && val <= maximum ) ||
					(minimum == LIMIT_NOT_SET && maximum == LIMIT_NOT_SET)){
				return val;
			}
			else{
				throw new AttributeException(getName() + " must be an integer in the range (" + 
					minimum + " - " + maximum + ")");
			}
		}catch(Exception e){
			throw new AttributeException(getName() + " must be an integer in the range (" + 
					minimum + " - " + maximum + ")");
		}
	}

	@Override
	public void resetValue() {
		setValue(0);		
	}
}

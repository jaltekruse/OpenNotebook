/*
   This file is part of OpenNotebook.

   OpenNotebook is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   OpenNotebook is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
    along with OpenNotebook.  If not, see <http://www.gnu.org/licenses/>.
 */

package doc.attributes;

import javax.xml.bind.ValidationException;

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

	public void validate(Integer val) throws AttributeException {
		if ( (val >= minimum && val <= maximum ) ||
				(minimum == LIMIT_NOT_SET && maximum == LIMIT_NOT_SET)){
			this.value = val;
		}
		else {
			throw invalidValueError();
		}
	}

	private AttributeException invalidValueError() {
		if ( minimum == LIMIT_NOT_SET ){
			if (maximum == LIMIT_NOT_SET){
				return new AttributeException(getName() + " must be an integer.");
			}
			else{
				return new AttributeException(getName() + " must be an integer less than " + maximum + ".");
			}
		}
		else if (maximum == LIMIT_NOT_SET){
			return new AttributeException(getName() + " must be an integer grater than " + minimum + ".");
		}
		return new AttributeException(getName() + " must be an integer in the range (" +
				minimum + " - " + maximum + ")");
	}

	@Override
	public Integer readValueFromString(String s) throws AttributeException {
		try{
			setValue(Integer.parseInt(s));
			return value;
		} catch(Exception e) {
			throw invalidValueError();
		}
	}

	@Override
	public void resetValue() {
		setValue(0);		
	}

	@Override
	public IntegerAttribute clone() {
		IntegerAttribute newInt = new IntegerAttribute(
				getName(), getValue(), getMinimum(), getMaximum());
		copyRootManagedFields(newInt);
		return newInt;
	}

}

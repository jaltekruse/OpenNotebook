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


public class DoubleAttribute extends MathObjectAttribute<Double> {

	private double minimum, maximum;
	
	public static final double LIMIT_NOT_SET = Double.MAX_VALUE;
	
	public DoubleAttribute(String n, double min, double max) {
		super(n);
		minimum = min;
		maximum = max;
	}
	
	public DoubleAttribute(String n, double min, double max, boolean userEditable) {
		super(n, userEditable);
		minimum = min;
		maximum = max;
	}
	
	public DoubleAttribute(String n, double min, double max,
			boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		minimum = min;
		maximum = max;
	}
	
	public DoubleAttribute(String n, double val, double min, double max) {
		super(n);
		setValue(val);
		minimum = min;
		maximum = max;
	}
	
	public DoubleAttribute(String n, double val, double min, double max, boolean userEditable) {
		super(n, userEditable);
		setValue(val);
		minimum = min;
		maximum = max;
	}
	
	public DoubleAttribute(String n, double val, double min,
			double max, boolean userEditable, boolean studentEditable) {
		super(n, userEditable, studentEditable);
		setValue(val);
		minimum = min;
		maximum = max;
	}
	
	public DoubleAttribute(String n){
		this(n, LIMIT_NOT_SET, LIMIT_NOT_SET);
	}
	
	public DoubleAttribute(String n, boolean userEditable){
		this(n, LIMIT_NOT_SET, LIMIT_NOT_SET, userEditable);
	}
	
	public DoubleAttribute(String n, boolean userEditable, boolean studentEditable){
		this(n, LIMIT_NOT_SET, LIMIT_NOT_SET, userEditable, studentEditable);
	}
	
	public DoubleAttribute(String n, double val){
		this(n, val, LIMIT_NOT_SET, LIMIT_NOT_SET);
	}
	
	public DoubleAttribute(String n, double val, boolean userEditable){
		this(n, val, LIMIT_NOT_SET, LIMIT_NOT_SET, userEditable);
	}
	
	public DoubleAttribute(String n, double val, boolean userEditable, boolean studentEditable){
		this(n, val, LIMIT_NOT_SET, LIMIT_NOT_SET, userEditable, studentEditable);
	}

	public void setMaximum(double maximum) {
		this.maximum = maximum;
	}

	public double getMaximum() {
		return maximum;
	}

	public void setMinimum(double minimum) {
		this.minimum = minimum;
	}

	public double getMinimum() {
		return minimum;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return DOUBLE_ATTRIBUTE;
	}

	@Override
	public Double readValueFromString(String s) throws AttributeException {
		// TODO Auto-generated method stub
		try{
			double val = Double.parseDouble(s);
			if ( (val >= minimum || minimum == LIMIT_NOT_SET ) &&
					( val <= maximum || maximum == LIMIT_NOT_SET) ){
				return val;
			}
			else{
				throw new AttributeException(getName() + " must be an decimal in the range (" + 
					minimum + " - " + maximum + ")");
			}
		}catch(Exception e){
			throw new AttributeException(getName() + " must be an decimal in the range (" + 
					minimum + " - " + maximum + ")");
		}
	}

	@Override
	public void resetValue() {
		setValue(0.0);
	}
}

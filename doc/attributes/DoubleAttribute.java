/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
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

/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package tree;

public class Decimal extends Number
{
	
	private double value;
	
	public Decimal(ExpressionParser p)
	{
		super(p);
	}
	
	public Decimal(double num)
	{
		value = num;
	}
	
	public double getValue()
	{
		return value;
	}
	
	public void setValue(double num)
	{
		value = num;
	}
	
	@Override
	public Number eval()
	{
		return this;
	}
	
	@Override
	public String toString()
	{
		return "" + value;
	}

	
	@Override
	public Expression multiply(Expression e) throws EvalException
	{
		if (e instanceof Decimal)
		{
			return new Decimal(value * ((Decimal)e).value);
		}
		else if (e instanceof Fraction)
		{
			return new Decimal(value * Fraction.frac2Dec(((Fraction)e)).value);
		}
		else if (e instanceof Identifier)
		{
			return multiply(((Identifier)e).getValue());
		}
//		else if (e instanceof Constant)
//		{
//			return multiply(((Constant)e).getValue());
//		}
		else
		{
			throw new EvalException("Cannot multiply a(n) " + this.getClass().getSimpleName()
					+ " and a(n) " + e.getClass().getSimpleName());
		}
	}

	@Override
	public Expression add(Expression e) throws EvalException
	{
		if (e instanceof Decimal)
		{
			return new Decimal(value + ((Decimal)e).value);
		}
		else if (e instanceof Fraction)
		{
			return new Decimal(value + Fraction.frac2Dec(((Fraction)e)).value);
		}
		else if (e instanceof Identifier)
		{
			return add(((Identifier)e).getValue());
		}
//		else if (e instanceof Constant)
//		{
//			return add(((Constant)e).getValue());
//		}
		else
		{
			throw new EvalException("Cannot add a(n) " + this.getClass().getSimpleName()
					+ " and a(n) " + e.getClass().getSimpleName());
		}
	}
	
	@Override
	public Expression subtract(Expression e) throws EvalException
	{
		if (e instanceof Decimal)
		{
			return new Decimal(value - ((Decimal)e).value);
		}
		else if (e instanceof Fraction)
		{
			return new Decimal(value - Fraction.frac2Dec(((Fraction)e)).value);
		}
		else if (e instanceof Identifier)
		{
			return subtract(((Identifier)e).getValue());
		}
//		else if (e instanceof Constant)
//		{
//			return subtract(((Constant)e).getValue());
//		}
		else
		{
			throw new EvalException("Cannot add a(n) " + this.getClass().getSimpleName()
					+ " and a(n) " + e.getClass().getSimpleName());
		}
	}

	@Override
	public Expression divide(Expression e) throws EvalException
	{
		if (e instanceof Decimal)
		{
			return new Decimal(value / ((Decimal)e).value);
		}
		else if (e instanceof Fraction)
		{
			return new Decimal(value / Fraction.frac2Dec(((Fraction)e)).value);
		}
		else if (e instanceof Identifier)
		{
			return divide(((Identifier)e).getValue());
		}
//		else if (e instanceof Constant)
//		{
//			return divide(((Constant)e).getValue());
//		}
		else
		{
			throw new EvalException("Cannot add a(n) " + this.getClass().getSimpleName()
					+ " and a(n) " + e.getClass().getSimpleName());
		}
	}

	@Override
	public Expression power(Expression e) throws EvalException
	{
		if (e instanceof Decimal)
		{
			return new Decimal(Math.pow(value, ((Decimal)e).value));
		}
		else if (e instanceof Fraction)
		{
			return new Decimal((Math.pow(value, Fraction.frac2Dec(((Fraction)e)).value)));
		}
		else if (e instanceof Identifier)
		{
			return divide(((Identifier)e).getValue());
		}
//		else if (e instanceof Constant)
//		{
//			return divide(((Constant)e).getValue());
//		}
		else
		{
			throw new EvalException("Cannot add a(n) " + this.getClass().getSimpleName()
					+ " and a(n) " + e.getClass().getSimpleName());
		}
	}

	@Override
	public Expression sin() {
		// TODO Auto-generated method stub
		return new Decimal(Math.sin(convertAngle2Rad(value)));
	}

	@Override
	public Expression cos() {
		// TODO Auto-generated method stub
		return new Decimal(Math.cos(convertAngle2Rad(value)));
	}

	@Override
	public Expression tan() {
		// TODO Auto-generated method stub
		
		return new Decimal(Math.tan(convertAngle2Rad(value)));
	}

	@Override
	public Expression invSin() {
		// TODO Need to make this adjust for current TrigUnit setting
		// i.e.   Rad, Deg, Grad
		return new Decimal(Math.asin(value));
	}

	@Override
	public Expression invCos() {
		// TODO Need to make this adjust for current TrigUnit setting
		// i.e.   Rad, Deg, Grad
		return new Decimal(Math.acos(value));
	}

	@Override
	public Expression invTan() {
		// TODO Need to make this adjust for current TrigUnit setting
		// i.e.   Rad, Deg, Grad
		return new Decimal(Math.acos(value));
	}
	
	/**
	 * Takes an angle in the current units and converts it to radians, for use
	 * with the standard JAVA math functions involving trig.
	 * @param angle - an angle value in the current unit type
	 * @return the value of the angle in radians, if Rad passed in, passed back
	 */
	private double convertAngle2Rad(double angle) {
		// TODO Auto-generated method stub
		if(getParser().getAngleUnits()== 2){
			angle *= (Math.PI/180);
		}
		else if(getParser().getAngleUnits() == 3){
			angle *= (Math.PI/200);
		}
		return angle;
	}
	
	/**
	 * Takes an angle in radians and converts it to the needed angleUnits.
	 * @param angle - in radians
	 * @return angle in the current angleUnits
	 */
	private double convertAngleFromRad(double angle) {
		// TODO Auto-generated method stub
		if(getParser().getAngleUnits() == 2){
			angle *= (180/Math.PI);
		}
		else if(getParser().getAngleUnits() == 3){
			angle *= (200/Math.PI);
		}
		return angle;
	}

	@Override
	public Expression neg() {
		// TODO Auto-generated method stub
		return new Decimal(-1 * value);
	}

	@Override
	public Decimal toDec() throws EvalException {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public Expression squareRoot() {
		// TODO Auto-generated method stub
		return new Decimal(Math.sqrt(value));
	}

	@Override
	public Decimal log() throws EvalException {
		// TODO Auto-generated method stub
		return new Decimal(Math.log10(value));
	}

	@Override
	public Decimal natLog() throws EvalException {
		// TODO Auto-generated method stub
		return new Decimal(Math.log(value));
	}
	
	@Override
	public Expression absoluteValue() throws EvalException{
		return new Decimal(Math.abs(value));
	}
}

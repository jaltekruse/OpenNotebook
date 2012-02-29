/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package tree;

import java.util.ArrayList;

public class Fraction extends Number{
	private int d;
	private int n;
	
	public Fraction(int n, int d){
		setDenominator(d);
		setNumerator(n);
	}

	public void setNumerator(int numerator) {
		n = numerator;
	}

	public int getNumerator() {
		return n;
	}

	public void setDenominator(int denominator) {
		d = denominator;
	}

	public int getDenominator() {
		return d;
	}

	@Override
	public Expression eval() throws EvalException {
		return this;
	}
	
	@Override
	public String toString(){
		if (d == 1){
			return "" + n;
		}
		return "(" + n + "/" + d + ")";
	}
	
	public static Fraction Dec2Frac(Decimal d){
		double temp = d.getValue();
		double remainder;
		int counter = 1;
		do{
			counter--;
			remainder = (temp/Math.pow(10, counter))%1;
		}while(remainder != 0);
		return new Fraction((int) Math.round(temp * Math.pow(10, -1 * counter)), 
				(int) Math.round((Math.pow(10, -1* counter))));
	}
	
	public static Decimal frac2Dec(Fraction f){
		return new Decimal(((double)f.n) / f.d);
	}
	
	public Fraction reduce(){
		if (d == 1){
			return this;
		}
		ArrayList<Integer> nList = new ArrayList<Integer>();
		ArrayList<Integer> dList = new ArrayList<Integer>();
		nList.add(n);
		dList.add(d);
		for (int i = Math.abs(n/2); i > 0; i--){
			if (n % i == 0){
				nList.add(i);
			}
		}
		for (int i = Math.abs(d/2); i > 0; i--){
			if (d % i == 0){
				dList.add(i);
			}
		}
		for (int i : nList){
			if (findInList(i, dList)){
				return new Fraction(n/i, d/i);
			}
		}
		return this;
	}
	
	private boolean findInList(int i, ArrayList<Integer> list){
		for (int a: list){
			if (a == i){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Expression multiply(Expression e) throws EvalException
	{
		if (e instanceof Decimal)
		{
			return ((Decimal)e).multiply(frac2Dec(this));
		}
		else if (e instanceof Fraction)
		{
			return new Fraction(this.n * ((Fraction)e).n, this.d * ((Fraction)e).d);
		}
		else if (e instanceof Identifier)
		{
			return multiply(((Identifier)e).getValue());
		}
		else if (e instanceof Constant)
		{
			return multiply(((Constant)e).getValue());
		}
		else
		{
			throw new EvalException("Cannot multiply a(n) " + this.getClass()
					+ " and a(n) " + e.getClass());
		}
	}
	
	@Override
	public Expression add(Expression e) throws EvalException
	{
		if (e instanceof Decimal)
		{
			return ((Decimal)e).add(frac2Dec(this));
		}
		else if (e instanceof Fraction)
		{
			return new Fraction(n * ((Fraction)e).d + ((Fraction)e).n * d, d * ((Fraction)e).d);
		}
		else if (e instanceof Identifier)
		{
			return add(((Identifier)e).getValue());
		}
		else if (e instanceof Constant)
		{
			return add(((Constant)e).getValue());
		}
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
			return new Decimal(frac2Dec(this).getValue() - ((Decimal)e).getValue());
		}
		else if (e instanceof Fraction)
		{
			return new Fraction(n * ((Fraction)e).d - ((Fraction)e).n * d, d * ((Fraction)e).d);
		}
		else if (e instanceof Identifier)
		{
			return subtract(((Identifier)e).getValue());
		}
		else if (e instanceof Constant)
		{
			return subtract(((Constant)e).getValue());
		}
		else
		{
			throw new EvalException("Cannot subtract a(n) " + this.getClass().getName()
					+ " and a(n) " + e.getClass().getSimpleName());
		}
	}

	@Override
	public Expression divide(Expression e) throws EvalException
	{
		if (e instanceof Decimal)
		{
			return new Decimal(frac2Dec(this).getValue() / ((Decimal)e).getValue());
		}
		else if (e instanceof Fraction)
		{
			return new Fraction(this.n * ((Fraction)e).d, this.d * ((Fraction)e).n);
		}
		else if (e instanceof Identifier)
		{
			return divide(((Identifier)e).getValue());
		}
		else if (e instanceof Constant)
		{
			return divide(((Constant)e).getValue());
		}
		else
		{
			throw new EvalException("Cannot divide a(n) " + this.getClass().getSimpleName()
					+ " by a(n) " + e.getClass().getSimpleName());
		}
	}

	@Override
	public Expression power(Expression e) throws EvalException
	{
		if (e instanceof Decimal)
		{
			return new Decimal(Math.pow(frac2Dec(this).getValue(), ((Decimal)e).getValue()));
		}
		else if (e instanceof Fraction)
		{
			if (((Fraction)e).d == 1){
				if (n < 100 && d < 100 && ((Fraction)e).n < 5 && ((Fraction)e).n > -1){
					return new Fraction((int) Math.pow(n, ((Fraction)e).n), (int) Math.pow(d, ((Fraction)e).n));
				}
				else if (n < 100 && d < 100 && ((Fraction)e).n > -5 && ((Fraction)e).n < 0){
					return new Fraction(1, (int) Math.pow(n, -1 * ((Fraction)e).n));
				}
			}
			return new Decimal(Math.pow(frac2Dec(this).getValue(), frac2Dec(((Fraction)e)).getValue()));
		}
		else if (e instanceof Identifier)
		{
			return power(((Identifier)e).getValue());
		}
		else if (e instanceof Constant)
		{
			return power(((Constant)e).getValue());
		}
		else
		{
			throw new EvalException("Cannot take a(n) " + this.getClass().getSimpleName()
					+ " to a(n) " + e.getClass().getSimpleName() + " power");
		}
	}

	@Override
	public Expression sin() throws EvalException {
		// TODO Auto-generated method stub
		return toDec().sin();
	}

	@Override
	public Expression cos() throws EvalException {
		// TODO Auto-generated method stub
		return toDec().cos();
	}

	@Override
	public Expression tan() throws EvalException {
		// TODO Auto-generated method stub
		return toDec().tan();
	}

	@Override
	public Expression invSin() throws EvalException {
		// TODO Need to make this adjust for current TrigUnit setting
		// i.e.   Rad, Deg, Grad
		return toDec().invSin();
	}

	@Override
	public Expression invCos() throws EvalException {
		// TODO Need to make this adjust for current TrigUnit setting
		// i.e.   Rad, Deg, Grad
		return toDec().invCos();
	}

	@Override
	public Expression invTan() throws EvalException {
		// TODO Need to make this adjust for current TrigUnit setting
		// i.e.   Rad, Deg, Grad
		return toDec().invTan();
	}

	@Override
	public Expression neg() {
		// TODO Auto-generated method stub
		return new Fraction(-1 * n, d);
	}

	@Override
	public Decimal toDec() throws EvalException {
		// TODO Auto-generated method stub
		return frac2Dec(this);
	}

	@Override
	public Expression assign(Expression v) throws EvalException {
		// TODO Auto-generated method stub
		throw new EvalException("Cannot assign a value to a Fraction");
	}

	@Override
	public Expression squareRoot() throws EvalException {
		// TODO Auto-generated method stub
		if (Math.sqrt(n)%1 == 0 && Math.sqrt(d)%1 == 0){
			return new Fraction((int) Math.sqrt(n), (int) Math.sqrt(d));
		}
		else{
			return new Decimal(Math.sqrt(toDec().getValue()));
		}
	}

	@Override
	public Expression log() throws EvalException {
		// TODO Auto-generated method stub
		return new Decimal(frac2Dec(this).log().getValue());
	}

	@Override
	public Expression natLog() throws EvalException {
		// TODO Auto-generated method stub
		return new Decimal(frac2Dec(this).natLog().getValue());
	}
	
	@Override
	public Expression absoluteValue() throws EvalException{
		return new Fraction(Math.abs(n), d);
	}
}

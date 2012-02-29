/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package tree;

/**
 * Number is the superclass of everything that has a known Value.
 * This includes Decimal, Fraction, Matrix(if this ends up being able
 * to contain Variables, this will extend from something else), Irrational,
 * and anything else that could be contained in the tree that will represent the
 * Coefficient of a Term.
 * @author jason
 *
 */
public abstract class Number extends Expression{
	
	public Number(ExpressionParser p){
		super(p);
	}
	
	public Number(){}
	
	@Override
	public boolean allChildrenFilled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canHoldChildren() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean needsChildToLeft(){
		return false;
	}
	
	public boolean needsChildToRight(){
		return false;
	}
	
	public int getArity(){
		return 0;
	}

}

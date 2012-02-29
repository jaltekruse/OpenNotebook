/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package tree;

public abstract class Expression {
	
	private Expression parent;//the parent of this expression in the tree
	protected Operator op;//the operator associated with this expression
	
	//the parser currently in use, stores the calculator state
	//used for calculation of trig functions and finding variable values
	private static ExpressionParser parser;
	
	public Expression(ExpressionParser p){
		parser = p;
	}
	
	public Expression(){}
	
	public Expression(Operator o){
		op = o;
	}
	
	public Operator getOperator()
	{
		return op;
	}
	
	public void setOperator(Operator o)
	{
		op = o;
	}
	
	public abstract boolean allChildrenFilled();
	
	public abstract boolean canHoldChildren();
	
	public abstract boolean needsChildToLeft();
	
	public abstract boolean needsChildToRight();
	
	/**
	 * Returns the number of arguments taken by a function.
	 * @return - the number of arguments
	 */
	public abstract int getArity();
	
	public boolean isContainerOp()
	{
		return (op == Operator.PAREN || op == Operator.BRACKET || op == Operator.CURL_BRAC);
	}
	
	public void setParent(Expression e){
		parent = e;
	}
	
	public abstract Expression eval() throws EvalException;
	
	public boolean hasParent(){
		if (parent != null){
			return true;
		}
		else return false;
	}
	
	public Expression findParentTermRoot(){
		if (hasParent()){
			if (getParent() instanceof Expression){
				if ((getParent()).getOperator() != Operator.ADD
						&& (getParent()).getOperator() != Operator.SUBTRACT
						&& (getParent()).getOperator() != Operator.EQ
						&& (getParent()).getOperator() != Operator.ASSIGN){
					return getParent().findParentTermRoot();
				}
			}
		}
		
		// base case to stop recursion
		return this;
	}
	
	public Expression findLeft(){
		return null;
	}
	
	public Expression findRight(){
		return null;
	}
	
	public boolean isLeftChild(){
		if (hasParent()){
			if (getParent() instanceof BinExpression){
				if (((BinExpression)getParent()).getLeftChild().equals(this)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean onLeftSideEquation(){
		if (hasParent()){
			if (getParent() instanceof BinExpression){
				if (((BinExpression)getParent()).getOperator() == Operator.ASSIGN){
					if (isLeftChild()){
						return true;
					}
				}
			}
			return getParent().onLeftSideEquation();
		}
		return false;
	}
	
	public boolean onRightSideEquation(){
		if (hasParent()){
			if (getParent() instanceof BinExpression){
				if (((BinExpression)getParent()).getOperator() == Operator.ASSIGN){
					if (isRightChild()){
						return true;
					}
				}
			}
			return getParent().onRightSideEquation();
		}
		return false;
	}
	
	public boolean isRightChild(){
		if (hasParent()){
			if (getParent() instanceof BinExpression){
				if (((BinExpression)getParent()).getRightChild().equals(this)){
					return true;
				}
			}
		}
		return false;
	}
	
	public ExpressionParser getParser(){
		return parser;
	}
	
	public Expression getParent(){
		return parent;
	}
	
	public Expression integrate(double a, double b,  String indVar, String depVar) throws EvalException{
		double lastY, currY, aveY, result = 0;
		int numTraps = 500;
		parser.getVarList().setVarVal(indVar, (new Decimal(a)));
		eval();
		lastY = parser.getVarList().getVarVal(depVar).toDec().getValue();
		double xStep = (b - a) / numTraps;

		for(int i = 0; i < numTraps; i++){
			parser.getVarList().updateVarVal(indVar, xStep);
			eval();
			currY = parser.getVarList().getVarVal(depVar).toDec().getValue();
			aveY = (lastY + currY) / 2;
			result += aveY * xStep;
			lastY = currY;
		}
		return new Decimal(result);
	}
	
	public Expression deriveAtPt(double d, String indVar, String depVar){
		Number tempX = new Decimal(0);
		Number tempY = new Decimal(0);
		tempX = parser.getVarList().getVarVal(indVar);
		tempY = parser.getVarList().getVarVal(depVar);
		double xChange = .0000001;
		try {
			parser.getVarList().setVarVal(indVar, (new Decimal(d)));
			eval();
			double firstY = parser.getVarList().getVarVal(depVar).toDec().getValue();
			parser.getVarList().setVarVal(indVar, (new Decimal(d + xChange)));
			eval();
			double secondY = parser.getVarList().getVarVal(depVar).toDec().getValue();
			
			parser.getVarList().setVarVal(indVar, tempX);
			parser.getVarList().setVarVal(depVar, tempY);
			return new Decimal( (secondY - firstY) /  xChange);
			
		} catch (EvalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public Expression squareRoot() throws EvalException{
		throw new EvalException("Cannot take the square root of a(n) " + this.getClass().getSimpleName());
	}
	
	public Expression sin() throws EvalException{
		throw new EvalException("Cannot take the sine of a(n) " + this.getClass().getSimpleName());
	}
	
	public Expression cos() throws EvalException{
		throw new EvalException("Cannot take the cosine of a(n) " + this.getClass().getSimpleName());
	}
	
	public Expression tan() throws EvalException{
		throw new EvalException("Cannot take the tangent of a(n) " + this.getClass().getSimpleName());
	}
	
	public Expression invSin() throws EvalException{
		throw new EvalException("Cannot take the inverse sine of a(n) " + this.getClass().getSimpleName());
	}

	public Expression invCos() throws EvalException{
		throw new EvalException("Cannot take the inverse cosine of a(n) " + this.getClass().getSimpleName());
	}
	
	public Expression invTan() throws EvalException{
		throw new EvalException("Cannot take the inverse tangent of a(n) " + this.getClass().getSimpleName());
	}

	public Expression neg() throws EvalException{
		throw new EvalException("Cannot negate a(n) " + this.getClass().getSimpleName());
	}
	
	public Expression log() throws EvalException{
		throw new EvalException("Cannot take the log base 10 of a(n) " + this.getClass().getSimpleName());
	}
	
	public Expression natLog() throws EvalException{
		throw new EvalException("Cannot take the natural log of a(n) " + this.getClass().getSimpleName());
	}
	
	public Expression absoluteValue() throws EvalException{
		throw new EvalException("Cannot take the absolute value of a(n) " + this.getClass().getSimpleName());
	}

	public Expression add(Expression e) throws EvalException
	{
		throw new EvalException("Cannot add a(n) " + this.getClass().getSimpleName() + " and a(n) " + e.getClass().getSimpleName());
	}

	public Expression subtract(Expression e) throws EvalException
	{
		throw new EvalException("Cannot subtract a(n) " + this.getClass().getSimpleName() + " and a(n) " + e.getClass().getSimpleName());
	}
	
	public Expression multiply(Expression e) throws EvalException
	{
		throw new EvalException("Cannot multiply a(n) " + this.getClass().getSimpleName() + " and a(n) " + e.getClass().getSimpleName());
	}
	
	public Expression divide(Expression e) throws EvalException
	{
		throw new EvalException("Cannot divide a(n) " + this.getClass().getSimpleName() + " by a(n) " + e.getClass().getSimpleName());
	}

	public Expression power(Expression e) throws EvalException
	{
		throw new EvalException("Cannot raise a(n) " + this.getClass().getSimpleName() + " to a(n) " + e.getClass().getSimpleName() + " power");
	}

	public Expression assign(Expression e) throws EvalException{
		throw new EvalException("Cannot assign a(n) " + e.getClass().getSimpleName() + " to a(n) " + this.getClass().getSimpleName());
	}
	
	public Decimal toDec() throws EvalException{
		throw new EvalException("Cannot convert a(n) " + this.getClass().getSimpleName() + " to a decimal");
	}
	
}

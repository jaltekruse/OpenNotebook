/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package tree;

/**
 * A <code>BinExpression</code> represents an expression in the form of
 * Operand, operator, Operand. Examples include + - / * ^
 * 
 * This does not include operations entered in the from of operator(op1, op2).
 * The addition of such operations is still forthcoming, but they will be in the same
 * class as operations with many operands, and possibly combined with unary operators.
 * These types of operations do no have the same properties as a BinExpression, which
 * can be associative, transitive, etc. These properties are necessary to make the
 * expression manipulation system function properly, which is why they are in their own class.
 * 
 * @author jason altekruse
 */

public class BinExpression extends Expression {
	
	private Expression leftChild; //the left operand
	private Expression rightChild; //the right operand
	
	/**
	 * The default constructor, this is rarely used as the operator code is
	 * usually known at the time of creation of a BinExpression object.
	 */
	public BinExpression(){
		
	}
	
	/**
	 * A constructor that takes an Operator enum.
	 *  
	 * @param oper - the enum to describe which operation this object represents
	 */
	public BinExpression(Operator oper){
		super(oper);
	}
	
	/* (non-Javadoc)
	 * @see tree.Expression#setOp(tree.Operator)
	 */
	@Override
	public void setOperator(Operator oper) {
		// has access to op from superclass Expression
		op = oper;
	}
	
	@Override
	public Operator getOperator(){
		// has access to op from superclass Expression
		return op;
	}

	/**
	 * Set the left child of the Expression. Also sets the child's
	 * parent to this BinExpression.
	 * 
	 * @param child - the new child expression
	 */
	public void setLeftChild(Expression child){
		leftChild = child;
		leftChild.setParent(this);
	}
	
	/**
	 * Check if a given Expression is a child of this BinExpression.
	 * 
	 * @param e - the Expression to check
	 * @return - true if the given Expression is a child of this BinExpression
	 */
	public boolean isChild(Expression e){
		if (e == leftChild || e == rightChild ){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Check if a given Expression is the right child of this BinExpression.
	 * @param e - the Expression to check
	 * @return - true if the Expression is the right child
	 */
	public boolean isRightChild(Expression e){
		if (e == rightChild){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Check if a given Expression is the left child of this BinExpression.
	 * @param e - the expression to check
	 * @return - true if the Expression is the left child
	 */
	public boolean isLeftChild(Expression e){
		if (e == leftChild){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Get the left child.
	 * @return - the left child of this expression.
	 */
	public Expression getLeftChild(){
		return leftChild;
	}
	
	/**
	 * Set the right child of the expression. Also sets the child's
	 * parent to this BinExpression.
	 * 
	 * @param child - the new child expression
	 */
	public void setRightChild(Expression child){
		rightChild = child;
		rightChild.setParent(this);
	}
	
	/**
	 * Get the left child.
	 * @return - the left child of this expression.
	 */
	public Expression getRightChild(){
		return rightChild;
	}
	
	
	@Override
	public Expression eval() throws EvalException{
		
		Expression leftVal = null;
		if (leftChild != null){
			if (op == Operator.ASSIGN && leftChild instanceof Identifier){
				leftVal = leftChild;
			}
			else{
				leftVal = leftChild.eval();
			}
		}
		else{
			throw new EvalException("binary operator without a left child");
		}
		Expression rightVal = null;
		if (rightChild != null){
			rightVal = rightChild.eval();
		}
		else
		{
			throw new EvalException("binary operator without a right child");
		}
		//System.out.println(leftVal.toString() + "  " + op.name() + "  " + rightVal.toString());
		switch(op){
			case ADD:
				return leftVal.add(rightVal);
			case SUBTRACT:
				return leftVal.subtract(rightVal);
			case MULTIPLY:
			case IMP_MULT:
				return leftVal.multiply(rightVal);
			case DIVIDE:
			case DIV_BAR:
				return leftVal.divide(rightVal);
			case POWER:
			case SUPERSCRIPT_POWER:
				return leftVal.power(rightVal);
			case ASSIGN:
				return leftVal.assign(rightVal);
		}
		throw new EvalException("unrecognized operation");
	}

	@Override
	public String toString(){
		String result = new String();
		if (getLeftChild() != null)
			result += leftChild.toString();
		if (getOperator() != null)
			switch(op){
			case ADD:
				result += "+";
				break;
			case SUBTRACT:
				result += "-";
				break;
			case MULTIPLY:
				result += "*";
				break;
			case DIVIDE:
				result += "/";
				break;
			case POWER:
				result += "^";
				break;
			case ASSIGN:
				result += "=";
				break;
		}
		else{
			result += " no op ";
		}
		if (getRightChild() != null)
			result += rightChild.toString();
		return result;
	}

	@Override
	public boolean allChildrenFilled() {
		// TODO Auto-generated method stub
		if ( leftChild != null && rightChild != null){
			return true;
		}
		return false;
	}

	@Override
	public boolean canHoldChildren() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public int getArity(){
		return 2;
	}

	@Override
	public boolean needsChildToLeft() {
		// TODO Auto-generated method stub
		return leftChild == null;
	}

	@Override
	public boolean needsChildToRight() {
		// TODO Auto-generated method stub
		return rightChild == null;
	}
}

/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package tree;

public enum Operator {
	
	ASSIGN(1, "="),		GT(1, null),		LT(1, null),		EQ(1, null),		NOT(1, null),
	AND(1, null),		OR(1, null),		NE(1, null),		GTE(1, null),		LTE(1, null),
	
	ADD(2, "+"),		SUBTRACT(2, "-"),
	MULTIPLY(3, "*"), 	IMP_MULT(3, null),	DIVIDE(3, "/"),		DIV_BAR(3, null), 
	
	POWER(4, "^"),		SUPERSCRIPT_POWER(2, null),
	SQUARE(4, null),	CUBE(4, null),		SQRT(4, "sqrt"),	SQRT_SYMBOL(4, null),
	
	SIN(5, "sin"),		COS(5, "cos"),
	TAN(5, "tan"),		INV_COS(5, "cos-1"),	INV_SIN(5, "sin-1"),	INV_TAN(5, "tan-1"),	LN(5, "ln"),
	LOG(5, "log"),		ABS(5, "abs"),		FLOOR(5, "floor"),	CEILING(5, "ceil"),	INT(5, "int"),
	ROUND(5, "round"),	NEG(5, "neg"),		FACT(5, "!"), INTEGRAL(5, "integral"),
	PAREN(6, null),	BRACKET(6, null),	CURL_BRAC(6, null),
	SOLVE(7, "solve")
	;
	
	//these are not in any particular order
	private static String[] requirePrevNum = {"!", "^", "*", "/", "-", "+", "^-1"};
	
	
	private final int prec;
	private final String symbol;
	
	Operator(int precedence, String s){
		prec = precedence;
		symbol = s;
	}

	public int getPrec() {
		return prec;
	}
	
	public boolean isUnaryPost(){
		if(this == FACT)
		{
			return true;
		}
		return false;
	}
	
	/**Takes a string representing an operator, determines if it requires a
	 *previous num by searching the hard coded list of operators.
	 *
	 *@param s - string representing an operator
	 *@return true if it does require previous, else false
	 */
	public static boolean requiresPrevious(String s){
		for(int i = 0; i < requirePrevNum.length; i++){
			if (s.equals(requirePrevNum[i])){
				return true;
			}
		}
		return false;
	}

	public String getSymbol() {
		return symbol;
	}
	
	public boolean isMultiply(){
		if (this == IMP_MULT){
			return true;
		}
		else if (this == MULTIPLY){
			return true;
		}
		return false;
		
	}
	
	public boolean isDivision(){
		if (this == DIVIDE){
			return true;
		}
		else if (this == DIV_BAR){
			return true;
		}
		return false;
	}
	
	public boolean isPower(){
		if (this == POWER){
			return true;
		}
		else if (this == SUPERSCRIPT_POWER){
			return true;
		}
		return false;
	}
	
	public boolean isSquareRoot(){
		if (this == SQRT){
			return true;
		}
		else if (this == SQRT_SYMBOL){
			return true;
		}
		return false;
	}
}

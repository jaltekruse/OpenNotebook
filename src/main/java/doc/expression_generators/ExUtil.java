package doc.expression_generators;

import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import expression.Expression;
import expression.Identifier;
import expression.Node;
import expression.NodeException;
import expression.Number;
import expression.Operator;
import expression.VarList;

public class ExUtil {

	private static Random rand = new Random();;

	public static String USE_INTEGERS = "integers";
	public static String USE_DECIALS = "deciamls";
	public static String USE_VARIABLES = "variables";

	public static String INTEGER_ANSWER = "intAnswer";
	public static String FRACTION_ANSWER = "fractionAnswer";
	public static String DECIMAL_ANSWER = "decimalAnswer";

	public static String ADDITION = "addition";
	public static String MULTIPLICATION = "multiplication";
	public static String DIVISION = "division";
	public static String SUBTRACTION = "subtraction";
	public static String NEGATION = "negation";
	public static String ABSOLUTE_VALUE = "absolute value";

	public static String[] vars = { "x", "y", "z", "a", "b", "c", "d", "s", "t", "w", "v", "m", "n", "j", "k", "l"};
	
	public static String[] generateRandomExpressions(){
		String[] ops = { ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION };
		String[] vars = { "x", "y", "z", "a", "b", "c", "d", "s", "t", "w", "v", "m", "n", "j", "k", "l"};
		int numTrials = 10;

		String[] expressions = new String[numTrials];
		int numZeros = 0;
		int numNegatives = 0;
		int minNumOps = 2;
		int maxNumOps = 5;
		int maxAbsVal = 20;
		int minGeneratedVal = 1;
		int maxGeneratedVal = 10;
		int numOps;

		for (int j = 0; j < numTrials; j++){
			numOps = randomInt(minNumOps, maxNumOps, false);
			Node n = randomExpression(ops, vars, numOps, maxAbsVal, minGeneratedVal, 
					maxGeneratedVal, true, false, false, true);
			try {
				expressions[j] = n.toStringRepresentation();
				if ( ((Number)n.numericSimplify()).getValue() == 0 ){
					numZeros++;
				}
				if ( ((Number)n.numericSimplify()).getValue() < 0){
					numNegatives++;
				}
			} catch (NodeException e) {
				e.printStackTrace();
			}
		}
		// TODO - add a logger
//		System.out.println( numZeros + " out of " + numTrials + " trials evaluated to 0.");
//		System.out.println(numNegatives + " out of " + numTrials + " trials evaluated to a negative.");
		return expressions;
	}

	public static Node randomExpression( String[] ops, String[] vars, int numOps,
			double maxAbsVal, int minNumVal, int maxNumVal, 
			boolean excludeZero, boolean subtractNegatives, boolean addNegatives, boolean includeFractions ){
		Node n;
		n = new Number( randomInt( minNumVal, maxNumVal, excludeZero));

		for (int i = 0; i < numOps; i++){
			n = addRandomOp( n, ops, vars, minNumVal, maxNumVal, maxAbsVal, excludeZero, subtractNegatives, addNegatives);
		}
		return n;
	}
	
	public static Node randomEquation( Operator[] ops, int numOps, int nimNumVal, int maxNumVal){
		return null;
	}

	public static String randomVarName(){
		return vars[rand.nextInt(vars.length - 1)];
	}
	
	public static Identifier randomVar(){
		try {
			return new Identifier(vars[rand.nextInt(vars.length - 1)]);
		} catch (NodeException e) {
			// stupid error, will not happen with list of vars being pulled from
		}
		return null;
	}
	
	public static Expression flipSides(Expression ex){
		try {
			ex = ex.cloneNode();
		} catch (NodeException e) {
			e.printStackTrace();
		}
		Collections.shuffle(ex.getChildren());
		return ex;
	}

	public static Vector<String> randomUniqueVarNames(int num){
		Vector<String> varNames = new Vector<String>();
		String varName;
		while ( varNames.size() < num){
			varName = randomVarName();
			if ( ! varNames.contains(varName)){
				varNames.add(varName);
			}
		}
		return varNames;
	}

	/**
	 * Generate a pair of numbers that add to an easy to deal with whole number
	 * ( a multiple of 10, 50, 100, 1000,...) under the given number.
	 * 
	 * @param maxSum - the greatest sum that should result
	 * @return - the two numbers
	 */
	public static double[] pairOfCleanAddingNumbers(int maxSum){
		int[] easyNumbers = { 10, 50, 100, 1000, 10000 };
		int[][] easyDivisors = { {1, 2} , {1, 5, 10}, 
				{1, 5, 10, 25}, {100, 200, 250 },
				{1000, 2000, 2500}};
		int index, sub_index, sum = Integer.MAX_VALUE;
		double[] nums = new double[2];
		while (sum > maxSum)
		{
			index = ExUtil.randomInt(0, easyNumbers.length - 1, false);
			sub_index = ExUtil.randomInt(0, easyDivisors[index].length - 1, false);
			sum = easyNumbers[ index ] * ExUtil.randomInt(1, 4, false);
			nums[0] = ExUtil.randomInt(1, sum / easyDivisors[index][sub_index] - 1, true)
					* easyDivisors[index][sub_index];
			nums[1] = sum - nums[0];
		}
		return nums;
	}
	
	/**
	 * Generate a pair of numbers that multiply to an easy to deal with whole number
	 * ( a multiple of 10, 50, 100, 1000,...) under the given number.
	 * 
	 * @param maxProduct - the greatest product that should result
	 * @return - the two numbers
	 */
	public static double[] pairOfCleanFactors(int maxProduct){
		int[] easyNumbers = { 10, 50, 100, 1000, 10000 };
		int[][] easyDivisors = { {2} , {2, 5, 10}, 
				{ 5, 10, 25}, {100, 200, 250 },
				{1000, 2000, 2500}};
		int index, sub_index, product = Integer.MAX_VALUE;
		double[] nums = new double[2];
		while (product > maxProduct)
		{
			index = ExUtil.randomInt(0, easyNumbers.length - 1, false);
			sub_index = ExUtil.randomInt(0, easyDivisors[index].length - 1, false);
			product = easyNumbers[ index ] * randomInt(1, 4, false);
			nums[0] = easyDivisors[index][sub_index];
			nums[1] = product / nums[0];
		}
		return nums;
	}
	
	/**
	 * Randomly adds parenthesis to an expression composed of additions or multiplications.
	 * The result of the operation does not change, due to the Associative property of both of
	 * the operators, but this method is useful for creating exercises to teach those concepts.
	 * 
	 * @param ex - the expression
	 * @param minNumParens - the minimum number of parenthesis to add
	 * @param maxNumParens - the maximum number of parenthesis to add
	 * @return - the modified expression
	 */
	public static Node randomlyAddParenthesis(Expression ex, int minNumParens,  int maxNumParens){
		Vector<Node> terms = new Vector<Node>();
		if ( ex.getOperator() instanceof Operator.Addition){
			terms = ex.splitOnAddition();
			int numParensToAdd = randomInt(minNumParens, maxNumParens, true);
			int unusedTermsLeft = terms.size();
			int tempIndex;
			Expression tempEx;
			while ( unusedTermsLeft > 2 && numParensToAdd > 0){
				// use size - 2 because a term and it's successive term will be used to
				// create a new expression with parenthesis
				tempIndex = randomInt(0, terms.size() - 2, false);
				// remove from the same index twice, because everything is shifted with the
				// first move
				tempEx = new Expression(new Operator.Addition(), terms.remove(tempIndex),
						terms.remove(tempIndex));
				tempEx.setDisplayParentheses(true);
				terms.add(tempEx);
				unusedTermsLeft--;
				numParensToAdd--;
			}
			return Expression.staggerAddition(terms);
		}
		else if ( ex.getOperator() instanceof Operator.Multiplication){
			terms = ex.splitOnMultiplication();
			int numParensToAdd = randomInt(minNumParens, maxNumParens, true);
			int unusedTermsLeft = terms.size();
			int tempIndex;
			Expression tempEx;
			while ( unusedTermsLeft > 2 && numParensToAdd > 0){
				tempIndex = randomInt(0, terms.size() - 2, false);
				tempEx = new Expression(new Operator.Multiplication(),
						terms.remove(tempIndex), terms.remove(tempIndex));
				tempEx.setDisplayParentheses(true);
				terms.add(tempEx);
				unusedTermsLeft--;
				numParensToAdd--;
			}
			return Expression.staggerMultiplication(terms);
		}
		return ex;
	}

	public static boolean randomBoolean(){
		return rand.nextBoolean();
	}
	
	public static Node randomPolynomial(int maxDegree, int minCoefficient, int maxCoefficient, int minNumberTerms,
			int maxNumberTerms, VarList variables){
		Node n = null;
		int numTerms = randomInt( minNumberTerms, maxNumberTerms, true);

		for (int i = 0; i < numTerms; i++){
			// add a new term
		}

		return n;
	}
	
	public static Expression randomLinearExpression(String varName, int minCoeff, int maxCoeff){
		Expression ex = null;
		if( randomBoolean()){
			ex = new Expression(new Operator.Addition(), randomTerm(1, varName, minCoeff, maxCoeff),
					new Number( randomInt(minCoeff, maxCoeff, true)));
		}
		else{
			ex = new Expression(new Operator.Subtraction(), randomTerm(1, varName, minCoeff, maxCoeff),
					new Number( randomInt(minCoeff, maxCoeff, true)));
		}
		return ex;
	}
	
	public static Expression randomAdditionOrSubtraction(int minVal, int maxVal){
		Number num1 = new Number(randomInt(minVal, maxVal, true)), num2 = new Number(randomInt(minVal, maxVal, true));
		Expression sumNode = null;
		if ( ExUtil.randomBoolean()){
			sumNode = new Expression(new Operator.Addition(), num1, num2);
		}
		else{
			sumNode = new Expression(new Operator.Subtraction(), num1, num2);
		}
		return sumNode;
	}

	public static Node randomTerm(int maxDegree, int minCoefficient, int maxCoefficient, VarList variables){
		Vector<Node> parts = new Vector<Node>();
		// add the coefficient
		parts.add(new Number(randomInt(minCoefficient, maxCoefficient, true)));
		// keep a list of the variables that have already been used
		Vector<String> usedVars = new Vector<String>();
		int degree = 0;
		while( degree < maxDegree && variables.size() > usedVars.size()){
			
		}
        return null;
	}

	public static Node randomTerm(int degree, String var, int minCoefficient, int maxCoefficient){
		Number num = new Number(randomInt(minCoefficient, maxCoefficient, true));
		if ( degree == 0){
			return num;
		}
		Expression ex = new Expression(new Operator.Multiplication());
		ex.getChildren().add(num);
		try {
			if ( degree == 1){

				ex.getChildren().add(new Identifier(var));
				return ex;
			}
			else{
				ex.getChildren().add(new Expression(new Operator.Exponent(),
						new Identifier(var), new Number(degree)));
				return ex;
			}
		} catch (NodeException e) {
			throw new RuntimeException(e);
		}
	}

	public static double[] getFactors( double d){

		double[] factors = new double[0];
		int index = 0;

		for ( double num = 2; num < d / 2 + 1; d++){
			if ( d / num == 0){
				index = factors.length;
				System.arraycopy(factors, 0, new double[index + 1], 0, index);
				factors[index] = num;
			}
		}
		return factors;
	}

	public static int getPrec( Operator o){

		if ( o instanceof Operator.Addition || o instanceof Operator.Subtraction){
			return 1;
		}

		else if ( o instanceof Operator.Division || o instanceof Operator.Multiplication){
			return 2;
		}
		else return 0;
	}

	public static Node addChild( Expression ex, Node n ){

		if ( n instanceof Number){
			Number numChild = (Number) n;
			if ( numChild.getValue() < 0)
			{// if the number is a negative add parenthesis around it
				n.setDisplayParentheses(true);
			}
		}

		if ( ex.getOperator() instanceof Operator.BinaryOperator){
			if ( ex.getChildren().size() == 0 || ex.getChild(0) == null)
			{ // there are no children added yet (this is the first operand of a binary operator) 

				if ( n instanceof Expression ){
					Expression childEx = (Expression) n;
					if ( getPrec( childEx.getOperator() ) < getPrec( ex.getOperator() ) ||
							( getPrec( childEx.getOperator() ) == getPrec( ex.getOperator() ) &&
							( (childEx.getOperator() instanceof Operator.Subtraction ||
							   childEx.getOperator() instanceof Operator.Division ))))
					{ // the expression has a greater precedence than the
						// new child, it needs parenthesis
						// or the operator is not associative
						n.setDisplayParentheses(true);
					}
				}
				ex.getChildren().add(n);
				return ex;
			}
			else
			{// there is already one operand, this is the second one added

				if ( n instanceof Expression ){
					Expression childEx = (Expression) n;
					if ( getPrec( childEx.getOperator() ) < getPrec( ex.getOperator() ) ||
							( getPrec( childEx.getOperator() ) == getPrec( ex.getOperator() ) &&
							( (childEx.getOperator() instanceof Operator.Subtraction ||
							   childEx.getOperator() instanceof Operator.Division ))))
					{ // the child that was already added to the expression has a lesser precedence than the
						// new child, the first expression needs parenthesis
						n.setDisplayParentheses(true);
					}
				}
				ex.getChildren().add(n);
				return ex;

			}
		}
		else if ( ex.getOperator() instanceof Operator.Function){
			if ( n instanceof Expression ){
				Expression childEx = (Expression) n;
				if ( getPrec( childEx.getOperator() ) < getPrec( ex.getOperator() ) )
				{ // the expression has a greater precedence than the
					// new child, it needs parenthesis
					n.setDisplayParentheses(true);
				}
			}
			ex.getChildren().add(n);
			return ex;
		}
		return ex;
	}
	
	public static Node randomlyStaggerOperation(Operator o, Node... nodes){
		Node newNode = nodes[0];
		for ( int i = 1; i < nodes.length; i++){
			newNode = addNodeOnRandomSide(newNode, nodes[i], o);
		}
		return newNode;
	}

	public static Node addRandomOp(Node n, String[] ops, String[] vars, int min, int max,
			double maxAbsVal, boolean excludeZero, boolean subtractNegatives, boolean addNegatives){

		int opIndex = rand.nextInt( ops.length );
		String opCode = ops[opIndex];
		Node newChild = new Number( randomInt(min, max, excludeZero) );
		Operator op = null;
		Number newNum;
		Expression newEx;
		double expressionVal = 0;
		boolean numberTooBig = false;

		try {
			//			System.out.println(n.toStringRepresentation());
			expressionVal = ((Number)n.numericSimplify()).getValue();
		} catch (NodeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if ( Math.abs(expressionVal) > maxAbsVal){
			numberTooBig = true;
		}

		if ( opCode.equals(DIVISION)){

			op = new Operator.Division();
			newEx = new Expression( op );
			if ( ! (newChild instanceof Number) ){
				// the new child being added is not a number, it will not have to be adjusted
				// to keep the answer clean
				return addNodeOnRandomSide(n, newChild, op);
			}

			else if ( expressionVal == 0){
				newNum = (Number) newChild; 
				do{ 
					newChild = new Number( randomInt(min, max, excludeZero));
				} while( newNum.getValue() == 0);
				return new Expression(op, n, newChild);
			}
			else if ( isPrime( expressionVal ) || ( rand.nextBoolean() && ! numberTooBig) )
			{// the expression evaluates to a prime number, so it must be the divisor
				// or the expression was randomly selected to be the divisor when it wasn't prime
				return addRandomOp(n, ops, vars, min, max, maxAbsVal, excludeZero, subtractNegatives, addNegatives);
				// had problem with these next two lines, gave up for now and just adding another operation
//				newChild = new Number( expressionVal * randomInt(min, max, excludeZero));
//				return new Expression(op, newChild, n);
			}
			else
			{// the expression evaluates to a non-prime number,and was randomly chosen to be the dividend
				// need to find a divisor
				if ( numberTooBig ){
					return addRandomOp(n, ops, vars, min, max, maxAbsVal, excludeZero, subtractNegatives, addNegatives);
				}
				double[] factors = getFactors(expressionVal);
				int factorIndex = rand.nextInt( factors.length );
				newChild = new Number( factors[factorIndex]);
				return new Expression(op, n, newChild);
			}
		}
		else if ( opCode.equals(ADDITION)){
			op = new Operator.Addition();

			if ( ! (newChild instanceof Number) ){
				// the new child being added is not a number, it will not have to be adjusted
				// to keep the answer clean
				return addNodeOnRandomSide(n, newChild, op);
			}

			if ( ! addNegatives)
			{// negative numbers are not supposed to be added, generate new positive number

				if ( max < 0)
				{// the settings contradict one another, the user wanted only negative numbers generated
					//do nothing
				}
				else{
					// the minimum is below zero, so exclude those values between 0 and the minimum while finding a new number
					int tempMin = 0;
					newNum = (Number) newChild;
					if ( min > 0)
						tempMin = min;
					while ( newNum.getValue() < 0){
						newNum = new Number( randomInt(tempMin, max, excludeZero) );
					}
				}
			}
		}
		else if ( opCode.equals(SUBTRACTION)){
			op = new Operator.Subtraction();

			if ( ! (newChild instanceof Number) ){
				// the new child being added is not a number, it will not have to be adjusted
				// to keep the answer clean
				if ( n instanceof Expression && ((Expression)n).getOperator() instanceof Operator.Negation)
				{// do not subtract a negative
					// MIGHT CAUSE INFINITE RECURSION, BE CAREFUL
					return addRandomOp(n, ops, vars, min, max, maxAbsVal, excludeZero,
							subtractNegatives, addNegatives);
				}
				return addNodeOnRandomSide(n, newChild, op);
			}

			if ( ! subtractNegatives)
			{// negative numbers are not supposed to be subtracted, generate new positive number
				if ( max < 0)
				{// the settings contradict one another, the user wanted only negative numbers generated
					//do nothing, ignore the value of subtractNegatives
				}
				else{
					// the minimum is below zero, so exclude those values between 0 and the minimum while finding a new number
					int tempMin = 0;
					newNum = (Number) newChild;
					if ( min > 0)
						tempMin = min;
					while ( newNum.getValue() < 0){
						newNum = new Number( randomInt(tempMin, max, excludeZero) );
					}
				}
			}
		}
		else if ( opCode.equals(MULTIPLICATION)){
			op = new Operator.Multiplication();
			newEx = new Expression( op );

			if ( ! (newChild instanceof Number) ){
				// the new child being added is not a number, it will not have to be adjusted
				// to keep the answer clean
				return addNodeOnRandomSide(n, newChild, op);
			}

		}
		else if ( opCode.equals(NEGATION)){
			if ( n instanceof Expression && ((Expression)n).getOperator() instanceof Operator.Negation
					|| ( n instanceof Number && ((Number)n).getValue() < 0 ) )
			{// do not add a negation on top of another negation, generate another random operation
				// THIS WILL CAUSE INFINITE RECURSION IF THE ONLY OPERATOR IS NEGATION!!
				return addRandomOp(n, ops, vars, min, max, maxAbsVal, excludeZero, subtractNegatives, addNegatives);
			}
			return new Expression(new Operator.Negation(), true, n);
		}
		else{
			throw new RuntimeException("unknown op");
		}

		return addNodeOnRandomSide(n, newChild, op);
	}
	
	public static Node addNodeOnRandomSide(Node oldNode, Node newNode, Operator o){
		Expression ex = new Expression(o);
		if (randomBoolean()){
			addChild(ex, newNode);
			addChild(ex, oldNode);
		}
		else{
			addChild(ex, oldNode);
			addChild(ex, newNode);
		}
		return ex;
	}

	public static boolean isPrime( double d){

		if (d % 1 != 0){
			return true;
		}

		for (double i = 2 ; i < (d / 2) % 1 ; i++ ){
			if ( d / i == 0){
				return false;
			}
		}

		return true;
	}

	public Node randomFraction(int min, int max, boolean allowImproper, boolean allowZero){
		if ( min > max){
			// min must be smaller
			return null;
		}
		int denom = rand.nextInt(max - min) + min;
		Number denominator = new Number( denom );

		return null;
	}

	public static Node randomEquation(){

		return null;
	}

	public static Expression getExpression(String s){
		if (s.equals(ADDITION)){
			return new Expression(new Operator.Addition());
		}
		else if (s.equals(MULTIPLICATION)){
			return new Expression(new Operator.Multiplication());
		}
		else if (s.equals(DIVISION)){
			return new Expression(new Operator.Division());
		}
		else if (s.equals(SUBTRACTION)){
			return new Expression(new Operator.Subtraction());
		}
		return null;
	}

	/**
	 * Return a random number between a and b. A boolean flag is included if
	 * zero is to be excluded.
	 * 
	 * @param a - start of random number generation
	 * @param b - last number possible
	 * @param excludeZero
	 * @return
	 */
	public static int randomInt(int a, int b, boolean excludeZero){
		double randVal = rand.nextDouble() * ( b - a );
		int randNum = a + (int) Math.round(randVal);
		if ( a < 0 && b > 0 && excludeZero){
			while ( randNum == 0){
				randNum = ( (int) (a + rand.nextDouble() * ( b - a )) );
			}
		}
		return randNum;

	}

	public static double randomDecimal(double a, double b, double round){
		double randNum = a + rand.nextDouble() * ( b - a);
		return randNum - randNum % round;
	}
}

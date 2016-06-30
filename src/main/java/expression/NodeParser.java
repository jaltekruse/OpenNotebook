package expression;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

/**
 * Instances of this class parse {@code String} objects
 * into {@code Node} objects using the {@code parseNode(String)} 
 * method. They hold settings for specific methods of doing so.
 * 
 * @author Killian Kvalvik
 *
 */
public class NodeParser {
	
	private boolean allowEmptyArguments = false;
	
	private boolean allowLongIdentifiers = false;
	
	private boolean identifiersAsVariables = false;
	
	private List<String> functions =
			Arrays.asList("abs", "log", "sin", "cos", "tan",
					"sqrt", "cbrt", "root", "ln", "randomInt", "random");
	
	private List<String> nonFunctionalIdentifiers = 
			Arrays.asList();
	
	private List<String> delimiters = 
			Arrays.asList(",");
	
	private List<String> identifiers;
	
	private List<String> equalityOps = 
		Arrays.asList("=");
	
	private List<String> additionOps = 
			Arrays.asList("+", "-");
	
	private List<String> multiplicationOps =
			Arrays.asList("*", "/", "", "-"); // unary negation taken out
	
	private List<String> exponentOps =
			Arrays.asList("^");
	
	private List<String> binaryOps =
			Arrays.asList("+", "*", "", "/", "^"); // no thought, just split at index
	
	private List<String> operatorStrings =
			Arrays.asList("+", "-", "*", "/", "-", "^");
	
	private List<String> unsplittableStrings;
	
	private int lowestPrecedence = 0;
	private int highestPrecedence = 4;
	
	/** List of open brackets. */
	private List<String> openBrackets =
			Arrays.asList("(", "[", "{");
	
	/** List of closed brackets. */
	private List<String> closeBrackets =
			Arrays.asList(")", "]", "}");
	
	private String exEmptyString = "Empty string or missing argument";
	
	public NodeParser() {
		identifiers = new Vector<>();
		identifiers.addAll(functions);
		identifiers.addAll(nonFunctionalIdentifiers);
		
		unsplittableStrings = new Vector<>();
		unsplittableStrings.addAll(operatorStrings);
		unsplittableStrings.addAll(delimiters);
	}
	
	public boolean longIdentifiers() {
		return allowLongIdentifiers;
	}

	public void allowLongIdentifiers(boolean allowLongIdentifiers) {
		this.allowLongIdentifiers = allowLongIdentifiers;
	}
	
	public boolean emptyArguments() {
		return allowEmptyArguments;
	}

	public void allowEmptyArguments(boolean allowEmptyArguments) {
		this.allowEmptyArguments = allowEmptyArguments;
	}
	
	public boolean identifiersAsVariables() {
		return identifiersAsVariables();
	}
	
	public void treatIdentifiersAsVariables(boolean b) {
		identifiersAsVariables = b;
	}

	public List<String> getOperators(int level) {
		switch (level) {
			case 0:
				return equalityOps;
			case 1:
				return additionOps;
			case 2:
				return multiplicationOps;
			case 3:
				return exponentOps;
			case 4:
				return functions;
			
		}
		return null;
	}
	
	public Node parseNode(String expression) throws NodeException {
		expression = format(expression);
		expression = checkParens(expression);
		return parse(expression);
	}
	
	public String checkParens(String s){
		int parenMatcher = 0;
		for (int i = 0; i < s.length(); i++){
			if (s.charAt(i) == '('){
				parenMatcher++;
			}
			else if  (s.charAt(i) == ')'){
				parenMatcher--;
			}
		}
		if ( parenMatcher == 1){
			return s + ")";
		}
		else{
			return s;
		}
	}
	
	private String format(String expression) {
		return expression.replaceAll("\\s", "");
	}
	
	private Node parse(String expression) throws NodeException {
		Node n = parse(expression, lowestPrecedence, expression.length() - 1);;
		return n;
	}
	
	private Node parse(String expression, int initialPrecedence, int offset) throws NodeException{
		try {
			return rawParse(expression, initialPrecedence, offset);
		} catch (NodeException e) {
			if (e.getMessage().equals(exEmptyString) && allowEmptyArguments)
				return new EmptyValue();
			else
				throw e;
		}
	}

	private String unshell(String expression) {
		int depth = 0;
		for (int i = 0 ; i < expression.length() ; i++) {
			String c = expression.charAt(i) + "";
			if (openBrackets.contains(c))
				depth++;
			if (closeBrackets.contains(c))
				depth--;
			if (depth == 0) {
				if (i == 0) {
					return expression;
				} else if (i == (expression.length() - 1)) {
					return expression.substring(1, expression.length() - 1);
				} else {
					return expression;
				}
			}
		}
		return expression;
	}

	private Node rawParse(String expression, int initialPrecedence, int offset) throws NodeException {
		if (expression.equals("")) // string is whitespace
			throw new NodeException(exEmptyString);
		
		
		String unshell = unshell(expression);
		if (!unshell.equals(expression)) {
		// expression is surrounded by parens
			Node newNode = parseNode(unshell);
			newNode.setDisplayParentheses(true);
			return newNode;
		}
		
//		if (openBrackets.contains(expression.charAt(0) + "") &&
//				closeBrackets.contains(expression.charAt(expression.length() - 1) + ""))
//			// expression is surrounded by parens
//			//this allows a paren to open and a bracket to close, should be redone
//			return parseNode(expression.substring(1, expression.length() - 1));
		
		for (int precedence = initialPrecedence ; precedence <= highestPrecedence ; precedence++) {
			List<String> operators = getOperators(precedence);
			
			int[] lastPositions = new int[operators.size()];
			
			for (int i = 0 ; i < operators.size() ; i++) {		// get the last position of each operator
				if (precedence == initialPrecedence)
					lastPositions[i] = seekFromLast(expression, operators.get(i), offset);
				else
					lastPositions[i] = seekFromLast(expression, operators.get(i));
			}
			
			int max = 0;
			int maxPos = -1;
			int maxRightPos = -1;
			int rightPos = -1;
			for (int i = 0 ; i < lastPositions.length ; i++) {
				if (lastPositions[i] == -1)
					continue;
				rightPos = lastPositions[i] + operators.get(i).length();
				if ((rightPos > maxRightPos) || 
					((rightPos == maxRightPos) && (operators.get(max).length() < operators.get(i).length())))
				{
					maxPos = lastPositions[i];
					maxRightPos = rightPos;
					max = i;
				}
			}
			// maxPos now holds the position furthest to the right
			// of any operator in this precedence level
			
			if (maxPos == -1)
				continue;		// no operators of this precedence level
			
			String symbol = operators.get(max);
			int index = maxPos;
			
			if (equalityOps.contains(symbol)){
				Vector<Node> children = splitAtIndex(expression, index, symbol.length());
				Operator o = null;
				if (symbol.equals("="))
					o = new Operator.Equals();
				return new Expression(o, children);
			}
			
			if (binaryOps.contains(symbol)) {
				Vector<Node> children = splitAtIndex(expression, index, symbol.length());
				Operator o = null;
				if (symbol.equals("+"))
					o = new Operator.Addition();
				if (symbol.equals("*")) {
					o = new Operator.Multiplication
					(Operator.Multiplication.Format.ASTERISK);
				}
				if (symbol.equals("")) {
					o = new Operator.Multiplication
					(Operator.Multiplication.Format.IMPLICIT);
				}
				if (symbol.equals("/"))
					o = new Operator.Division();
				if (symbol.equals("^"))
					o = new Operator.Exponent();
				
				return new Expression(o, children);
			}
			if (functions.contains(symbol)) {
				if (index == 0) {
					Vector<String> stringChildren = new Vector<>();
					String args = expression.substring(symbol.length() + 1, expression.length() - 1);
					stringChildren.addAll(splitArgs(args, ","));
					Vector<Node> children = new Vector<>();
					for (String s : stringChildren)
						children.add(parse(s));					
					Operator o = null;
					
					if (symbol.equals("log")) {
						if (children.size() == 1)
							o = new Operator.Logarithm();
						else
							o = new Operator.LogBase();
					}
					else if (symbol.equals("random")){
						o = new Operator.RandomGenerator();
					}
					else if (symbol.equalsIgnoreCase("randomInt")){
						o = new Operator.RandomIntGenerator();
					}
					else if (symbol.equalsIgnoreCase("abs"))
						o = new Operator.AbsoluteValue();
					else if (symbol.equalsIgnoreCase("ln"))
						o = new Operator.NaturalLog();
					else if (symbol.equals("sin"))
						o = new Operator.Sine();
					else if (symbol.equals("cos"))
						o = new Operator.Cosine();
					else if (symbol.equals("tan"))
						o = new Operator.Tangent();
					else if (symbol.equals("sqrt"))
						o = new Operator.SquareRoot();
					else if (symbol.equals("cbrt"))
						o = new Operator.CubeRoot();
					else if (symbol.equals("root"))
						o = new Operator.Root();
					if ( children.size() == 1){
						children.get(0).setDisplayParentheses(true);
					}
					return new Expression(o, children);
				}
			}
			
			if (symbol.equals("-")) {
				if (operators == additionOps) {  // subtraction
					boolean missingArg = false;
					Vector<Node> children = null;
					try {
						children = splitAtIndex(expression, index, symbol.length());
						for (Node c : children) {
							if (c.isEmpty()) {
								missingArg = true;
								break;
							}
						}
					} catch (NodeException e) {
						if (e.getMessage().equals(exEmptyString))
							missingArg = true;
						else
							throw e;
					}
					if (missingArg) {
						return parse(expression, precedence, index - 1);
					} else {
						return new Expression(new Operator.Subtraction(), children);
					}
				} else {	// negation
					if (index != 0) {
						return parse(expression, precedence, index - 1);
					}
					else if ( expression.length() > 1 &&
							Character.isDigit(expression.charAt(index + 1))){
						return parseValue(expression);
					}
					else{
						return new Expression( new Operator.Negation(), parseNode(expression.substring(1)));
					}
				}
			}
		}
		return parseValue(expression);
	}
	
	private Value parseValue(String expression) throws NodeException {
		Value v = Value.parseValue(expression);
		if ((v instanceof Identifier) && identifiersAsVariables) {
			v = new Variable(expression);
		}
		return v;
	}
	
	private Vector<String> splitArgs(String string, String delim) {
		Vector<String> args = new Vector<>();
		int depth = 0;
		int last = 0;
		for (int i = 0 ; i <= string.length() - delim.length() ; i++) { 
			// the equals sign in "i <= string.length()" took me half an hour to debug
			if (openBrackets.contains(string.charAt(i) + ""))
				depth++;
			if (closeBrackets.contains(string.charAt(i) + ""))
				depth--;
			if (depth == 0) {
				if (string.substring(i, i + delim.length()).equals(delim)) {
					args.add(string.substring(last, i));
					i += delim.length();
					last = i;
				}
			}
		}
		args.add(string.substring(last));
		return args;
	}

	private int seekFromLast(String expression, String symbol) {
		return seekFromLast(expression, symbol, expression.length() - 1);
	}

	private int seekFromLast(String expression, String symbol, int endpoint) {
		// no idea how this method works anymore
		int depth = 0;
		boolean lastWasNumber = false;
		boolean lastWasLetter = false;
		boolean lastWasEnd = false;  // possibly no longer necessary
		boolean thisIsNumber;
		boolean thisIsLetter;
		for (int i = endpoint ; i >= 0 ; i--) {
			if (openBrackets.contains(expression.charAt(i) + ""))
				depth++;
			if (closeBrackets.contains(expression.charAt(i) + ""))
				depth--;
			if ((i == 0) && (symbol.length() == 0))
				break;
			if (symbol.length() == 0) {
				thisIsNumber = Number.isNumeric(expression.charAt(i-1));
				thisIsLetter = Identifier.isValidChar(expression.charAt(i-1));
				lastWasNumber = Number.isNumeric(expression.charAt(i));
				lastWasLetter = Identifier.isValidChar(expression.charAt(i));
			} else {
				thisIsNumber = Number.isNumeric(expression.charAt(i));
				thisIsLetter = Identifier.isValidChar(expression.charAt(i));
			}
			if ((depth == 0) && (i <= expression.length() - symbol.length())) {
				if (!(lastWasEnd && (symbol.length() == 0)) 
						&& !((i == 0) && (symbol.length() == 0)) // because this is just stupid
						&& !(lastWasNumber && thisIsNumber) 
						&& !(lastWasLetter && thisIsLetter && allowLongIdentifiers)) {
					boolean inIdentifier = false;
					for (String id : identifiers) {
						if (!symbol.contains(id)) {
							inIdentifier = inIdentifier || indexIn(expression, i, id);
							if (functions.contains(id)) {
								int j = i - id.length();
								if (j >= 0) {
									inIdentifier = 
											inIdentifier || 
											expression.substring(j, j+id.length()).equals(id);
								}
							}
						}
						if (inIdentifier)
							break;
					}
					if (symbol.length() == 0) {
						for (String op: unsplittableStrings) {
							if (!symbol.contains(op)) {
								inIdentifier = inIdentifier || indexIn(expression, i, op);
								int j = i - op.length();
								if (j >= 0) {
									inIdentifier = 
											inIdentifier || expression.substring(j, j+op.length()).equals(op);
								}
								int end = i + op.length() + 1;
								if (symbol.length() == 0)
									end--;					// HACK
								// also, this whole part may need to be deleted
								if (end <= expression.length()) {
									inIdentifier = 
											inIdentifier || expression.substring(end - op.length(), end).equals(op);
								}
							}
							if (inIdentifier)
								break;
						}
					}
					if (!inIdentifier) {
						if (symbol.equals(expression.substring(i, i + symbol.length())))
							return i;
					}
				}
			}
			
			lastWasEnd = false;
			lastWasNumber = Number.isNumeric(expression.charAt(i));
			lastWasLetter = Identifier.isValidChar(expression.charAt(i));			
		}
		return -1;
	}
	
	private boolean indexIn(String expression, int index, String identifier) {
		for (int j = Math.max(0, index - identifier.length() + 1) 
				; (j < (expression.length() - identifier.length())) && (j <= index) ; j++) {
			if (expression.substring(j, j + identifier.length()).equals(identifier))
				return true;
		}
		return false;
	}
	
	private Vector<Node> splitAtIndex(String expression, int index, int symbolLength) throws NodeException {
		Vector<Node> children = new Vector<>();
		children.add(parseNode(expression.substring(0, index)));
		children.add(parseNode(expression.substring(index + symbolLength)));
		return children;
	}
}

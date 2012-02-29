package expression;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 * The {@code Expression} class represents the combination
 * of children {@code Node} objects with an {@code Operator} 
 * to create a nontrivial expression.
 * 
 * @author Killian Kvalvik
 *
 */
public class Expression extends Node {

	private Operator o;
	private Vector<Node> children;

	public Expression(Operator o, Vector<Node> children) {
		setOperator(o);
		setChildren(children);
	}
	
	public Expression(Operator o, Node... children) {
		Vector<Node> v = new Vector<Node>();
		for (Node c : children) {
			v.add(c);
		}
		setOperator(o);
		setChildren(v);
	}
	
	/**
	 * Constructor for an expression, includes flag for displaying parenthesis.
	 * 
	 * @param o - the operator for the new expression
	 * @param displayParens - if parenthesis should be displayed
	 * @param children - children of the expression
	 */
	public Expression(Operator o, boolean displayParens, Node... children) {
		this(o, children);
		setDisplayParentheses(displayParens);
	}
	
	@Override
	public boolean equals(Object other) {
		if ((other == null) || !(other instanceof Expression))
			return false;
		Expression e = (Expression) other;
		return (o.equals(e.getOperator())) && 
				(children.equals(e.getChildren()));
	}

	@Override
	public int hashCode() {
		return o.hashCode() + children.hashCode();
	}
	
	@Override
	public String toStringRepresentation() throws NodeException {
		if ( ! (o instanceof Operator.Equals) && displayParentheses())
			return parenthetize(o.toString(children));
		else
			return o.toString(children);
	}
	
	@Override
	public Expression cloneNode() throws NodeException {
		Vector<Node> clone = new Vector<Node>();
		for (Node child : children)
			clone.add(child.cloneNode());
		Expression e = new Expression(o.clone(), clone);
		e.setDisplayParentheses(displayParentheses());
		return e;
	}

	@Override
	public Node replace(Identifier identifier, Node node) {
		Vector<Node> newChildren = new Vector<Node>();
		for (Node c : children){
			newChildren.add(c.replace(identifier, node));
		}
		Expression newEx = new Expression(o, newChildren);
		if (this.displayParentheses()){
			newEx.setDisplayParentheses(true);
		}
		return newEx;
	}

	@Override
	public Node numericSimplify() throws NodeException {
		Vector<Node> simplifiedChildren = new Vector<Node>();
		Vector<Number> numbers = new Vector<Number>();
		Node simplified;
		boolean totallyNumeric = true;
		for (Node c : children) {
			simplified = c.numericSimplify();
			simplifiedChildren.add(simplified);
			if (simplified instanceof Number) {
				numbers.add((Number) simplified.cloneNode());
			} else {
				totallyNumeric = false;
			}
		}
		if (totallyNumeric && ! ( o instanceof Operator.Equals ) ){
			return o.evaluate(numbers);
		}
		else{
			return new Expression(o.clone(), simplifiedChildren);
		}
	}
	
	@Override
	public Node smartNumericSimplify() throws NodeException {
		Vector<Node> addends = splitOnAddition();
		if (addends.size() > 1) {
			Vector<Node> simplified = new Vector<Node>();
			for (Node addend : addends) {
				simplified.add(addend.smartNumericSimplify());
			}
			Vector<Node> numbers = new Vector<Node>();
			Node addend;
			for (int i = simplified.size() - 1 ; i >= 0 ; i--) {
				addend = simplified.get(i);
				if (addend instanceof Number) {
					numbers.add(addend);
					simplified.remove(i);
				}
			}
			if (numbers.size() > 0)
				simplified.add(staggerAddition(numbers).numericSimplify());
			if (simplified.contains(Number.get(0))) {
				simplified.remove(Number.get(0));
				return staggerAddition(simplified).smartNumericSimplify();
			}
			Node finalNode = staggerAddition(simplified);
			if (finalNode instanceof Expression) {
				Expression ex = (Expression) finalNode;
				if (ex.getOperator() instanceof Operator.Addition) {
					Node second = ex.getChildren().get(1);
					if (second instanceof Number) {
						Number n = (Number) second;
						if (n.isNegative()) {
							finalNode = new Expression(new Operator.Subtraction(), 
									ex.getChildren().firstElement(), n.negate());
						}
					}
				}
			}
			return finalNode;
		}
		
		Vector<Node> factors = splitOnMultiplication();
		Number one = Number.get(1);
		while (factors.contains(one))
			factors.remove(one);
		if (factors.size() > 1) {
			Vector<Node> simplified = new Vector<Node>();
			for (Node factor : factors) {
				simplified.add(factor.smartNumericSimplify());
			}
			Vector<Node> numbers = new Vector<Node>();
			Node addend;
			for (int i = simplified.size() - 1 ; i >= 0 ; i--) {
				addend = simplified.get(i);
				if (addend instanceof Number) {
					numbers.add(addend);
					simplified.remove(i);
				}
			}
			simplified.add(staggerMultiplication(numbers).numericSimplify());
			if (simplified.contains(Number.get(0))) {
				return Number.get(0);
			}
			if (simplified.contains(Number.get(1))) {
				simplified.remove(Number.get(1));
				return staggerMultiplication(simplified).smartNumericSimplify();
			}
			return staggerMultiplication(simplified);
		}
		
		if (o instanceof Operator.Exponent) {
			Node exponent = children.get(1).smartNumericSimplify();
			Node base = children.get(0).smartNumericSimplify();
			if (exponent.equals(Number.get(0)))
				return Number.get(1);
			if (exponent.equals(Number.get(1)))
				return base;
			if (base.equals(Number.get(0)))
				return Number.get(0);
			if (base.equals(Number.get(1)))
				return Number.get(1);
			return new Expression(new Operator.Exponent(), base, exponent);
		}
		
		Vector<Node> simplifiedChildren = new Vector<Node>();
		for (Node child : children)
			simplifiedChildren.add(child.smartNumericSimplify());
		return (new Expression(o.clone(), simplifiedChildren)).numericSimplify();
	}

	@Override
	public Node collectLikeTerms() throws NodeException {
		Vector<Node> split = splitOnAddition();
		Vector<Node> terms = new Vector<Node>();
		if (split.size() == 1) {
			Vector<Node> args = new Vector<Node>();
			for (Node c : children)
				args.add(c.collectLikeTerms());
			return new Expression(o.clone(), args);
		}
		
		for (Node n : split)
			terms.add(n.collectLikeTerms());  // THIS is the recursion part
		
		Vector<Vector<Node>> expandedTerms = new Vector<Vector<Node>>();
		Vector<Node> factors;
		Vector<Node> collectedFactors;
		for (Node term : terms) {
			factors = term.splitOnMultiplication();
			collectedFactors = new Vector<Node>();
			for (Node f : factors) {
				collectedFactors.add(f.cloneNode());
			}
			expandedTerms.add(collectedFactors);
		}
		
		Vector<Node> additionChildren = new Vector<Node>();
		
		Node likeTerm = findDuplicate(expandedTerms);
		while (likeTerm != null) {
			Vector<Vector<Node>> commonTerms = new Vector<Vector<Node>>();
			Vector<Node> expandedTerm;
			Vector<Integer> termsToRemove = new Vector<Integer>();
			for (int i = 0 ; i < terms.size() ; i++) {
				expandedTerm = expandedTerms.get(i);
				boolean addedThisTerm = false;
				for (Node factor : expandedTerm) {
					if (factor.equals(likeTerm)) {
						commonTerms.add(expandedTerm);
						addedThisTerm = true;
						break;
					}
				}
				if (addedThisTerm) {
					termsToRemove.add(i);
				}
			}
			for (int i = termsToRemove.size() - 1; i >= 0 ; i--) {
				int index = termsToRemove.get(i);
				terms.remove(index);
				expandedTerms.remove(index);
			}
			
			// now commonTerms contains all nodes with a factor of likeTerm
			Vector<Node> combinedTerms = new Vector<Node>();
			for (Vector<Node> commonTerm : commonTerms) {
				commonTerm.remove(likeTerm);
				combinedTerms.add(staggerMultiplication(commonTerm));
			}
			
			additionChildren.add(new Expression(
					new Operator.Multiplication(), 
					staggerAddition(combinedTerms),
					likeTerm));
			
			likeTerm = findDuplicate(expandedTerms);
		}
		
		for (Node t : terms) { // all the ones that couldn't be collected
			additionChildren.add(t);
		}
		
		return stagger(additionChildren, new Operator.Addition());
	}
	
	private Node findDuplicate(Vector<Vector<Node>> expandedTerms) {
		Vector<Node> factors;
		Vector<Node> searchTerm;
		for (int i = 0 ; i < expandedTerms.size(); i++) {
			factors = expandedTerms.get(i);
			for (Node f : factors) {
				if (!f.containsIdentifier())
					continue;
				for (int search = (i + 1) ; search < expandedTerms.size(); search++) {
					searchTerm = expandedTerms.get(search);
					if (searchTerm.contains(f))
						return f;
				}
			}
		}
		return null;
	}
	
	@Override
	public Node standardFormat() throws NodeException {
		Vector<Node> terms = splitOnAddition();
		
		if (terms.size() > 1) {
			Vector<Node> formattedTerms = new Vector<Node>();
			for (Node term : terms) {
				formattedTerms.add(term.standardFormat());
			}
			Vector<Vector<Node>> expandedTerms = new Vector<Vector<Node>>();
			for (Node term : formattedTerms) {
				expandedTerms.add(term.splitOnMultiplication());
			}
			
//			Vector<Vector<Node>> expandedTerms = new Vector<Vector<Node>>();
//			for (Node term : terms) {
//				expandedTerms.add(term.splitOnMultiplication());
//			}
			
			boolean[] subtraction = new boolean[terms.size()];
			for (int i = 0 ; i < expandedTerms.size() ; i++) {
				Vector<Node> expandedTerm = expandedTerms.get(i);
				if (expandedTerm.contains(Number.get(-1))) {
					expandedTerm.remove(Number.get(-1));
					formattedTerms.set(i, staggerMultiplication(expandedTerm).standardFormat());
					subtraction[i] = true;
				} else {
					subtraction[i] = false;
				}
			}
			
//			
//			Vector<Node> formattedTerms = new Vector<Node>();
//			for (Vector<Node> expandedTerm : expandedTerms) {
//				formattedTerms.add(staggerMultiplication(expandedTerm).standardFormat());
//				// this is where the recursion is for addition
//			}
			
			Vector<Integer> indices = sortIntegersAs(formattedTerms, Node.getStandardComparator());
			
			Vector<Node> sortedTerms = new Vector<Node>();
			boolean[] sortedSubtraction = new boolean[subtraction.length];
			
			for (int i = 0 ; i < indices.size() ; i++) {
				sortedTerms.add(formattedTerms.get(indices.get(i)));
				sortedSubtraction[i] = subtraction[indices.get(i)];
			}
			
			Node sum = null;
			for (int i = 0 ; i < sortedTerms.size() ; i++) {
				boolean sub = sortedSubtraction[i];
				Node term = sortedTerms.get(i);
				if (i == 0) {
					if (sub) {
						sum = new Expression(new Operator.Negation(), term);
					} else {
						sum = term;
					}
				} else {
					sum = new Expression((sub ? new Operator.Subtraction() : new Operator.Addition()),
							sum, term);
				}
			}
			
			return sum;
		}
		
		Vector<Node> factors = splitOnMultiplication();

		while (factors.contains(Number.get(1)) && (factors.size()  > 1)) {
			factors.remove(Number.get(1));
		}
		
		if (factors.size() > 1) {
			for (int i = 0 ; i < factors.size(); i++) {
				Node f = factors.get(i);
				if (f instanceof Number) {
					Number n = (Number) f;
					if (n.isNegative() && !n.equals(Number.get(-1))) {
						factors.set(i, n.negate());
						factors.add(Number.get(-1));
					}
				}
			}
			if (factors.remove(Number.get(-1))) {
				return new Expression(new Operator.Negation(), 
						staggerMultiplication(factors).standardFormat());
			}
			for (int i = 0 ; i < factors.size() ; i++) {
				factors.set(i, factors.get(i).standardFormat());
			}
			Collections.sort(factors, Node.getStandardComparator());
			Node product = null;
			for (int i = 0 ; i < factors.size() ; i++) {
				Node factor = factors.get(i);
				if (i == 0) {
					product = factor;
				} else {
					Operator mult;
					if (factor.containsIdentifier()) {
						mult = new Operator.Multiplication(Operator.Multiplication.Format.IMPLICIT);
					} else {
						mult = new Operator.Multiplication(Operator.Multiplication.Format.DOT);
					}
					
					product = new Expression(mult, product, factor);
				}
			}
			return product;
		}
		
		Vector<Node> formattedChildren = new Vector<Node>();
		for (Node child : children) {
			formattedChildren.add(child.standardFormat());
		}
		return new Expression(o.clone(), formattedChildren);
	}
	
	@Override
	public int standardCompare(Node other) {
		if (this.equals(other))
			return 0;
		
		if (!(other instanceof Expression)) {
			return -other.standardCompare(this);
		}
		Expression ex = (Expression) other;
		
		int c = o.getClass().getCanonicalName().compareTo(
				ex.getOperator().getClass().getCanonicalName());
		// operator compare hack
		if (c != 0) {
			return c;
		}
		
		int argnum = children.size() - ex.getChildren().size();
		if (argnum < 0)
			return -1;
		if (argnum > 0)
			return 1;
		
		int compare = 0;
		for (int i = 0 ; i < children.size() ; i++) {
			compare = children.get(i).standardCompare(ex.getChildren().get(i));
			if (compare != 0)
				return compare;
		}
		
		return 0;
	}
	
	private static <E> Vector<Integer> sortIntegersAs(List<E> list, Comparator<E> comparator) {
		Vector<Integer> indices = new Vector<Integer>();
		for (int i = 0 ; i < list.size() ; i++) {
			indices.add(i);
		}
		
		class IntegerComparator implements Comparator<Integer> {
			private List<E> terms;
			private Comparator<E> comparator;
			
			public IntegerComparator(List<E> terms, Comparator<E> comparator) {
				this.terms = terms;
				this.comparator = comparator;
			}
			
			@Override
			public int compare(Integer o1, Integer o2) {
				return comparator.compare(terms.get(o1), terms.get(o2));
			}
		}
		
		Collections.sort(indices, new IntegerComparator(list, comparator));
		
		return indices;
	}
	
	public static Node staggerAddition(Vector<Node> addends) {
		if (addends.isEmpty())
			return Number.get(0);
		return stagger(addends, new Operator.Addition());
	}
	
	public static Node staggerMultiplication(Vector<Node> factors) {
		if (factors.isEmpty())
			return Number.get(1);
		return stagger(factors, new Operator.Multiplication());
	}
	
	private static Node stagger(Vector<Node> addends, Operator op) {
		Vector<Node> children = (Vector<Node>) addends.clone();
		
		int size = children.size();
		switch (size) {
		case 0:
			return null;
		case 1:
			return children.firstElement();
		case 2:
			return new Expression(op.clone(), children);
		default:
			Node last = children.remove(children.size() - 1);
			return new Expression(op.clone(), stagger(children, op), last);
		}
	}

	@Override
	public boolean containsIdentifier() {
		boolean id = false;
		for (Node child : children) {
			id = id || child.containsIdentifier();
		}
		return id;
	}
	
	public Node multiplyByNode(Node n){
		Vector<Node> newChildren = new Vector<Node>();
		if ( n instanceof Expression && 
				! (((Expression)n).getOperator() instanceof Operator.Multiplication)){
			n.setDisplayParentheses(true);
		}
		for ( Node node : this.splitOnAddition()){
			try {
				newChildren.add( new Expression(new Operator.Multiplication(), n.cloneNode(), node.cloneNode()));
			} catch (NodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Expression.staggerAddition(newChildren);
	}
	
	public Node divideByNode(Node n){
		Vector<Node> newChildren = new Vector<Node>();
		if ( n instanceof Expression){
			n.setDisplayParentheses(true);
		}
		for ( Node node : this.splitOnAddition()){
			try {
				newChildren.add( new Expression(new Operator.Division(), node.cloneNode(), n.cloneNode()));
			} catch (NodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Expression.staggerAddition(newChildren);
	}
	
	@Override
	public Vector<Node> splitOnAddition() {
		Vector<Node> terms = new Vector<Node>();
		if (o instanceof Operator.Addition) {
			terms.addAll(children.get(0).splitOnAddition());
			terms.addAll(children.get(1).splitOnAddition());
		} else if (o instanceof Operator.Subtraction) {
			terms.addAll(children.get(0).splitOnAddition());
			Vector<Node> subtracted = children.get(1).splitOnAddition();
			Vector<Node> negativeTerms = new Vector<Node>();
			for (Node s : subtracted) {
				negativeTerms.add(new Expression(new Operator.Negation(), s));
			}
			terms.addAll(negativeTerms);
		} else {
			terms.add(this);
		}
		return terms;
	}
	
	@Override
	public Vector<Node> splitOnMultiplication() {
		Vector<Node> factors = new Vector<Node>();
		if (o instanceof Operator.Multiplication) {
			factors.addAll(children.get(0).splitOnMultiplication());
			factors.addAll(children.get(1).splitOnMultiplication());
		} else if (o instanceof Operator.Division) {
			factors.addAll(children.get(0).splitOnMultiplication());
			Vector<Node> divided = children.get(1).splitOnMultiplication();
			Vector<Node> reciprocalFactors = new Vector<Node>();
			for (Node d : divided) {
				reciprocalFactors.add(new Expression(new Operator.Division(), Number.get(1), d));
			}
			factors.addAll(reciprocalFactors);
		} else if (o instanceof Operator.Negation) {
			factors.add(Number.get(-1));
			factors.addAll(children.get(0).splitOnMultiplication());
		} else {
			factors.add(this);
		}
		return factors;
	}

	public Operator getOperator() {
		return o;
	}

	public void setOperator(Operator o) {
		this.o = o;
	}

	public Vector<Node> getChildren() {
		return children;
	}

	public void setChildren(Vector<Node> children) {
		this.children = children;
	}
	
	public Node getChild(int i){
		return children.get(i);
	}

	private static String parenthetize(String s) {
		return "(" + s + ")";
	}
	
}

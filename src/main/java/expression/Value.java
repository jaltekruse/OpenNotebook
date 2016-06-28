package expression;

import java.util.Vector;

/**
 * Instances of this class are {@code Node} objects that
 * do not contain other {@code Node} objects; leaves on the
 * expression tree.
 * 
 * @author Killian Kvalvik
 *
 */
public abstract class Value extends Node {
	
	@Override
	public Node collectLikeTerms() throws NodeException {
		// this means nothing for pure Values
		return this.cloneNode();
	}

	@Override
	public Node smartNumericSimplify() throws NodeException {
		return numericSimplify();
	}
	
	@Override
	public Node numericSimplify() throws NodeException {
		// this means nothing for pure Values
		return this.cloneNode();
	}
	
	@Override
	public Node standardFormat() throws NodeException {
		return this.cloneNode();
	}
	
	@Override
	public Vector<Node> splitOnAddition() {
		Vector<Node> v = new Vector<>();
		v.add(this);
		return v;
	}
	
	@Override
	public Vector<Node> splitOnMultiplication() {
		Vector<Node> v = new Vector<>();
		v.add(this);
		return v;
	}
	
	@Override
	public boolean containsIdentifier() {
		return (this instanceof Identifier);
	}

	public static Value parseValue(String expression) throws NodeException {
		try {
			return Number.parseNumber(expression);
		} catch (NumericException e) {
			return new Identifier(expression);
		}
	}
}

package expression;

import java.util.Vector;

/**
 * Wrapper class for a an entire expression tree. Holds information that applies to
 * the entire tree.
 * 
 * @author jason
 *
 */
public class VarList {
	
	private Vector<String> identifiersWithValues;
	private Vector<Number> identifierValues;

	public VarList(){
		identifiersWithValues = new Vector<>();
		identifierValues = new Vector<>();
	}
	
	public void setIdentifierValue(String s, Number n){
		identifiersWithValues.add(s);
		identifierValues.add(n);
	}
	
	public boolean removeIdentifier(String s){
		if ( this.identifiersWithValues.contains(s)){
			identifierValues.remove(identifiersWithValues.indexOf(s));
			identifiersWithValues.remove(s);
			return true;
		}
		return false;
	}
	
	public int size(){
		return identifiersWithValues.size();
	}
	
	public double evaluate(Node n) throws NodeException{
		String s;
		Node ex = null;
		ex = n.cloneNode();
		double val = Double.MAX_VALUE;
		for ( int i = 0; i < this.identifiersWithValues.size(); i++){
			s = this.identifiersWithValues.get(i);
			ex = ex.replace(new Identifier(s), identifierValues.get(i));
		}
		ex = ex.numericSimplify();
		if ( ex instanceof Expression){
			if ( ((Expression)ex).getOperator() instanceof Operator.Equals){
				if ( ((Expression)ex).getChild(1) instanceof Number){
					val = ((Number)((Expression)ex).getChild(1)).getValue();
				}
			}
		}
		else if (ex instanceof Number){
			val = ((Number)ex).getValue();
		}
		return val;
	}
}

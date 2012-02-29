package expression;

import java.util.Random;
import java.util.Vector;

/**
 * A class representing a generic operator in an expression tree.
 * All operators, such as addition, subtraction, etc. are instances 
 * of this class.
 * 
 * @author Killian Kvalvik
 *
 */
public abstract class Operator {

	public static interface DisplayFormat {
		
	}
	
	@Override
	public boolean equals(Object o) {
		if (! (o instanceof Operator))
			return false;
		return ((this.getClass() == o.getClass()) &&
				(this.getFormat() == ((Operator) o).getFormat()));
	}

	@Override
	public int hashCode() {
		return 17;
	}

	@Override
	public abstract Operator clone();

	public abstract String getSymbol();
	
	public abstract Number evaluate(Vector<Number> children) throws NodeException;

	public abstract String format(Vector<String> children) throws NodeException;

	public DisplayFormat getFormat() {
		return null;
	}

	public String toString(Vector<Node> children) throws NodeException {
		Vector<String> stringChildren = new Vector<String>();
		for (Node c : children)
			stringChildren.add(c.toStringRepresentation());
		return format(stringChildren);
	}

	public static abstract class BinaryOperator extends Operator {
		@Override
		public String format(Vector<String> children) throws NodeException {
			if (children.size() != 2){
				throwBadArguments();
			}
			return children.get(0) + getSymbol() + children.get(1);
		}
		
		@Override
		public Number evaluate(Vector<Number> children) throws NodeException {
			if (children.size() != 2)
				throwBadArguments();
			return evaluate(children.get(0), children.get(1));
		}
		
		public abstract Number evaluate (Number a, Number b) throws NodeException;
	}
	
	public static class Addition extends BinaryOperator {
		@Override
		public String getSymbol() {
			return "+";
		}

		@Override
		public Number evaluate(Number a, Number b) {
			return a.add(b);
		}

		@Override
		public Operator clone() {
			return new Addition();
		}
	}
	
	public static class Subtraction extends BinaryOperator {
		@Override
		public String getSymbol() {
			return "-";
		}

		@Override
		public Number evaluate(Number a, Number b) {
			return a.subtract(b);
		}

		@Override
		public Operator clone() {
			return new Subtraction();
		}
	}
	
	public static class Multiplication extends BinaryOperator {
		
		public enum Format implements DisplayFormat {
			ASTERISK, DOT, IMPLICIT, X;
		}
		
		private Format format;
		
		public Multiplication() {
			this(Format.ASTERISK);
		}
		
		public Multiplication(Format format) {
			setFormat(format);
		}

		@Override
		public Format getFormat() {
			return format;
		}

		public void setFormat(Format format) {
			this.format = format;
		}

		@Override
		public Number evaluate(Number a, Number b) {
			return a.multiply(b);
		}

		@Override
		public String getSymbol() {
			if (format == Format.IMPLICIT)
				return "";
			else
				return "*";
		}

		@Override
		public Operator clone() {
			return new Multiplication(getFormat());
		}
	}
	
	public static class Division extends BinaryOperator {
		
		public enum Format implements DisplayFormat {
			COLON, DIAGONAL_BAR, HORIZONTAL_BAR, OBELUS;
		}
		
		private Format format;
		
		public Division() {
			this(Format.HORIZONTAL_BAR);
		}
		
		public Division(Format format) {
			setFormat(format);
		}

		@Override
		public Format getFormat() {
			return format;
		}

		public void setFormat(Format format) {
			this.format = format;
		}

		@Override
		public Number evaluate(Number a, Number b) {
			return a.divide(b);
		}

		@Override
		public String getSymbol() {
			return "/";
		}

		@Override
		public Operator clone() {
			return new Division(getFormat());
		}
	}
	
	public static class Exponent extends BinaryOperator { // add formatting if necessary

		public enum Format implements DisplayFormat {
			CARET, SUPERSCRIPT;
		}
		
		private Format format = Format.SUPERSCRIPT;
		
		public Exponent() {
			this(Format.SUPERSCRIPT);
		}
		
		public Exponent(Format format) {
			setFormat(format);
		}

		@Override
		public Format getFormat() {
			return format;
		}

		public void setFormat(Format format) {
			this.format = format;
		}

		@Override
		public Number evaluate(Number a, Number b) {
			return a.exponent(b);
		}

		@Override
		public String getSymbol() {
			return "^";
		}

		@Override
		public Operator clone() {
			return new Exponent(getFormat());
		}
		
	}
	
	public static class Equals extends BinaryOperator {
		@Override
		public String getSymbol() {
			return "=";
		}

		@Override
		public Number evaluate(Number a, Number b) throws NodeException {
			throw new NodeException("cannot evaluate an equality");
		}

		@Override
		public Operator clone() {
			return new Equals();
		}
	}
	
	public static abstract class Function extends Operator {
		public abstract int getArity();
		
		public abstract Number safeEval(Vector<Number> children);
		
		@Override
		public Number evaluate(Vector<Number> children) throws NodeException {
			if (children.size() != getArity())
				throwBadArguments();
			return safeEval(children);
		}
		
		@Override
		public String format(Vector<String> children) throws NodeException {
			if (children.size() != getArity())
				throwBadArguments();
			String s = getSymbol() + "(";
			for (int i = 0 ; i < children.size(); i++) {
				s += children.get(i);
				if (i != (children.size() - 1)) 
					s += ", ";
			}
			s += ")";
			return s;
		}
	}
	
	public static class LogBase extends Function {
		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public Number safeEval(Vector<Number> children) {
			return Number.log(children.get(0), children.get(1));
		}

		@Override
		public String getSymbol() {
			return "log";
		}

		@Override
		public Operator clone() {
			return new LogBase();
		}
	}
	
	public static class RandomGenerator extends Function {
		
		private static Random random = new Random();
		
		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public Number safeEval(Vector<Number> children) {
			return new Number( random.nextDouble() * ( children.get(1).getValue() -
					children.get(0).getValue() ) + children.get(0).getValue() );
		}

		@Override
		public String getSymbol() {
			return "random";
		}

		@Override
		public Operator clone() {
			return new RandomGenerator();
		}
	}
	
	public static class RandomIntGenerator extends Function {
		
		private static Random random = new Random();
		
		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public Number safeEval(Vector<Number> children) {
			Number newNum = null;
			do{
//				try {
//					Thread.sleep(2);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				newNum = new Number( ( random.nextInt((int) ( children.get(1).getValue() -
						children.get(0).getValue()  + 1) ) + (int) children.get(0).getValue() ) );
			} while( newNum.getValue() == 0);
			return newNum;
		}

		@Override
		public String getSymbol() {
			return "randomInt";
		}

		@Override
		public Operator clone() {
			return new RandomIntGenerator();
		}
	}
	
	public static class Root extends Function {
		@Override
		public int getArity() {
			return 2;
		}

		@Override
		public Number safeEval(Vector<Number> children) {
			return Number.root(children.get(0), children.get(1));
		}

		@Override
		public String getSymbol() {
			return "root";
		}

		@Override
		public Operator clone() {
			return new Root();
		}
	}
	
	public static abstract class UnaryFunction extends Function {

		@Override
		public int getArity() {
			return 1;
		}
		
		@Override
		public Number safeEval(Vector<Number> children) {
			return evaluate(children.firstElement());
		}

		public abstract Number evaluate(Number a);
	}
	
	
	public static class Negation extends UnaryFunction {
		
		@Override
		public String getSymbol() {
			return "-";
		}
	
		@Override
		public String format(Vector<String> children) throws NodeException {
			if (children.size() != 1)
				throwBadArguments();
			return getSymbol() + children.get(0);
		}
	
		@Override
		public Operator clone() {
			return new Negation();
		}
		
		@Override
		public Number evaluate(Number a) {
			return a.negate();
		}
		
	}
	public static class Logarithm extends UnaryFunction {

		@Override
		public String getSymbol() {
			return "log";
		}

		@Override
		public Number evaluate(Number a) {
			return a.log();
		}

		@Override
		public Operator clone() {
			return new Logarithm();
		}
	}
	
	public static class NaturalLog extends UnaryFunction {
		@Override
		public String getSymbol() {
			return "ln";
		}

		@Override
		public Number evaluate(Number a) {
			return a.ln();
		}

		@Override
		public Operator clone() {
			return new NaturalLog();
		}
	}
	
	public static class Sine extends UnaryFunction {
		@Override
		public String getSymbol() {
			return "sin";
		}

		@Override
		public Number evaluate(Number a) {
			return a.sin();
		}

		@Override
		public Operator clone() {
			return new Sine();
		}
	}
	
	public static class Cosine extends UnaryFunction {
		@Override
		public String getSymbol() {
			return "cos";
		}

		@Override
		public Number evaluate(Number a) {
			return a.cos();
		}

		@Override
		public Operator clone() {
			return new Cosine();
		}
	}
	
	public static class Tangent extends UnaryFunction {
		@Override
		public String getSymbol() {
			return "tan";
		}

		@Override
		public Number evaluate(Number a) {
			return a.tan();
		}

		@Override
		public Operator clone() {
			return new Tangent();
		}
	}
	
	public static class SquareRoot extends UnaryFunction {
		@Override
		public String getSymbol() {
			return "sqrt";
		}
		
		@Override
		public Number evaluate(Number a) {
			return a.sqrt();
		}

		@Override
		public Operator clone() {
			return new SquareRoot();
		}
	}
	
	public static class CubeRoot extends UnaryFunction {
		@Override
		public String getSymbol() {
			return "cbrt";
		}
		
		@Override
		public Number evaluate(Number a) {
			return a.cbrt();
		}

		@Override
		public Operator clone() {
			return new CubeRoot();
		}
	}
	
	public static class AbsoluteValue extends UnaryFunction {
		@Override
		public String getSymbol() {
			return "abs";
		}
		
		@Override
		public Number evaluate(Number a) {
			return a.abs();
		}

		@Override
		public Operator clone() {
			return new AbsoluteValue();
		}
	}
	
	public static void throwBadArguments() throws NodeException {
		throw new NodeException("Incorrect number of arguments");
	}
}

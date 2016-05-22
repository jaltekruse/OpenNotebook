package expression;

import java.util.Arrays;
import java.util.List;

public class Function {

	private Node formula;
	private List<Variable> variables;
	
	public Function(Node formula, List<Variable> variables) {
		this.formula = formula;
		this.variables = variables;
	}

	public Function(Node formula, Variable... variables) {
		this(formula, Arrays.asList(variables));
	}

	public Node getFormula() {
		return formula;
	}

	public void setFormula(Node formula) {
		this.formula = formula;
	}

	public List<Variable> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	public Node apply(Node... arguments) throws NodeException {
		return apply(Arrays.asList(arguments));
	}
	
	public Node apply(List<Node> arguments) throws NodeException {
		if (arguments.size() > variables.size()) {
			throw new NodeException("Too many arguments");
		}
		Node result = formula;
		for (int i = 0 ; i < arguments.size(); i++) {
			result = result.replace(variables.get(i), arguments.get(i));
		}
		return result;
	}
}

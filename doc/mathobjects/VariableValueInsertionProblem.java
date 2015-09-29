package doc.mathobjects;

import java.util.Vector;

import javax.swing.JOptionPane;

import doc.Page;
import doc.attributes.AttributeException;
import doc.attributes.ListAttribute;
import doc.attributes.MathObjectAttribute;
import doc.attributes.StringAttribute;
import doc_gui.graph.CartAxis;
import expression.Expression;
import expression.Identifier;
import expression.Node;
import expression.NodeException;
import expression.Number;
import expression.Operator;

public class VariableValueInsertionProblem extends ProblemGenerator {

	private static final int bufferSpace = 20;
	// store the parent document, allows access to document attributes from problems that
	// are in the list of generators but not on the page

	public VariableValueInsertionProblem(MathObjectContainer p, int x, int y, int w, int h) {
		super(p, x, y, w, h);
	}

	public VariableValueInsertionProblem(MathObjectContainer p){
		super(p);
	}

	public VariableValueInsertionProblem() {}

	@Override
	public void performSpecialObjectAction(String s) {
		if (s.equals(REMOVE_PROBLEM)){
			Object[] options = {"Continue", "Cancel"};
			int n = JOptionPane.showOptionDialog(null,
					"This operation will ungroup the objects in this problem and\n" +
							"place them back on the page. It will also delete the problem's\n" +
							"script data.",
							"Revert a Problem",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE,
							null,
							options,
							options[1]);

			if (n == 0){
				this.unGroup();
				this.getParentContainer().removeObject(this);
				this.getParentContainer().getParentDoc().getDocViewerPanel().setFocusedObject(null);
			}
			else{
				this.setActionCancelled(true);
			}
		}
		else if (s.equals(STORE_IN_DATABASE)){
			getParentContainer().getParentDoc().getDocViewerPanel().getNotebookPanel().addProbelmToDatabase((ProblemGenerator) clone());
		}
		else if (s.equals(GENERATE_NEW)){
			int number = 0;
			do {
				String num = (String)JOptionPane.showInputDialog(
						null,
						"Number of problems",
						"Number of problems.",
						JOptionPane.PLAIN_MESSAGE,
						null,
						null,
						null);
				if (num == null)
				{// the user hit cancel or the exit button on the dialog
					this.setActionCancelled(true);
					return;
				}
				try{
					number = Integer.parseInt(num);
					if (number < 1 || number > 50){
						JOptionPane.showMessageDialog(null,
								"Input must be an integer between 1 and 50",
								"Error",
								JOptionPane.ERROR_MESSAGE);
					}
				} catch ( Exception e){
					JOptionPane.showMessageDialog(null,
							"Input must be an integer between 1 and 50",
							"Error",
							JOptionPane.ERROR_MESSAGE);
				}
			} while ( number < 1 || number > 50);

			int greatestWidth = 0, greatestHeight = 0;
			Vector<GeneratedProblem> newProblems = new Vector<GeneratedProblem>();
			int difficulty;
			for (int i = 0; i < number; i++){
				if ( i < number / 3){
					difficulty = EASY;
				}
				else if ( i < number * (2/3)){
					difficulty = MEDIUM;
				}
				else{
					difficulty = HARD;
				}
				GeneratedProblem newProb = generateProblem(difficulty);
				newProblems.add(newProb);
				if (newProb.getWidth() > greatestWidth){
					greatestWidth = newProb.getWidth();
				}
				if (newProb.getHeight() > greatestHeight){
					greatestHeight = newProb.getHeight();
				}
			}

			// add this generator to the documents background list of generators for later
			// reference by the children
			try {
				getParentContainer().getParentDoc().addGenerator(this);
			} catch (Exception e1) {
				// What to do if generator ID collides with another, or if
				// the same generator tries to get added twice
				System.out.println(e1.getMessage());
			}

			int numColumns = ( (getParentContainer().getWidth()
					- 2 * getParentPage().getxMargin() - bufferSpace) / (greatestWidth + bufferSpace) );
			int totalExtraSpace = ( (getParentContainer().getWidth()
					- 2 * getParentPage().getxMargin() - bufferSpace) % (greatestWidth + bufferSpace) );

			int extraColumnSpace = totalExtraSpace / (numColumns + 1);
			int currColumn = 0;
			int curryPos = getyPos() + getHeight() + bufferSpace;
			Page currentPage = getParentPage();

			for (MathObject mObj : newProblems){
				mObj.setxPos(currentPage.getxMargin() + bufferSpace + extraColumnSpace + 
						currColumn * (greatestWidth + bufferSpace + extraColumnSpace));
				mObj.setyPos(curryPos);
				if ( ! mObj.isOnPage()){
						if ( currentPage.getParentDoc().getNumPages() < currentPage.getParentDoc().getPageIndex(currentPage) + 2)
						{// a new page must be added to add the objects
							currentPage.getParentDoc().addBlankPage();
							currentPage = currentPage.getParentDoc().getPage(currentPage.getParentDoc().getNumPages() - 1);
							currentPage.getParentDoc().getDocViewerPanel().resizeViewWindow();
						}
						else
						{// there is a next page on the document that the new objects can be added to
							currentPage = currentPage.getParentDoc().getPage( currentPage.getParentDoc().getPageIndex(currentPage) + 1);
						}

						curryPos = currentPage.getyMargin() + bufferSpace;
						mObj.setyPos(curryPos);
				}
				currentPage.addObject(mObj);
				mObj.setParentContainer(currentPage);
				currColumn++;
				if (currColumn > numColumns - 1){
					curryPos += greatestHeight + bufferSpace;
					currColumn = 0;
				}
			}

		}
	}
	
	public MathObject subInVal(String s, Node val, MathObject mObj){
		MathObject newObj = mObj.clone();

        // TODO - use inheritance for this
		if (newObj instanceof Grouping){
			Grouping newGroup = (Grouping) newObj.clone();
			newGroup.removeAllObjects();
			for ( MathObject mathObj : ((Grouping)newObj).getObjects()){
				newGroup.addObjectFromPage(subInVal(s, val, mathObj));
			}
			return newGroup;
		}
		else if ( newObj instanceof ExpressionObject){
			final String exString = ((ExpressionObject)newObj).getExpression();
            // TODO - review, make this work with multiple answers
            final String ansString = ((ExpressionObject)newObj).getCorrectAnswers().get(0).getValue();
			if (exString != null && ! exString.equals("")){
				try {
					Node expression = Node.parseNode(exString);

					expression = expression.replace(s, val);
					((ExpressionObject)newObj).setExpression(expression.toStringRepresentation());

                    Node ansExpression = Node.parseNode(ansString);
                    ansExpression = ansExpression.replace(s, val);
                    // TODO - make this work with multiple answers
                    ((ExpressionObject)newObj).getCorrectAnswers().clear();
                    ((ExpressionObject)newObj).addCorrectAnswer(ansExpression.toStringRepresentation());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else if ( newObj instanceof GraphObject){
			for ( StringAttribute exAtt : ((GraphObject)newObj).getExpressions()){
				if (exAtt.getValue() != null && ! exAtt.getValue().equals("")){
					try {
						Node expression = Node.parseNode(exAtt.getValue());
						expression = expression.replace(s, val);

						exAtt.setValue(expression.toStringRepresentation());
					} catch (Exception e) {	
						e.printStackTrace();
					}
				}
			}
		}
		else if ( newObj instanceof TextObject){
			final String textString = replaceInString(s, ((TextObject) newObj).getText() , val);
			try{
				((TextObject) newObj).setText(textString);
			} catch( AttributeException e){
				e.printStackTrace();
			}

		}
		else if ( newObj instanceof AnswerBoxObject ){
			String str;
			for (StringAttribute strAtt : ((AnswerBoxObject) newObj).getCorrectAnswers().getValues()){
				str = strAtt.getValue();
				str = replaceInString(s, str, val);
				strAtt.setValue(str);
			}
		}
		return newObj;
	}
	
	public String replaceInString(String s, String textString, Node val){
		String valString;
		try {
			if ( val instanceof Number){
				valString = CartAxis.doubleToString(((Number)val).getValue(), 1);
			}
			else{
				valString = val.toStringRepresentation();
			}
			for (int j = 0; j < textString.length(); j++)
			{// loop through all characters in text
				if ( s.equals(textString.charAt(j) + "" ) )
				{// if the variable char was found
					if ( (j == 0 || ! Character.isLetter(textString.charAt(j - 1)) ) && 
							( j == textString.length() - 1 ||
							! Character.isLetter(textString.charAt(j + 1)) ) )
					{// if the character is surrounded by non-alphabetic chars
						if ( j != 0 && j != textString.length())
						{// if the char is not at the end or beginning
							textString = textString.substring(0, j) 
									+ valString + 
									textString.substring(j+1);
						}
						else{
							if ( j == 0){
								if (textString.length() > 1)
									textString = valString 
											+ textString.substring(j + 1);
								else
									textString = valString;
							}
							else if ( j == textString.length()){
								if (textString.length() > 1)
									textString = textString.substring(0, j - 1) +  
											valString;
								else
									textString = valString;
							}
						}
					}
				}
			}
		} catch (NodeException e) {
			// should not be throw an error
			e.printStackTrace();
		}
		return textString;
	}

	public GeneratedProblem generateProblem(int difficulty){
		Grouping newProblem = new Grouping(getParentContainer(), getxPos(),
				getyPos() + getHeight() + bufferSpace, getWidth(), getHeight());
		String s;
		Node n =  null;
		Vector<String> varNames = new Vector<String>();
		Vector<Number> varVals = new Vector<Number>();
		for (StringAttribute strAtt : (Vector<StringAttribute>) getScripts().getValues()){
			s = strAtt.getValue();
			if (s == null || s.equals("")){
				continue;
			}
			try {
				n = Node.parseNode(s);

				//sub in variables already assigned in previous scripts
				for ( int i = 0 ; i < varNames.size(); i++){
					n = n.replace(varNames.get(i), varVals.get(i));
				}

				n = n.numericSimplify();

				if (n instanceof Expression){
					Expression ex = (Expression) n;
					if (ex.getOperator() instanceof Operator.Equals){
						if (ex.getChild(0) instanceof Identifier){
							Identifier var = (Identifier)ex.getChild(0);
							if (ex.getChild(1) instanceof Number){
								varNames.add(var.getIdentifier());
								// this causes a lot of unneeded parenthesis
								// but without it, you cannot sub in a value
								// where there is an implied parenthesis
								// ex.getChild(1).setDisplayParentheses(true);
								varVals.add((Number) ex.getChild(1));
							}
						}
					}
				}
			} catch (NodeException e) {
				JOptionPane.showMessageDialog(null,
						"Error generating a problem, check scripts.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}


		MathObject newObj = null;

		for (MathObject mObj : getObjects()){
			newObj = mObj.clone();
			for ( int i = 0 ; i < varNames.size(); i++){
				newObj = subInVal(varNames.get(i), varVals.get(i), newObj);
			}
			//shift object down so it doesn't overlap the current problem
			newObj.setyPos( newObj.getyPos() + getHeight() + bufferSpace);

			//expressions can change their bounds when the random values are substituted in
			//this line sets the bounds to the actual space it takes to render them
			if ( getParentContainer() != null)
			{// if this problem formula is in the background storage for a document
				System.out.println("21342134 asdfasf@E@$##@$");
				getParentDoc().getDocViewerPanel().drawObjectInBackground(newObj);
			}
			else
			{// if this problem formula is actually on a document
				System.out.println("asdfasf@E@$##@$");
				getProblemHoldingDocument().getDocViewerPanel().drawObjectInBackground(newObj);
			}
			newObj.setParentContainer(newProblem.getParentContainer());
			newProblem.addObjectFromPage(newObj);
		}

		return new GeneratedProblem(newProblem.getParentContainer(), this.getProblemID(), newProblem);
	}
	
	@Override
	public String getType() {
		return VAR_INSERTION_PROBLEM;
	}
	
	public VariableValueInsertionProblem newInstance(){
		return new VariableValueInsertionProblem();
	}
}

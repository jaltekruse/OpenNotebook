/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package math_rendering;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Vector;

import tree.BinExpression;
import tree.Fraction;
import tree.MissingValue;
import tree.Operator;
import tree.UnaryExpression;
import tree.Expression;

public class CompleteProblem {

	Vector<CompleteExpressionGraphic> expressions;
	Vector<ExpressionModification> steps;
	CompleteExpressionGraphic currentCEG;
	Vector<ValueGraphic> selectedTerms;
	private int xPos, yPos;
	private Graphics2D graphics;
	private int space = 10;
	
	public CompleteProblem(CompleteExpressionGraphic ceg, int x, int y){
		expressions = new Vector<CompleteExpressionGraphic>();
		steps = new Vector<ExpressionModification>();
		selectedTerms = new Vector<ValueGraphic>();
		expressions.add(ceg);
		currentCEG = ceg;
		xPos = x;
		yPos = y;
	}
	
	public void setGraphics(Graphics2D g){
		graphics = g; 
	}
	
	public void draw(){
		for (CompleteExpressionGraphic ceg : expressions){
			ceg.setGraphics(graphics);
			ceg.draw();
		}
		
		for (ExpressionModification em : steps){
			em.draw();
		}
	}
	
	public void selectTerm(int mouseX, int mouseY){
		Vector<ValueGraphic> temp = new Vector<ValueGraphic>();
		ValueGraphic smallest = null;
		for (ValueGraphic vg : currentCEG.getComponents()){
			if (mouseX >= vg.getX1() && mouseX <= vg.getX2() && mouseY >= vg.getY1() && mouseY <= vg.getY2())
			{
				temp.add(vg);
			}
		}
		if (temp.size() != 0){
			smallest = temp.get(0);
		}
		else{
			if (mouseX < currentCEG.xPos){
				temp.add(currentCEG.getRoot().getMostInnerWest());
			}
			else if (mouseX > 30 + currentCEG.getRoot().getX2() - currentCEG.getRoot().getX1()){
				temp.add(currentCEG.getRoot().getMostInnerEast());
			}
		}
		if (smallest == null)
		{
			smallest = currentCEG.getComponents().get(0);
		}
		for (ValueGraphic vg : temp){
			if (smallest.containedBelow(vg)){
				smallest = vg;
			}
		}
		
//		smallest.setCursorPos(e.getX());
		Expression smallVal = smallest.getValue();
		System.out.println("smallest clicked" + smallVal);
		System.out.println("smallest's parent:" + smallVal.getParent());
//		currentCEG.clearSelection();
		if (smallVal instanceof BinExpression){
			if (((BinExpression)smallVal).getOp().getPrec() < 3)
			{//did not click on a term
				
			}
			else{
				ValueGraphic term = currentCEG.getRoot().findValueGraphic(
						smallest.getValue().findParentTermRoot());
				if (selectedTerms.contains(term)){
					term.setSelectAllBelow(false);
					selectedTerms.remove(term);
				}
				else{
					term.setSelectAllBelow(true);
					selectedTerms.add(term);
				}
			}
		}
		else if (smallVal != null){
			System.out.println("rootTerm	dsafadf:" + smallest.getValue().findParentTermRoot());
			ValueGraphic term = currentCEG.getRoot().findValueGraphic(
					smallest.getValue().findParentTermRoot());
			if (selectedTerms.contains(term)){
				term.setSelectAllBelow(false);
				selectedTerms.remove(term);
			}
			else{
				term.setSelectAllBelow(true);
				selectedTerms.add(term);
			}
		}
	}
	
	public CompleteExpressionGraphic getCurrentCEG(){
		return currentCEG;
	}
	
	public void applyUnarytoBothSides(UnaryExpression uEx){
		Expression rootVal = currentCEG.getRoot().getValue();
		int height = 0;
		for (CompleteExpressionGraphic ceg : expressions){
			height += ceg.getRoot().getHeight();
			height += space;
		}
		for (ExpressionModification em : steps){
			height += em.getHeight();
			height += space;
		}
		System.out.println("xPos:" + xPos);
		if (rootVal instanceof BinExpression){
			if (((BinExpression) rootVal).getOp() == Operator.ASSIGN){
				uEx.setChild(((BinExpression)rootVal).getLeftChild());
				((BinExpression)rootVal).setLeftChild(uEx);
				UnaryExpression uEx2 = new UnaryExpression(uEx.getOp());
				uEx2.setChild(((BinExpression)rootVal).getRightChild());
				((BinExpression)rootVal).setRightChild(uEx2);
				setCurrentCEG(new CompleteExpressionGraphic(rootVal));
				expressions.add(currentCEG);
				try {
					currentCEG.generateExpressionGraphic(graphics, xPos, yPos + height + space);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void setCurrentCEG(CompleteExpressionGraphic ceg){
		currentCEG.getRoot().setSelectAllBelow(false);
		selectedTerms = new Vector<ValueGraphic>();
		currentCEG = ceg;
	}
	
	public void MoveTermToOtherSide(){
		if (selectedTerms.size() == 1){
			Expression selectedTerm = selectedTerms.get(0).getValue();
			ValueGraphic currVG = currentCEG.getRoot().findValueGraphic(selectedTerm);
			System.out.println(selectedTerm.getClass());
			Expression selectedTermParent = selectedTerm.getParent();
			String exString = null;
			String opString = "+";
			boolean onLeft = selectedTerm.onLeftSideEquation();
			boolean onRight = selectedTerm.onRightSideEquation();
			Expression rootVal = currentCEG.getRoot().getValue();
			int height = 0;
			int vg2XPos = xPos;
			for (CompleteExpressionGraphic ceg : expressions){
				height += ceg.getRoot().getHeight();
				height += space;
			}
			for (ExpressionModification em : steps){
				height += em.getHeight();
				height += space;
			}
			System.out.println("xPos:" + xPos);
			if (rootVal instanceof BinExpression){
				if (((BinExpression) rootVal).getOp() == Operator.ASSIGN){
					
					if (selectedTerm.isRightChild()){
						System.out.println("selected is right");
						if (selectedTerm.getParent() instanceof BinExpression){
							if (((BinExpression)selectedTerm.getParent()).getOp() == Operator.SUBTRACT){
								//term just needs to be added to other side
								opString = "+";
							}
							else if (((BinExpression)selectedTerm.getParent()).getOp() == Operator.ADD 
									|| ((BinExpression)selectedTerm.getParent()).getOp() == Operator.ASSIGN ){
								//negate term before moving to other side
								opString = "-";
							}
							System.out.println("!#@$!@# parent of parent:" + selectedTermParent.getParent());
							if (selectedTermParent instanceof BinExpression){
								if (selectedTermParent.isLeftChild()){
									((BinExpression)selectedTermParent.getParent()).setLeftChild(
											((BinExpression) selectedTermParent).getLeftChild());
									if (onLeft){
										exString = currentCEG.getRoot().getValue().toString() + opString + selectedTerm.toString();
										vg2XPos = ((BinExpressionGraphic)currentCEG.getRoot()).getLeftGraphic().getWidth() + 60;
									}
									else if (onRight){
										exString = ((BinExpression)currentCEG.getRoot().getValue()).getLeftChild().toString()
												+ opString +  selectedTerm.toString() + "=" +
												((BinExpression)currentCEG.getRoot().getValue()).getRightChild().toString();
									}
								}
								else if (selectedTermParent.isRightChild()){
									if (selectedTermParent.getParent() == currentCEG.getRoot().getValue()){
										System.out.println("!!!!!selectTerm parent is root");
									}
									((BinExpression)selectedTermParent.getParent()).setRightChild(
											((BinExpression) selectedTermParent).getLeftChild());
									if (onLeft){
										exString = currentCEG.getRoot().getValue().toString() + opString + selectedTerm.toString();
										vg2XPos = ((BinExpressionGraphic)currentCEG.getRoot()).getLeftGraphic().getWidth() + 60;
									}
									else if (onRight){
										exString = ((BinExpression)currentCEG.getRoot().getValue()).getLeftChild().toString()
												+ opString +  selectedTerm.toString() + "=" +
												((BinExpression)currentCEG.getRoot().getValue()).getRightChild().toString();
									}
								}
								else if (selectedTermParent == currentCEG.getRoot().getValue()){
									((BinExpression)selectedTermParent).setRightChild(new Fraction(0, 1));
									exString = ((BinExpression)currentCEG.getRoot().getValue()).getLeftChild().toString()
											+ opString +  selectedTerm.toString() + "=" +
											((BinExpression)currentCEG.getRoot().getValue()).getRightChild().toString();
								}
							}
						}
					}
					if (selectedTerm.isLeftChild()){
						System.out.println("selected is left");
						if (selectedTermParent instanceof BinExpression){
							if (((BinExpression)selectedTerm.getParent()).getOp() == Operator.SUBTRACT){
								//term just needs to be added to other side
								opString = "+";
							}
							else if (((BinExpression)selectedTerm.getParent()).getOp() == Operator.ADD 
									|| ((BinExpression)selectedTerm.getParent()).getOp() == Operator.ASSIGN ){
								//negate term before moving to other side
								opString = "-";
							}
							System.out.println("!#@$!@# parent of parent:" + selectedTermParent.getParent());
							if (selectedTermParent.isLeftChild()){
								System.out.println("selected's parent is leftChild");
								((BinExpression)selectedTermParent.getParent()).setLeftChild(
										((BinExpression) selectedTermParent).getRightChild());
								if (onLeft){
									exString = currentCEG.getRoot().getValue().toString() + opString + selectedTerm.toString();
									vg2XPos = ((BinExpressionGraphic)currentCEG.getRoot()).getLeftGraphic().getWidth()+ 60;
								}
								else if (onRight){
									exString = ((BinExpression)currentCEG.getRoot().getValue()).getLeftChild().toString()
											+ opString +  selectedTerm.toString() + "=" +
											((BinExpression)currentCEG.getRoot().getValue()).getRightChild().toString();
								}
							}
							else if (selectedTermParent.isRightChild()){
								System.out.println("selected's parent is rightChild");
								((BinExpression)selectedTermParent.getParent()).setRightChild(
										((BinExpression) selectedTermParent).getRightChild());
								if (onLeft){
									exString = currentCEG.getRoot().getValue().toString() + opString + selectedTerm.toString();
									vg2XPos = ((BinExpressionGraphic)currentCEG.getRoot()).getLeftGraphic().getWidth()+ 60;
								}
								else if (onRight){
									exString = ((BinExpression)currentCEG.getRoot().getValue()).getLeftChild().toString()
											+ opString +  selectedTerm.toString() + "=" +
											((BinExpression)currentCEG.getRoot().getValue()).getRightChild().toString();
								}
							}
							else if (selectedTermParent == currentCEG.getRoot().getValue()){
								((BinExpression)selectedTermParent).setLeftChild(new Fraction(0, 1));
								exString = currentCEG.getRoot().getValue().toString() + opString + selectedTerm.toString();
							}
						}
					}
					try {
						System.out.println("modified ex:" + exString);
						ValueGraphic nothing = new NothingGraphic(new MissingValue(), currentCEG);
						if (opString.equals("-")){
							selectedTerm = currentCEG.getRoot().getValue().getParser().ParseExpression("-" + selectedTerm.toString());
						}
						ValueGraphic vg = nothing.makeValueGraphic(selectedTerm);
						ValueGraphic vg2 = nothing.makeValueGraphic(selectedTerm);
						
						
						int vgXPos = currVG.getX1();
						System.out.println("x:" + xPos + " y: " + yPos);
						vg.requestSize(graphics, currVG.getFont(), vgXPos, yPos + height);
						vg2.requestSize(graphics, currVG.getFont(), vg2XPos, yPos + height);
						height += vg.getHeight();
						height += space;
						ExpressionModification em = new ExpressionModification();
						em.addVG(vg);
						em.addVG(vg2);
						steps.add(em);
						setCurrentCEG(new CompleteExpressionGraphic(
								currentCEG.getRoot().getValue().getParser().ParseExpression(exString)));
						currentCEG.generateExpressionGraphic(graphics, xPos, yPos + height);
						expressions.add(currentCEG);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		else{
			//there is more than 1 term selected, tell user somehow
		}
	}
}

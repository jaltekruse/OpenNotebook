/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

package doc_gui.graph;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Vector;

public class GraphCalculationGraphics extends GraphComponent {

	Vector<Derivative> derivatives;
	Vector<Tracer> tracers;
	Vector<Integral> integrals;
	Vector<CalcInfoBox> calcInfoBoxes;
	private BufferedImage copyIcon;
	
	public GraphCalculationGraphics(Graph g) {
		super(g);
//		copyIcon = graph.getMainApplet().getImage("smallCopy.png");
		derivatives = new Vector<Derivative>();
		tracers = new Vector<Tracer>();
		integrals = new Vector<Integral>();
		calcInfoBoxes = new Vector<CalcInfoBox>();
	}
	
	public void addDerivative(Derivative d){
		
		//if the given calculation already exists don't add a second copy
		for (Derivative derivative : derivatives)
		{
			if (d.getInvolvedGraphs().equals(derivative.getInvolvedGraphs()))
			{
				if (d.getSelection().getStart() == derivative.getSelection().getStart())
				{
					if (d.getSelection().getEnd() == derivative.getSelection().getEnd())
					{//this should always be equal to the flag value for empty Double.MAX_VALUE
						// TODO - possibly send a notification to the user that their request was ignored
						return;
					}
				}
			}
		}
		addCalcInfoBox(new CalcInfoBox(d, d.getSelection().getStart(), 0));
		derivatives.add(d);
	}
	
	public void addTracer(Tracer t){
		//if the given calculation already exists don't add a second copy
		for (Tracer tracer: tracers){
			if (t.getInvolvedGraphs().equals(tracer.getInvolvedGraphs())){
				if (t.getSelection().getStart() == tracer.getSelection().getStart())
				{
					if (t.getSelection().getEnd() == tracer.getSelection().getEnd())
					{//this should always be equal to the flag value for empty Double.MAX_VALUE
						System.out.println("not adding tracer");
						// TODO - possibly send a notification to the user that there request was ignored
						return;
					}
				}
			}
		}
		tracers.add(t);
		addCalcInfoBox(new CalcInfoBox(t, t.getSelection().getStart(), 0));
	}
	
	public void addIntegral(Integral i){
		for (Integral integral : integrals)
		{
			if (i.getInvolvedGraphs().equals(integral.getInvolvedGraphs()))
			{
				if (i.getSelection().getStart() == integral.getSelection().getStart())
				{
					if (i.getSelection().getEnd() == integral.getSelection().getEnd())
					{
						//possibly send a notification to the user that there request was ignored
						return;
					}
				}
			}
		}
		addCalcInfoBox(new CalcInfoBox(i, i.getSelection().getStart(), 0));
		integrals.add(i);
	}
	
	public void addCalcInfoBox(CalcInfoBox cib){
		
		for (CalcInfoBox calcib : calcInfoBoxes){
			if (calcib.getGraphCalc().equals(cib)){
				return;
			}
		}
		calcInfoBoxes.add(cib);	
	}
	
	public Vector<CalcInfoBox> getCalcInfoBoxes(){
		return calcInfoBoxes;
	}
	
	public Vector<Derivative> getDerivatives(){
		return derivatives;
	}
	
	public Vector<Integral> getIntegrals(){
		return integrals;
	}
	
	public Vector<Tracer> getTracers(){
		return tracers;
	}

	@Override
	public void draw(Graphics g) {
		try{
			double result = Double.MAX_VALUE;
			for (Derivative d : derivatives){
				for( SingleGraph sg : d.getInvolvedGraphs()){
//					if (sg instanceof GraphedCartFunction){
//						g.setColor(Color.black);
//						d.getSelection();
//						d.getSelection().getStart();
//						graph.getParser().getVarList().setVarVal(((GraphedCartFunction)sg)
//								.getIndependentVar().getName(), new Decimal(d.getSelection().getStart()));
////						System.out.println();
////						System.out.println("class GraphCalculationGraphics:");
////						System.out.println("graph: " + ((GraphWithExpression) sg).getExpression().toString());
////						System.out.println("selectionStart: " + d.getSelection().getStart());
////						System.out.println("derivative: " + ((GraphWithExpression) sg).getExpression().deriveAtPt(
////								d.getSelection().getStart(),"x", "y").toDec().getValue());
//
//						result = ((GraphWithExpression) sg).getExpression().deriveAtPt(
//								d.getSelection().getStart(),"x", "y").toDec().getValue();
//						d.setResult(result);
//						d.setGridX(d.getSelection().getStart());
//						double y = ((GraphedCartFunction)sg).getExpression().eval().toDec().getValue();
//						d.setGridY(y);
//						super.drawTangent(d.getSelection().getStart(), y, result, Color.BLACK, g);
//					}
				}
			}
			
//			for (Tracer t : tracers){
//				for( SingleGraph sg : t.getInvolvedGraphs()){
//					if (sg instanceof GraphedCartFunction){
//						g.setColor(Color.black);
//						t.getSelection();
//						t.getSelection().getStart();
//						((GraphedCartFunction)sg).getIndependentVar().setValue(new Decimal(
//								t.getSelection().getStart()));
//						result = ((GraphedCartFunction)sg).getExpression().eval().toDec().getValue();
//						t.setResult(result);
//						t.setGridX(t.getSelection().getStart());
//						t.setGridY(t.getResult());
//						super.drawTracer(t.getSelection().getStart(), result, g);
//					}
//				}
//			}
			
		} catch (Exception e){
			e.printStackTrace();
		}

	}
	
	public void drawIntegrals(Graphics g){
		
		BufferedImage graphCompPic = new BufferedImage(graph.X_SIZE, graph.Y_SIZE,
				BufferedImage.TYPE_4BYTE_ABGR);
		
		float[] scales = { 1f, 1f, 1f, 0.3f };
		float[] offsets = new float[4];
		RescaleOp rop = new RescaleOp(scales, offsets, null);
//
//		for (Integral i : integrals){
//			for ( SingleGraph sg : i.getInvolvedGraphs()){
//				if (sg instanceof GraphedCartFunction){
//
//					GraphedCartFunction gcf = (GraphedCartFunction) sg;
//
//					//used to temporarily store the value stored in the independent and dependent vars,
//					//this will allow it to be restored after graphing, so that if in the terminal a
//					//value was assigned to x, it will not be overriden by the action of graphing
//					Number xVal = gcf.getIndependentVar().getValue();
//					Number yVal = gcf.getDependentVar().getValue();
//
//					graphCompPic = new BufferedImage(graph.X_SIZE, graph.Y_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
//					Graphics picG = graphCompPic.getGraphics();
//					double currX, currY;
//					try{
//						//System.out.println(funcEqtn);
//						Expression expression = gcf.getParser().ParseExpression(gcf.getFuncEqtn());
//
//						//the small extra value added prevents error when starting at 0
//						gcf.getIndependentVar().setValue(new Decimal(i.getSelection().getStart() + graph.X_PIXEL/21));
//						if (i.getSelection().getStart() < graph.X_MIN){
//							gcf.getIndependentVar().setValue(new Decimal(graph.X_MIN));
//						}
//						expression.eval();
//						for (int j = 1; j < graph.X_SIZE; j++)
//						{//go through each pixel from the start of the selection to
//							gcf.getIndependentVar().updateValue(graph.X_PIXEL);
//							expression.eval();
//							currX = gcf.getIndependentVar().getValue().toDec().getValue();
//							currY = gcf.getDependentVar().getValue().toDec().getValue();
//
//							if (currX > i.getSelection().getEnd()){
//								break;
//							}
//							if (currX >= i.getSelection().getStart() && currX <= i.getSelection().getEnd()) {
//								gcf.setColor(gcf.getColor().brighter());
//								graph.LINE_SIZE = 1;
//								if (currY < graph.Y_MAX && currY > graph.Y_MIN)
//									drawLineSeg(currX, 0, currX, currY, gcf.getColor(), picG);
//								else if (currY <= graph.Y_MIN)
//									drawLineSeg(currX, 0, currX, graph.Y_MIN, gcf.getColor(), picG);
//								else if (currY >= graph.Y_MAX)
//									drawLineSeg(currX, 0, currX, graph.Y_MAX, gcf.getColor(), picG);
//								else
//									;// do nothing
//								gcf.setColor(gcf.getColor().darker());
//							}
//						}
//						((Graphics2D)g).drawImage(graphCompPic, rop, 0, 0);
//
//						//draw the edges fully opaque so they can be easily seen
//						if (i.getSelection().getStart() > graph.X_MIN &&
//								i.getSelection().getStart() < graph.X_MAX){
//							graph.LINE_SIZE = 1;
//							gcf.getIndependentVar().setValue(new Decimal(i.getSelection().getStart()));
//							drawLineSeg(i.getSelection().getStart(), 0, i.getSelection().getStart(),
//									gcf.getExpression().eval().toDec().getValue(), gcf.getColor().brighter(), g);
//						}
//						if (i.getSelection().getEnd() > graph.X_MIN &&
//								i.getSelection().getEnd() < graph.X_MAX){
//							graph.LINE_SIZE = 1;
//							gcf.getIndependentVar().setValue(new Decimal(i.getSelection().getEnd()));
//							drawLineSeg(i.getSelection().getEnd(), 0, i.getSelection().getEnd(),
//									gcf.getExpression().eval().toDec().getValue(), gcf.getColor().brighter(), g);
//						}
//					} catch (Exception e){
//						e.printStackTrace();
//					}
//
//					i.setGridX(i.getSelection().getStart() +
//							(i.getSelection().getEnd() - i.getSelection().getStart() ) / 2);
//					gcf.getIndependentVar().setValue(new Decimal(i.getSelection().getStart() +
//							(i.getSelection().getEnd() - i.getSelection().getStart() ) / 2));
//
//					try {
//						i.setGridY(gcf.getExpression().eval().toDec().getValue());
////						i.setResult(((GraphWithExpression) sg).getExpression().integrate(
////								i.getSelection().getStart(), i.getSelection().getEnd(),
////								"x", "y").toDec().getValue());
//					} catch (EvalException e) {
//						e.printStackTrace();
//					}
//
//					gcf.getIndependentVar().setValue(xVal);
//					gcf.getDependentVar().setValue(yVal);
//
//					graphCompPic.flush();
//				}
//			}
//		}
	}
	
	public void drawInfoBoxes(Graphics g){
		int tempLength;
		int longestLength;
		int numLines;
		int height;
		for (CalcInfoBox ib : calcInfoBoxes){
			longestLength = 0;
			tempLength = 0;
			numLines = 1;
			height = 0;
			for ( SingleGraph sg : ib.getGraphCalc().getInvolvedGraphs()){
				numLines++;
				if (sg instanceof GraphWithExpression){
					//the extra ten accounts for the box to show the color of the graph
					tempLength = g.getFontMetrics().stringWidth(((GraphWithExpression)sg).getFuncEqtn()) + 10;
					if (tempLength > longestLength){
						longestLength = tempLength;
					}
				}
			}

			if (ib.getGraphCalc().getSelection().getEnd() != Selection.EMPTY){
				numLines++;
				tempLength = g.getFontMetrics().stringWidth(
						"start x: " + shortDouble(ib.getGraphCalc().getSelection().getStart()));
				//add the width of the icon used for copying the value to the clipboard
				tempLength += copyIcon.getWidth();
				if (tempLength > longestLength){
					longestLength = tempLength;
				}
				numLines++;
				tempLength = g.getFontMetrics().stringWidth(
						"end x: " + shortDouble(ib.getGraphCalc().getSelection().getEnd()));
				tempLength += copyIcon.getWidth();
				if (tempLength > longestLength){
					longestLength = tempLength;
				}
			}
			else{
				numLines++;
				tempLength = g.getFontMetrics().stringWidth(
						"x: " + shortDouble(ib.getGraphCalc().getSelection().getStart()));
				tempLength += copyIcon.getWidth();
				if (tempLength > longestLength){
					longestLength = tempLength;
				}
			}
			numLines++;
			
			tempLength = g.getFontMetrics().stringWidth(
					"result: " + shortDouble(ib.getGraphCalc().getResult()));
			tempLength += copyIcon.getWidth();
			if (tempLength > longestLength){
				longestLength = tempLength;
			}
			
			//add some extra width for whitespace
			longestLength += 8;
			height = g.getFontMetrics().getHeight() * numLines;
			height += 3;
			
			ib.setWidth(longestLength);
			ib.setHeight(height);
			
			if (ib.getX() == CalcInfoBox.NOT_DRAWN_YET){
				ib.setX( gridxToScreen(ib.getGraphCalc().getGridX()) );
				ib.setY( gridyToScreen(ib.getGraphCalc().getGridY()) - 10 - height);
			}
			bringBoxIntoWindow(ib);
			
			g.setColor(Color.WHITE);
			g.fillRect(ib.getX(), ib.getY(),longestLength, height);
			g.setColor(Color.BLACK);
			int lineNum = 1;
			
			if (ib.getGraphCalc() instanceof Tracer){
				g.drawString("Trace:" , ib.getX() + 3,
						ib.getY() + g.getFontMetrics().getHeight() * lineNum);
			}
			else if (ib.getGraphCalc() instanceof Integral){
				g.drawString("Integral:" , ib.getX() + 3,
						ib.getY() + g.getFontMetrics().getHeight() * lineNum);
			}
			else if (ib.getGraphCalc() instanceof Derivative){
				g.drawString("Derivative:" , ib.getX() + 3,
						ib.getY() + g.getFontMetrics().getHeight() * lineNum);
			}
			//for the one line to indicate the type of calculation
			lineNum++;
			
			for ( ; lineNum < ib.getGraphCalc().getInvolvedGraphs().size() + 2; lineNum++)
			{//print the expressions for all involved graphs
				SingleGraph sg = ib.getGraphCalc().getInvolvedGraphs().get(lineNum - 2);
				g.setColor(sg.getColor());
				g.fillRect(ib.getX() + 3, ib.getY() + g.getFontMetrics().getHeight() * lineNum -
						g.getFontMetrics().getHeight() + 6, 8, g.getFontMetrics().getHeight() - 4);
				g.setColor(Color.BLACK);
				g.drawString(((GraphWithExpression)sg).getFuncEqtn(), ib.getX() + 16,
						ib.getY() + g.getFontMetrics().getHeight() * lineNum);
			}
			if ( ib.getGraphCalc().getSelection().getEnd() != Selection.EMPTY)
			{//if the selection involved has an end
				g.drawString("start x: " + shortDouble(ib.getGraphCalc().getSelection().getStart()), ib.getX() + 3,
						ib.getY() + g.getFontMetrics().getHeight() * lineNum);
				lineNum++;
				g.drawString("end x: " + shortDouble(ib.getGraphCalc().getSelection().getEnd()), ib.getX() + 3,
						ib.getY() + g.getFontMetrics().getHeight() * lineNum);
				lineNum++;
			}
			else{
				g.drawString("x: " + shortDouble(ib.getGraphCalc().getSelection().getStart()), ib.getX() + 3,
						ib.getY() + g.getFontMetrics().getHeight() * lineNum);
				lineNum++;
			}
			g.drawString("result: " + shortDouble(ib.getGraphCalc().getResult()), ib.getX() + 3,
					ib.getY() + g.getFontMetrics().getHeight() * lineNum);
			lineNum++;
			g.drawRect(ib.getX(), ib.getY(), longestLength, height);
		}
			
	}
	
	public String shortDouble(double d){
		return "" + (float) d;
	}
	
	public void clearAll(){
		derivatives.removeAllElements();
		tracers.removeAllElements();
		integrals.removeAllElements();
		calcInfoBoxes.removeAllElements();
	}
	
	// I'm pretty sure this is supposed to allow a point or integral to be selected
	// which would allow them to be draged along the function
	public void SelectClosestToPoint(double x, double y){
		
	}
	
	public void bringBoxIntoWindow(CalcInfoBox ib){
		if (ib.getX() + ib.getWidth() > graph.X_SIZE){
			ib.setX(graph.X_SIZE - ib.getWidth());
		}
		else if (ib.getX() < 0){
			ib.setX( 0 );
		}
		if (ib.getY() < 0){
			ib.setY( 0 );
		}
		else if (ib.getY() + ib.getHeight() > graph.Y_SIZE){
			ib.setY( graph.Y_SIZE - ib.getHeight() );
		}
	}
	
	public void removeAllWithGraph(SingleGraph sg)
	{
		for (int i = 0; i < integrals.size(); i++ ){
			if (integrals.get(i).getInvolvedGraphs().contains(sg)){
				integrals.remove(i);
				i--;
			}
		}
		for (int i = 0; i < derivatives.size(); i++){
			if (derivatives.get(i).getInvolvedGraphs().contains(sg)){
				derivatives.remove(i);
				i--;
			}
		}
		for (int i = 0; i < tracers.size(); i++){
			if (tracers.get(i).getInvolvedGraphs().contains(sg)){
				tracers.remove(i);
				i--;
			}
		}
		for (int i = 0; i < calcInfoBoxes.size(); i++){
			if (calcInfoBoxes.get(i).getGraphCalc().getInvolvedGraphs().contains(sg)){
				calcInfoBoxes.remove(i);
				i--;
			}
		}
	}
	
	public CalcInfoBox boxContainsPt(int x, int y){
		CalcInfoBox cib;
		for (int i = calcInfoBoxes.size() - 1; i >= 0; i--){
			cib = calcInfoBoxes.get(i);
			if ( x > cib.getX() && x < cib.getX() + cib.getWidth()
					&& y > cib.getY() && y < cib.getY() + cib.getHeight()){
				return cib;
			}
		}
		return null;
	}
	
	public boolean ptIsOnCloseButton(int x, int y){
		
		return false;
	}
	
	public void bringGraphToFront(SingleGraph s){
		for ( int i = 0; i < integrals.size(); i++){
			Integral integral = integrals.get(i);
			for ( int j = 0; j < integral.getInvolvedGraphs().size(); j++){
				if ( integral.getInvolvedGraphs().get(j).equals(s)){
					integrals.add(integrals.remove(i));
				}
			}
		}
	}

}

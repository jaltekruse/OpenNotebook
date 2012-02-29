package doc_gui.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;

import tree.EvalException;
import tree.ParseException;
import doc.attributes.DoubleAttribute;
import doc.attributes.IntegerAttribute;
import doc.mathobjects.NumberLineObject;

public class NumberLine {

	private NumberLineComponent numLine;

	private Graph graph;

	private int OVERHANG;

	public NumberLine(){
		graph = new Graph();
		numLine = new NumberLineComponent(graph);
	}

	public void repaint(Graphics g, int xSize, int ySize, float docZoomLevel,
			int xPicOrigin, int yPicOrigin, NumberLineObject gObj){

		graph.DOC_ZOOM_LEVEL = docZoomLevel;
		OVERHANG = (int) (15 * graph.DOC_ZOOM_LEVEL);
		graph.X_PIC_ORIGIN = xPicOrigin + OVERHANG;
		graph.Y_PIC_ORIGIN = yPicOrigin;
		graph.X_SIZE = xSize - 2 * OVERHANG;
		if (graph.X_SIZE == 0){
			graph.X_SIZE = 1;
		}
		graph.Y_SIZE = ySize;
		if (graph.Y_SIZE == 0){
			graph.Y_SIZE = 1;
		}
		graph.FONT_SIZE = 8;

		graph.X_MIN = ((DoubleAttribute)gObj.getAttributeWithName(NumberLineObject.MIN)).getValue();
		graph.X_MAX = ((DoubleAttribute)gObj.getAttributeWithName(NumberLineObject.MAX)).getValue();
		graph.X_STEP = ((DoubleAttribute)gObj.getAttributeWithName(NumberLineObject.STEP)).getValue();
		graph.FONT_SIZE = ((IntegerAttribute)gObj.getAttributeWithName(NumberLineObject.FONT_SIZE)).getValue();
		graph.Y_MIN = -3;
		graph.Y_MAX = 3;
		graph.X_PIXEL = (graph.X_MAX - graph.X_MIN) / graph.X_SIZE;
		graph.Y_PIXEL = (graph.Y_MAX - graph.Y_MIN) / graph.Y_SIZE;
		graph.NUM_FREQ = 2;
		try {
			numLine.draw(g);
		} catch (EvalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		pushValsToNumberLineObject(gObj);
	}
	
	public void pushValsToNumberLineObject(NumberLineObject gObj){
		((DoubleAttribute)gObj.getAttributeWithName("min")).setValue(graph.X_MIN);
		((DoubleAttribute)gObj.getAttributeWithName("max")).setValue(graph.X_MAX);
		((DoubleAttribute)gObj.getAttributeWithName("step")).setValue(graph.X_STEP);
		((IntegerAttribute)gObj.getAttributeWithName("font size")).setValue(graph.FONT_SIZE);
	}

	private class NumberLineComponent extends GraphComponent{

		public NumberLineComponent(Graph g) {
			super(g);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void draw(Graphics g) throws EvalException, ParseException {

			Font oldFont = g.getFont();

			g.setFont(g.getFont().deriveFont(graph.FONT_SIZE * graph.DOC_ZOOM_LEVEL));

			//these four statements are for resizing the grid after zooming
			if((graph.X_MAX-graph.X_MIN)/graph.X_STEP >= 14)
			{
				if ((graph.X_MAX-graph.X_MIN)/14 > 1)
				{
					graph.X_STEP = (int)((graph.X_MAX-graph.X_MIN)/10);
				}
				else
				{
					for (int i = 0; i < 25; i ++){
						if ((graph.X_MAX-graph.X_MIN)/20/Math.pow(.5, i) < .7){
							graph.X_STEP = Math.pow(.5, i);
						}
					}
				}
			}

			else if((graph.X_MAX-graph.X_MIN)/graph.X_STEP < 6){
				if ((graph.X_MAX-graph.X_MIN)/6 > 1)
				{
					graph.X_STEP = (int)((graph.X_MAX-graph.X_MIN)/10);
				}
				else
				{
					for (int i = 0; i < 25; i ++){
						if ((graph.X_MAX-graph.X_MIN)/20 < Math.pow(.5, i)){
							graph.X_STEP = Math.pow(.5, i);
						}
					}
				}
			}

			int width;
			int height = (int) ( g.getFontMetrics().getHeight() * (.6));
			int numberAndAxisSpace = (int) ( 5 * graph.DOC_ZOOM_LEVEL );
			int numberInset = (int) (1 * graph.DOC_ZOOM_LEVEL );
			int tempFreq;

			// finds the fist factor of the graph.X_STEP on the screen
			// used to draw the first dash mark on the x-axis
			double tempX = (int) (graph.X_MIN / graph.X_STEP);
			tempX *= graph.X_STEP;

			
			graph.NUM_FREQ = 1;
			int tempWidth;
			do{
				String ptText = doubleToString(tempX, graph.X_STEP);
				tempWidth = g.getFontMetrics().stringWidth(ptText);
				if(tempWidth > (int) ((graph.X_MAX-graph.X_MIN)/(graph.X_STEP * graph.NUM_FREQ) * graph.X_PIXEL)){
					tempFreq = (int) Math.round(((graph.X_MAX-graph.X_MIN)/(graph.X_STEP))/((graph.X_SIZE)/tempWidth));
					tempFreq++;
					if (tempFreq > graph.NUM_FREQ){
						graph.NUM_FREQ = tempFreq;
					}
				}

				setLineSize(graph.LINE_SIZE_DEFAULT);
				drawLineSeg(tempX, 2 * graph.LINE_SIZE * graph.Y_PIXEL, tempX, -2
						* graph.LINE_SIZE * graph.Y_PIXEL, Color.BLACK, g);
				tempX += graph.X_STEP;
			}while (tempX <= graph.X_MAX);
			
			tempX = Math.round(graph.X_MIN / graph.X_STEP);
			tempX *= graph.X_STEP;
			while (tempX <= graph.X_MAX) {
				String ptText = doubleToString(tempX, graph.X_STEP);
				width = g.getFontMetrics().stringWidth(ptText);
				g.setColor(Color.white);
				g.fillRect(gridxToScreen(tempX) - (width/2) - numberInset,
						gridyToScreen(0) - height - numberAndAxisSpace - 2 * numberInset,
						width + numberInset * 2, height + 2 * numberInset);
				g.setColor(Color.black);
				g.drawString(ptText, gridxToScreen(tempX) - (width/2) ,
						gridyToScreen(0) - numberAndAxisSpace - numberInset);
//				}
				tempX += (graph.X_STEP * graph.NUM_FREQ);
				
			}

			int thickness = (int) (graph.LINE_SIZE * graph.DOC_ZOOM_LEVEL);
			int yGridOrigin = gridyToScreen(0);
			Graphics2D g2d = (Graphics2D)g; 
			g2d.setStroke(new BasicStroke(thickness));

			g.setColor(Color.BLACK);	

			g.drawLine(graph.X_PIC_ORIGIN - OVERHANG, yGridOrigin,
					graph.X_PIC_ORIGIN + graph.X_SIZE + OVERHANG, yGridOrigin);
			g2d.setStroke(new BasicStroke());

			int heightAboveAxis = (int)(5 * (graph.DOC_ZOOM_LEVEL));
			int distBeyondAxis = (int)(5 * (graph.DOC_ZOOM_LEVEL));
			Polygon p = new Polygon();
			p.addPoint(gridxToScreen(graph.X_MIN) - distBeyondAxis - OVERHANG, yGridOrigin);
			p.addPoint(gridxToScreen(graph.X_MIN) + heightAboveAxis - OVERHANG, yGridOrigin + heightAboveAxis);
			p.addPoint(gridxToScreen(graph.X_MIN) + heightAboveAxis - OVERHANG, yGridOrigin - heightAboveAxis);
			g.fillPolygon(p);

			p.reset();
			p.addPoint(gridxToScreen(graph.X_MAX) + distBeyondAxis + OVERHANG, yGridOrigin);
			p.addPoint(gridxToScreen(graph.X_MAX) - heightAboveAxis + OVERHANG, yGridOrigin + heightAboveAxis);
			p.addPoint(gridxToScreen(graph.X_MAX) - heightAboveAxis + OVERHANG, yGridOrigin - heightAboveAxis);
			g.fillPolygon(p);

			g.setFont(oldFont);
		}

		private boolean numbersClose(double num1, double num2){
			if ( Math.abs( num1 - num2) < .00001){
				return true;
			}
			return false;
		}
		
		private String doubleToString(double d, double round){
			String ptText;
			if ( numbersClose(d % round, 0)){
				d = d / round * round;
			}
			else if ( numbersClose( d % round, round)){
				d = ((d / round) + 1 ) * round;
			}
			if ( ((Double)d).toString().length() > 5){
				ptText = String.format("%.4G", d);
			}
			else{
				if ( (int) d == d){
					ptText = (int) d + ""; 
				}
				else{
					ptText = d + "";
				}
			}
			return ptText;
		}

	}

}

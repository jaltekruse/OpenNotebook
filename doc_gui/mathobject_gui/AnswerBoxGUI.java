package doc_gui.mathobject_gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import doc.mathobjects.AnswerBoxObject;

public class AnswerBoxGUI extends MathObjectGUI<AnswerBoxObject> {

	
	public void drawMathObject(AnswerBoxObject object, Graphics g, Point pageOrigin,
			float zoomLevel) {
		
		int xOrigin = (int) (pageOrigin.getX() + object.getxPos() * zoomLevel);
		int yOrigin = (int) (pageOrigin.getY() + object.getyPos() * zoomLevel);
		int width = (int) (object.getWidth() * zoomLevel);
		int height = (int) (object.getHeight() * zoomLevel);
		
		if ( ! object.getStudentAnswer().equals("")){
			Font f = g.getFont();
			
			g.setColor(new Color(180, 255, 100));
			g.fillRect(xOrigin, yOrigin, width, height);
	
			float fontSize = object.getFontSize() * zoomLevel;
			
		    String message = object.getStudentAnswer();
			g.setFont(f.deriveFont(fontSize));
			
			g.setColor(Color.BLACK);
		    Graphics2D graphics2D = (Graphics2D)g;
		    
		    GraphicsEnvironment.getLocalGraphicsEnvironment();
		    AttributedString messageAS = new AttributedString(message);
		    messageAS.addAttribute(TextAttribute.FONT, g.getFont());
		    AttributedCharacterIterator messageIterator = messageAS.getIterator();
		    FontRenderContext messageFRC = graphics2D.getFontRenderContext();
		    LineBreakMeasurer messageLBM = new LineBreakMeasurer(messageIterator, messageFRC);
	
		    Insets insets = new Insets(2,2,2,2);
		    float wrappingWidth = width - insets.left - insets.right;
		    float x = xOrigin + insets.left;
		    float y = yOrigin + insets.top;
	
		    while (messageLBM.getPosition() < messageIterator.getEndIndex()) {
		      TextLayout textLayout = messageLBM.nextLayout(wrappingWidth);
		      y += textLayout.getAscent();
		      textLayout.draw(graphics2D, x, y);
		      y += textLayout.getDescent() + textLayout.getLeading();
		      x = xOrigin + insets.left;
		    }

			g.setFont(f);
		}
		else{
			g.setColor(new Color(230, 230, 255));
			g.fillRect(xOrigin, yOrigin, width, height);
		}
		g.setColor(Color.BLACK);
		g.drawRect(xOrigin, yOrigin, width, height);
	}
}

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
		ScaledSizeAndPosition sap = getSizeAndPositionWithFontSize(object, pageOrigin,
				zoomLevel, object.getFontSize());

    // TODO - decide how extra whitespace should be handled, should it always be stored?
    // students may use it to format a multi-line answer
    // although useful whitespace will likely not coming at the very beginning or very end
    // of an answer
		if ( ! object.getStudentAnswer().trim().equals("")){
			Font f = g.getFont();
      g.setColor(new Color(150, 210, 255));
			g.fillRect(sap.getxOrigin(), sap.getyOrigin(), sap.getWidth(), sap.getHeight());
	
		  String message = object.getStudentAnswer();
			g.setFont(f.deriveFont(sap.getFontSize()));
			
			g.setColor(Color.BLACK);
		    Graphics2D graphics2D = (Graphics2D)g;
		    
		    GraphicsEnvironment.getLocalGraphicsEnvironment();
		    AttributedString messageAS = new AttributedString(message);
		    messageAS.addAttribute(TextAttribute.FONT, g.getFont());
		    AttributedCharacterIterator messageIterator = messageAS.getIterator();
		    FontRenderContext messageFRC = graphics2D.getFontRenderContext();
		    LineBreakMeasurer messageLBM = new LineBreakMeasurer(messageIterator, messageFRC);
	
		    Insets insets = new Insets(2,2,2,2);
		    float wrappingWidth = sap.getWidth() - insets.left - insets.right;
		    float x = sap.getxOrigin() + insets.left;
		    float y = sap.getyOrigin() + insets.top;
	
		    while (messageLBM.getPosition() < messageIterator.getEndIndex()) {
		      TextLayout textLayout = messageLBM.nextLayout(wrappingWidth);
		      y += textLayout.getAscent();
		      textLayout.draw(graphics2D, x, y);
		      y += textLayout.getDescent() + textLayout.getLeading();
		      x = sap.getxOrigin() + insets.left;
		    }

			g.setFont(f);
		}
		else{
      g.setColor(new Color(230, 230, 230));
			g.fillRect(sap.getxOrigin(), sap.getyOrigin(), sap.getWidth(), sap.getHeight());
		}
		g.setColor(Color.BLACK);
		g.drawRect(sap.getxOrigin(), sap.getyOrigin(), sap.getWidth(), sap.getHeight());
	}
}

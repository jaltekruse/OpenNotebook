/*
 * This file is part of an application developed by Open Education Inc.
 * The source code of the entire project is the exclusive property of
 * Open Education Inc. If you have received this file in error please
 * inform the project leader at altekrusejason@gmail.com to report where
 * the file was found and delete it immediately. 
 */

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

import doc.attributes.BooleanAttribute;
import doc.mathobjects.TextObject;

public class TextObjectGUI extends MathObjectGUI<TextObject>{

	public void drawMathObject(TextObject object, Graphics g, Point pageOrigin,
			float zoomLevel) {
		ScaledSizeAndPosition sap = getSizeAndPositionWithFontSize(object, pageOrigin, zoomLevel, object.getFontSize());

		if ( ! object.getText().equals("")){
			Font f = g.getFont();

			String message = object.getText();
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

			try{
				TextLayout textLayout;
				System.out.println();
				while (messageLBM.getPosition() < messageIterator.getEndIndex()) {
					textLayout = messageLBM.nextLayout(wrappingWidth);
					System.out.println(textLayout.getCharacterCount());
					y += textLayout.getAscent();
					if ( object.getAlignment().equals(TextObject.LEFT)){
						textLayout.draw(graphics2D, x , y);
					}
					else if (object.getAlignment().equals(TextObject.RIGHT)){
						textLayout.draw(graphics2D, x + (float) (wrappingWidth - textLayout.getBounds().getWidth()) , y);
					}
					else{//centered
						textLayout.draw(graphics2D, x + (float) (wrappingWidth - textLayout.getBounds().getWidth())/2 , y);
					}

					y += textLayout.getDescent() + textLayout.getLeading();
					x = sap.getxOrigin() + insets.left;
				}
			} catch(Exception e){
				System.out.println("error with text rendering");
			}

			object.setHeight((int) ((y - sap.getyOrigin()) / zoomLevel));

			g.setFont(f);
		}
		else{
			// draw the black box around the text box if nothing is in it
			g.setColor(Color.BLACK);
			g.drawRect(sap.getxOrigin(), sap.getyOrigin(), (int) (object.getWidth() * zoomLevel)
					, (int) (object.getHeight() * zoomLevel));
		}
		if ( ((BooleanAttribute)object.getAttributeWithName(TextObject.SHOW_BOX)).getValue()){
			g.setColor(Color.BLACK);
			g.drawRect(sap.getxOrigin(), sap.getyOrigin(), sap.getWidth(), sap.getHeight());
		}
	}

	// Not used, an older alternative implementation of how to render text
	public void drawMathObject(TextObject object, Graphics g, Point pageOrigin,
			float zoomLevel, boolean unused) {
		Font f = g.getFont();

		g.setColor(Color.BLACK);
		ScaledSizeAndPosition sap = getSizeAndPositionWithFontSize(object, pageOrigin, zoomLevel, object.getFontSize());

		Graphics2D g2d = (Graphics2D)g;
		g2d.setFont(g.getFont().deriveFont(sap.getFontSize()));
		//draw lines of text with loop

		int textyPos = sap.getyOrigin() + g.getFontMetrics().getHeight() + (int) (3 * zoomLevel);
		int textxPos = sap.getxOrigin() + (int) (3 * zoomLevel);
		String s = object.getText();
		g.drawString(s, textxPos, textyPos);
		textyPos += g.getFontMetrics().getHeight();

		if ( ((BooleanAttribute)object.getAttributeWithName("showBox")).getValue()){
			g.setColor(Color.BLUE);
			g.drawRect(sap.getxOrigin(), sap.getyOrigin(), sap.getWidth(), sap.getHeight());
		}

		g.setFont(f);
	}

}

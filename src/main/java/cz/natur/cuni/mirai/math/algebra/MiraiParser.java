/* MiraiParser.java
 * =========================================================================
 * This file is part of the Mirai Math TN - http://mirai.sourceforge.net
 *
 * Copyright (C) 2008-2009 Bea Petrovicova
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 */
package cz.natur.cuni.mirai.math.algebra;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import cz.natur.cuni.mirai.math.controller.MathController;
import cz.natur.cuni.mirai.math.model.MathFormula;
import cz.natur.cuni.mirai.math.model.MathModel;

public class MiraiParser {

	public static final String MIRAI = "MiraiMath";
	public static final String FORMULA = "Formula";
	public static final String LABEL = "Label";

	public static void load(MathModel model, String filename) throws Exception {
		
		System.out.println("MiraiParser.load("+filename+")");

		Element root = new SAXBuilder().build(
			new FileInputStream(filename), "UTF-8").getRootElement();
		// keyboard input, characters and operators
		for (Object element : root.getChildren()) {
			String name = ((Element) element).getName();

			if (name.equals(FORMULA)) {
				String text = ((Element) element).getText();
				model.addElement(parse(model, text));

			} else if (name.equals(LABEL)) {
				String label = ((Element) element).getText();
				model.addElement(label);
			}
		}
		model.setModified(false);
		// validate model
		model.firstFormula();
	}

	public static boolean canPaste(String text) {
		return text!=null && text.length()>0 && text.indexOf("<math>") < 0;
	}

	public static MathFormula[] paste(MathModel model, String text) {
		ArrayList<MathFormula> tokens = new ArrayList<MathFormula>(); 
		while(text.indexOf('\n') >= 0) {
			String token = text.substring(0, text.indexOf('\n') +1).trim();
			if(token.length()>0) {
				MathFormula formula = parse(model, token);
				if(formula.getRootComponent().size()>0) {
					tokens.add(formula);
				}
			}
			text = text.substring(text.indexOf('\n') +1);
		}
		String token = text.trim();
		if(token.length()>0) {
			MathFormula formula = parse(model, token);
			if(formula.getRootComponent().size()>0) {
				tokens.add(formula);
			}
		}
		return tokens.toArray(new MathFormula[tokens.size()]);
	}

	public static MathFormula parse(MathModel model, String text) {

		MathFormula newFormula = MathFormula.newFormula(model.getMetaModel());
		MathController controller = new MathController() {
			public void update() {
			}
		};
		controller.setFormula(newFormula);

		boolean unbracedScript = false;
		for(int i=0;i<text.length();i++) {
			// do escape
			if('~' == text.charAt(i)) {
				controller.escSymbol();
				continue;
			}
			// skip script opening brace
			if(text.charAt(i) == '(' && i > 0 && 
				(text.charAt(i-1) == '^' || text.charAt(i-1) == '_')) {
				continue;
			}
			// initiate non-braced mode
			if(text.charAt(i) != '(' && i > 0 && 
				(text.charAt(i-1) == '^' || text.charAt(i-1) == '_')) {
				unbracedScript = true;
			}
			// complete non-braced mode
			if(unbracedScript && (text.charAt(i) == ')' ||
				model.getMetaModel().isOperator(""+text.charAt(i)) )) {
				unbracedScript = false;
				controller.keyTyped(')');				
			}
			controller.keyTyped(text.charAt(i));
		}
		
		return newFormula;
	}
}

/* MathInputController.java
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
package cz.natur.cuni.mirai.math.controller;

import java.awt.event.KeyEvent;

import cz.natur.cuni.mirai.math.meta.MetaComponent;
import cz.natur.cuni.mirai.math.meta.MetaFunction;
import cz.natur.cuni.mirai.math.meta.MetaCharacter;
import cz.natur.cuni.mirai.math.model.MathCharacter;
import cz.natur.cuni.mirai.math.model.MathFormula;

abstract public class MathInputController extends MathController {

	public MathFormula cut() {
		MathFormula formula = super.cut();
		update();
		return formula;
	}

	public void paste(MathFormula formula) {
		super.paste(formula);
		update();
	}

	public void toolbarInput(MetaComponent meta) {
		if(meta instanceof MetaCharacter) {
			currentField.addArgument(currentOffset, new MathCharacter(formula, (MetaCharacter)meta));
			currentOffset++;
		} else if(meta instanceof MetaFunction) {
			if("^".equals(meta.getName()) || "_".equals(meta.getName())) {
				newScript(meta.getName());
			} else {
				newFunction(meta.getName());				
			}
		} else if(meta instanceof MetaCharacter) {
			newCharacter((MetaCharacter)meta);
		}
		update();
		setToolbarVisible(-1);
	}

	public void keyPressed(int keyCode, int modifiers) {

		switch(keyCode) {

			case KeyEvent.VK_F1:
				setToolbarVisible(0);
				break;
			case KeyEvent.VK_F2:
				setToolbarVisible(1);
				break;
			case KeyEvent.VK_F3:
				setToolbarVisible(2);
				break;
			case KeyEvent.VK_F4:
				setToolbarVisible(3);
				break;

			case KeyEvent.VK_ESCAPE:
				escSymbol();
				update();
				break;

			case KeyEvent.VK_HOME:
				firstField();
				update();
				break;
			case KeyEvent.VK_END:
				lastField();
				update();
				break;
			case KeyEvent.VK_LEFT:
				prevCharacter();
				update();
				break;
			case KeyEvent.VK_RIGHT:
				nextCharacter();
				update();
				break;	
		}
	}
	
	abstract public void setToolbarVisible(int index);

	abstract public void update();
}

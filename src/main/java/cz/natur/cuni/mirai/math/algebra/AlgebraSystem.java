/* AlgebraSystem.java
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

import cz.natur.cuni.mirai.math.editor.JMathWorkbook;
import cz.natur.cuni.mirai.math.model.MathFormula;

/** 
 * Algebra System interface provides means of 
 * communication with an algebra system.
 * 
 * serialize() parses MathFormula into String/
 * 
 * evaluate() evaluates MathFormula in AS.
 * Result to be inserted into workbook.
 * 
 * name of syntax xml file. The file should be 
 * in the 'cz.natur.cuni.mirai.math.meta' package.
 * However it does not have to be in the mirai jar.
 */

public interface AlgebraSystem {

	/** Initialize algebra system. 
	 * @throws Exception */
	void run(String command) throws Exception;
	
	/** Serialize formula into string. */
	String serialize(MathFormula formula);

	/** Evaluate formula. */
	void evaluate(MathFormula formula) throws Exception;
	
	/** Active workbook. */
	void setWorkbook(JMathWorkbook workbook);
	
	/** Disconnect engine. */	
	void exit();
}

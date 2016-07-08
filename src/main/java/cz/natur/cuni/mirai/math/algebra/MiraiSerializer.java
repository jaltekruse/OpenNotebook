/* MiraiSerializer.java
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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import cz.natur.cuni.mirai.math.model.MathFormula;
import cz.natur.cuni.mirai.math.model.MathModel;

public class MiraiSerializer {
	
	public static void save(MathModel model, String filename) throws IOException {
		OctaveSerializer serializer = new OctaveSerializer();
		serializer.mirai=true;

		System.out.println("MiraiSerializer.save("+filename+")");

		BufferedWriter writer = new BufferedWriter(
			new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
		writer.write("<?xml version='1.0' encoding='UTF-8'?>"); writer.newLine(); 
		writer.write("<!-- This file was written by Mirai -"); writer.newLine();
		writer.write("     http://mirai.sourceforge.net -->"); writer.newLine(); 
		writer.write("<MiraiMath ver=\"0.5\">"); writer.newLine();

		for(int i=0;i<model.size();i++) {
			if(model.getElement(i) instanceof MathFormula) {
				MathFormula formula = (MathFormula)model.getElement(i);
				String text = serializer.serialize(formula);
				writer.write("  <Formula>"+text+"</Formula>"); writer.newLine();

			} else if(model.getElement(i) instanceof String) {
				String text = (String)model.getElement(i);
				if(!">".equals(text) || !"".equals(text)) {
					writer.write("  <Label>"+text+"</Label>"); writer.newLine();
				}
			}
		}

		writer.write("</MiraiMath>"); writer.newLine();
		writer.flush();
		writer.close();
		
		model.setModified(false);
	}

	public static String copy(MathFormula formula) {
		return serialize(formula);
	}

	public static String serialize(MathFormula formula) {
		OctaveSerializer serializer = new OctaveSerializer();
		serializer.mirai=true;
		return serializer.serialize(formula);
	}
}

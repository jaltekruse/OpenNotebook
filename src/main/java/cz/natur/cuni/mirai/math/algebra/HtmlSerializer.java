/* HtmlSerializer.java
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import cz.natur.cuni.mirai.math.model.MathFormula;
import cz.natur.cuni.mirai.math.model.MathModel;

public class HtmlSerializer {

	public static void save(MathModel model, String filename, String enc)
			throws IOException {

		System.out.println("HtmlSerializer.save("+filename+","+enc+")");

		String pathname = filename.substring(0, filename.lastIndexOf(".html"));
		String dirname = pathname.substring(filename.lastIndexOf(File.separator)+1);

		new File(pathname+"-files").mkdir();
		
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(filename), enc));
		writer.write("<html><head><title></title></head>");
		writer.newLine();
		writer.write("<body><font size=-1>");
		writer.newLine();

		for (int i = 0; i < model.size(); i++) {
			if (model.getElement(i) instanceof MathFormula) {
				MathFormula formula = (MathFormula) model.getElement(i);
				PngSerializer.save(formula, pathname+"-files"+File.separator+"formula"+i+".png");
				writer.write("<img src=\""+dirname+"-files"+File.separator+"formula"+i+".png\"><br>");
				writer.newLine();
			}
			if (model.getElement(i) instanceof String) {
				String text = (String) model.getElement(i);
				writer.write(text+"<br>");
				writer.newLine();
			}
		}

		writer.write("</font></body></html>");
		writer.newLine();
		writer.flush();
		writer.close();
	}
}

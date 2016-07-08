/* OctavePlugin.java
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import cz.natur.cuni.mirai.math.editor.JMathWorkbook;
import cz.natur.cuni.mirai.math.model.MathFormula;

public class OctavePlugin implements AlgebraSystem {

	private boolean skip = true;
	private PrintWriter input = null;
	private JMathWorkbook notebook;
	private OctaveSerializer serializer = new OctaveSerializer();
	private int workbookIndex = -1;

	public void run(String command) throws Exception {
			
		Process p = null;
		Runtime r = Runtime.getRuntime();
		String[] cmd = new String[] {command, "-i"};
		p = r.exec(cmd);

		InputStream errStream = p.getErrorStream();
		new Output(errStream);

		InputStream inStream = p.getInputStream();
		new Output(inStream);

		input = new PrintWriter(new BufferedWriter(
				new OutputStreamWriter(p.getOutputStream())));
	}

	public void setWorkbook(JMathWorkbook notebook) {
		this.notebook = notebook;
	}

	public String serialize(MathFormula formula) {
		String serializedFormula = serializer.serialize(formula);
		return serializedFormula;
	}

	public void evaluate(MathFormula formula) throws Exception {
		skip = false;
		workbookIndex = formula.getModelIndex();
		String serializedFormula = serialize(formula);
		System.out.println("octave:> " + serializedFormula+"\n");
		input.write(serializedFormula + "\n");
		input.flush();
	}

	public void exit() {
		if(input!=null) {
			input.write("exit\n");		
			input.flush();
		}
	}

	private void displayText(String output) {
		System.out.println(output);

		if (notebook != null && !skip) {
			if (output.indexOf('\n') >= 0) {
				workbookIndex++;
				notebook.getModel().addElement(
						workbookIndex<notebook.getModel().size()?
							workbookIndex:notebook.getModel().size(), 
						output.substring(0, output.indexOf('\n') +1));

				output = output.substring(output.indexOf('\n') +1);
				displayText(output);

			} else {
				if(!"".equals(output)) {
					workbookIndex++;
					notebook.getModel().addElement(
							workbookIndex<notebook.getModel().size()?
								workbookIndex:notebook.getModel().size(), 
							output);
				}
			}
		}
	}

	class Output extends Thread {
		InputStream is;

		Output(InputStream is) {
			this.is = is;
			start();
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					line = line.replaceAll("octave(\\.exe)?:[0-9]*>[ ]*", "");
					displayText(line);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
}

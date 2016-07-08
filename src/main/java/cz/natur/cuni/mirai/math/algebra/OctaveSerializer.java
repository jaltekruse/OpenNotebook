/* OctaveSerializer.java
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
import java.util.ArrayList;

import cz.natur.cuni.mirai.math.model.MathArray;
import cz.natur.cuni.mirai.math.model.MathBraces;
import cz.natur.cuni.mirai.math.model.MathComponent;
import cz.natur.cuni.mirai.math.model.MathFormula;
import cz.natur.cuni.mirai.math.model.MathFunction;
import cz.natur.cuni.mirai.math.model.MathCharacter;
import cz.natur.cuni.mirai.math.model.MathSequence;
import cz.natur.cuni.mirai.math.model.MathModel;

public class OctaveSerializer {

	private int counter=0;
	private ArrayList<String> stack = new ArrayList<String>();

	// serialization to file or clip-board
	boolean mirai=false; 
	// use dot operators within function definitions
	boolean dottize=false;
	// jmathlib v0.9: incompatibilities
	boolean jmathlib=false;

	public static void save(MathModel model, String filename, String enc) throws IOException {
		OctaveSerializer serializer = new OctaveSerializer();

		System.out.println("OctaveSerializer.save("+filename+","+enc+")");

		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(filename), enc));
		for(int i=0;i<model.size();i++) {
			if(model.getElement(i) instanceof MathFormula) {
				MathFormula formula = (MathFormula)model.getElement(i);
				String text = serializer.serialize(formula);
				writer.newLine();
				writer.write(text);
				writer.newLine();

			} else if(model.getElement(i) instanceof String) {
				String text = (String)model.getElement(i);
				writer.write(text);
				writer.newLine();
			}
		}			
		writer.flush();
		writer.close();
	}

	public String serialize(MathFormula formula) {
		StringBuffer buffer = new StringBuffer();
		serialize(formula.getRootComponent(), buffer);
		return buffer.toString();
	}

	private void serialize(MathComponent container, StringBuffer buffer) {
		if(container instanceof MathCharacter) {
			serialize((MathCharacter)container, buffer);

		} else if(container instanceof MathSequence) {
			serialize((MathSequence)container, buffer);

		} else if(container instanceof MathArray) {
			serialize((MathArray)container, buffer);

		} else if(container instanceof MathBraces) {
			serialize((MathBraces)container, buffer);

		} else if(container instanceof MathFunction) {
			serialize((MathFunction)container, buffer);
		}
	}

	private void serialize(MathArray array, StringBuffer buffer) {
		buffer.append(array.getOpen().getCasName());
		for(int i=0;i<array.rows();i++) {
			for(int j=0;j<array.columns();j++) {
				serialize(array.getArgument(i,j), buffer);				
				if(j+1<array.columns()) {
					buffer.append(array.getField().getCasName());
				} else if(i+1<array.rows()) {
					buffer.append(array.getRow().getCasName());
				}
			}
		}
		buffer.append(array.getClose().getCasName());
	}

	private void serialize(MathBraces braces, StringBuffer buffer) {
			buffer.append(braces.getClassif()==MathBraces.APOSTROPHES ? "\"" : "(");
			serialize(braces.getArgument(0), buffer);
			buffer.append(braces.getClassif()==MathBraces.APOSTROPHES ? "\"" : ")");
	}

	private void serialize(MathFunction function, StringBuffer buffer) {
		if(!mirai && function.getFormula().getMetaModel().isGeneral(function.getName())) {

			if("_".equals(function.getName())) {

				// skip subscript when used as index
				if(!(function.getArgument(0).size() == 1 &&
					function.getArgument(0).getArgument(0) instanceof MathArray)) {

					buffer.append(function.getName());
				}
				serialize(function.getArgument(0), buffer);

			} else if("^".equals(function.getName())) {
				// use dot operators within function definitions
				if(dottize) {
					buffer.append(".");
				}
				buffer.append(function.getCasName());
				serialize(function.getArgument(0), buffer);

			} else if("sqrt".equals(function.getName())) {
				buffer.append(function.getCasName());
				buffer.append('(');
				serialize(function.getArgument(0), buffer);
				buffer.append(')');

			} else if("nthroot".equals(function.getName())) {
				buffer.append(function.getCasName());
				buffer.append("(");
				serialize(function.getArgument(0), buffer);
				buffer.append(",");
				serialize(function.getArgument(1), buffer);
				buffer.append(")");

			} else if("frac".equals(function.getName())) {
				buffer.append('(');
				serialize(function.getArgument(0), buffer);
				// use dot operators within function definitions
				if(dottize) {
					buffer.append(".");
				}
				buffer.append(function.getCasName());
				serialize(function.getArgument(1), buffer);
				buffer.append(')');

			} else if("factorial".equals(function.getName())) {
				// jmathlib v0.9: incompatibility
				if(jmathlib) {
					boolean hasOperator = function.getArgument(0).hasOperator();
					if(hasOperator) {
						buffer.append('(');
					}
					serialize(function.getArgument(0),buffer);
					if(hasOperator) {
						buffer.append(')');
					}
					buffer.append(function.getCasName());
				} else {
					buffer.append(function.getCasName());
					buffer.append('(');
					serialize(function.getArgument(0),buffer);
					buffer.append(')');
				}

			} else if("sum".equals(function.getName()) || "prod".equals(function.getName())) {

				counter++;
				int id = counter;
				boolean dottizeBackup = dottize;
				dottize=true;

				StringBuffer args = new StringBuffer();
				for(int i=0;i<stack.size();i++) {
					args.append(stack.get(i));
					if(i+1<stack.size()) {
						args.append(",");
					}
				}
				String arguments = args.toString();

				stack.add(serialize(function.getArgument(0)));
				String cycle = aggregate(function.getName()+id, 
							arguments,
							serialize(function.getArgument(0)),
							serialize(function.getArgument(1)),
							serialize(function.getArgument(2)),
							function.getArgument(3));
				stack.remove(stack.size()-1);
				dottize = dottizeBackup;

				buffer.insert(0, cycle+"\n");
				buffer.append(function.getName()+id+"("+arguments+")");

			} else if("int".equals(function.getName())) {
				//buffer.append("[v, ier, nfun, err] = ");
				buffer.append(function.getCasName());
				buffer.append("(");
				MathSequence contents = function.getArgument(2);
				// if apostrophes simply serialize
				if(contents.size()==1 && contents.getArgument(0) instanceof MathBraces &&
					((MathBraces)contents.getArgument(0)).getClassif() == MathBraces.APOSTROPHES) {

					serialize(function.getArgument(2), buffer);

				// otherwise define new temporary function
				} else {
					counter++;
					int id = counter;
					boolean dottizeBackup = dottize;
					dottize=true;
					stack.add(serialize(function.getArgument(3)));
					String definition = function("func"+id, 
							serialize(function.getArgument(3)),
							function.getArgument(2));
					buffer.insert(0, definition+"\n");
					stack.remove(stack.size()-1);
					dottize = dottizeBackup;
					buffer.append("\"func"+id+"\"");
				}
				buffer.append(",");
				serialize(function.getArgument(0), buffer);
				buffer.append(",");
				serialize(function.getArgument(1), buffer);
				buffer.append(")");

			} else if("function".equals(function.getName())) {

				// use dot operators within function definitions
				boolean dottizeBackup = dottize;
				dottize=true;
				stack.add(serialize(function.getArgument(1)));
				String definition = function(
						serialize(function.getArgument(0)), 
						serialize(function.getArgument(1)),
						function.getArgument(2));
				stack.remove(stack.size()-1);
				dottize = dottizeBackup;
				buffer.append(definition);

			} else {

				// fall-back for unimplemented functions
				buffer.append("error \"Function not implemented.\"; ");				
			}

		} else {
			buffer.append((mirai?function.getName():function.getCasName())+"(");
			for(int i=0;i<function.size();i++) {

				// use dot operators within matrix type parameters
				boolean dottizeBackup = dottize;				
				if("matrix".equals(function.getArgumentType(i))) {
					dottize=true;
				}
				serialize(function.getArgument(i),buffer);
				if(i+1 < function.size()) {
					buffer.append(",");					
				}
				dottize = dottizeBackup;
			}
			buffer.append(")");			
		}
	}

	private void serialize(MathSequence sequence, StringBuffer buffer) {
				// use braces for sequences containing operators and scripts
		boolean addBraces = sequence.hasOperator() && 
				// no braces, if parent are braces
				!(sequence.getParent() instanceof MathBraces) &&
				// no braces, if parent is regular function
				!(sequence.getParent() instanceof MathFunction && (
					((MathFunction)sequence.getParent()).getFormula().getMetaModel().isFunction(
							((MathFunction)sequence.getParent()).getName()) ||
					"sqrt".equals(((MathFunction)sequence.getParent()).getName()) ||
					"nthroot".equals(((MathFunction)sequence.getParent()).getName()) ||
					"factorial".equals(((MathFunction)sequence.getParent()).getName()) )) &&
				// no braces, if in mirai mode and parent is function
				!(mirai && sequence.getParent() instanceof MathFunction) &&
				// no braces, if no parent exists ...
				sequence.getParent()!=null;

		if(addBraces) {
			// when necessary add braces
			buffer.append("(");
		}

		// jmathlib v0.9: empty field causes infinite loop
		if(jmathlib && sequence.size()==0) {
			buffer.append(" NaN ");
		}

		for(int i=0; i<sequence.size(); i++) {
			if(sequence.getArgument(i) instanceof MathCharacter) {
				String charName = ((MathCharacter)sequence.getArgument(i)).getName();
				
				if(mirai) {
					// use default spelling when serializing to file or clip-board
					buffer.append(charName);
					// escape multiple-character symbol / operator names
					if(mirai && charName.length()>1 && (sequence.isSymbol(i) || sequence.isOperator(i))) {
						buffer.append("~");
					}
				} else {

					// use dot operators within function definitions
					// unary fails "-".equals(charName) || "+".equals(charName) || 
					if(dottize && ("*".equals(charName) || "/".equals(charName))) {
						buffer.append(".");
					}

					// do not override i,j in in words
					if(("i".equals(charName) || "j".equals(charName)) && 
						(sequence.isCharacter(i-1) || sequence.isCharacter(i+1))) {

						buffer.append(charName);
					} else {

						// use CAS spelling when serializing for math engine
						buffer.append(((MathCharacter)sequence.getArgument(i)).getCasName());
					}
				}
			} else {
				serialize(sequence.getArgument(i),buffer);
			}
		}

		if(addBraces) {
			// when necessary add braces
			buffer.append(")");
		}
	}

	private String serialize(MathSequence sequence) {
		StringBuffer buffer = new StringBuffer();
		serialize(sequence, buffer);
		return buffer.toString();		
	}

	private String aggregate(String name, String vs, String v, String fm, String to, MathSequence x) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("function val="+name+"("+vs+") ");
		buffer.append(v+"="+fm+";val=");
		buffer.append(name.startsWith("sum") ? "0;" : "1;");
		buffer.append(" while("+v+"<="+to+")");
		buffer.append("val=val");
		buffer.append(name.startsWith("sum") ? "+" : ".*");
		serialize(x,buffer);
		buffer.append(";"+v+"++;");
		buffer.append("end");
		buffer.append("; endfunction");
		return buffer.toString();
	}

	private String function(String name, String v, MathSequence x) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("function val="+name+"("+v+") val=");
		serialize(x,buffer);
		buffer.append("; endfunction");
		return buffer.toString();
	}
}

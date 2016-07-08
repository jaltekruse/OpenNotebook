/* TeXSerializer.java
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

import cz.natur.cuni.mirai.math.meta.MetaModel;
import cz.natur.cuni.mirai.math.model.MathArray;
import cz.natur.cuni.mirai.math.model.MathBraces;
import cz.natur.cuni.mirai.math.model.MathComponent;
import cz.natur.cuni.mirai.math.model.MathContainer;
import cz.natur.cuni.mirai.math.model.MathFormula;
import cz.natur.cuni.mirai.math.model.MathFunction;
import cz.natur.cuni.mirai.math.model.MathCharacter;
import cz.natur.cuni.mirai.math.model.MathSequence;
import cz.natur.cuni.mirai.math.model.MathModel;

public class TeXSerializer {

	private MathSequence currentField = null;
	private int currentOffset = 0;

	private boolean jmathtex = true;
	private boolean currentBraces = true;

	public static void save(MathModel model, String filename, String enc) throws IOException {
		TeXSerializer serializer = new TeXSerializer();
		serializer.jmathtex=false;

		System.out.println("TeXSerializer.save("+filename+","+enc+")");
		
		BufferedWriter writer = new BufferedWriter(
			new OutputStreamWriter(new FileOutputStream(filename), enc));
		// tex document
		writer.write("\\documentclass[fleqn,a4paper,12pt]{article}"); 
		writer.newLine();
		writer.write("\\usepackage[top=2cm, bottom=2cm, left=2cm, right=2cm]{geometry}"); 
		writer.newLine();
		if(enc.equals("UTF-8") || enc.equals("UTF8")) {
			writer.write("\\usepackage[utf8]{inputenc}"); 
			writer.newLine();
		}
		// required by matrices
		writer.write("\\usepackage{amsmath}"); writer.newLine();
		// no indentation
		writer.write("\\setlength{\\mathindent}{0in}"); 
		writer.newLine();
		writer.write("\\setlength{\\parindent}{0in}"); 
		writer.newLine();
		writer.write("\\setlength{\\parskip}{0in}"); 
		writer.newLine();
		writer.write("\\begin{document}"); 
		writer.newLine();

		for(int i=0;i<model.size();i++) {
			if(model.getElement(i) instanceof MathFormula) {
				MathFormula formula = (MathFormula)model.getElement(i);
				String text = serializer.serialize(formula);

				writer.write("\\[ "+text+" \\]"); writer.newLine(); 

			} else if(model.getElement(i) instanceof String) {
				String text = (String)model.getElement(i);
				text = text.replace("\\", "\\\\");
				text = text.replace("^", "\\^");
				text = text.replace("&", "\\&");
				text = text.replace("$", "\\$");
				text = text.replace("%", "\\%");
				text = text.replace("#", "\\#");
				text = text.replace("_", "\\_");
				text = text.replace("{", "\\{");
				text = text.replace("}", "\\}");

				if(i==0 || (i>0 && !(model.getElement(i-1) instanceof String))) {
					writer.write("\\begin{verbatim}"); 
					writer.newLine(); 
				}

				writer.write(text); 
				writer.newLine(); 

				if(i+1==model.size() || (i+1<model.size() && !(model.getElement(i+1) instanceof String))) {
					writer.write("\\end{verbatim}"); 
					writer.newLine(); 
				}

			}
		}

		writer.write("\\end{document}"); 
		writer.newLine(); 
		writer.flush();
		writer.close();
	}

	public static String copy(MathFormula formula) {
		TeXSerializer serializer = new TeXSerializer();
		serializer.jmathtex=false;
		return "<math>"+serializer.serialize(formula)+"</math>";
	}

	public String serialize(MathFormula formula) {
		return serialize(formula,null,0);
	}

	public String serialize(MathFormula formula, MathSequence currentField, int currentOffset) {
		this.currentField = currentField;
		this.currentOffset = currentOffset;		
		currentBraces=currentField!=null;
		StringBuffer buffer = new StringBuffer();
		serialize(formula.getRootComponent(), buffer);
		return buffer.toString();
	}

	public String serialize(MathContainer container, MathSequence currentField, int currentOffset) {
		this.currentField = currentField;
		this.currentOffset = currentOffset;		
		StringBuffer buffer = new StringBuffer();
		serialize(container, buffer);
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
		if(jmathtex && MetaModel.MATRIX.equals(array.getName())) {
			// jmathlib does not implement matrix
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

		} else {
			buffer.append(array.getOpen().getTexName());
			for(int i=0;i<array.rows();i++) {
				for(int j=0;j<array.columns();j++) {
					serialize(array.getArgument(i,j), buffer);
					if(j+1<array.columns()) {
						buffer.append(array.getField().getTexName());
					} else if(i+1<array.rows()) {
						buffer.append(array.getRow().getTexName());
					}
				}
			}
			buffer.append(array.getClose().getTexName());
		}
	}

	private void serialize(MathBraces braces, StringBuffer buffer) {
		if(braces.getClassif() == MathBraces.REGULAR) {
			buffer.append('(');
			serialize(braces.getArgument(0), buffer);
			buffer.append(')');

		} else if(braces.getClassif() == MathBraces.SQUARE) {
			buffer.append('[');
			serialize(braces.getArgument(0), buffer);
			buffer.append(']');
			
		} else if(braces.getClassif() == MathBraces.CURLY) {
			buffer.append("\\lbrace");
			serialize(braces.getArgument(0), buffer);
			buffer.append("\\rbrace");

		} else if(braces.getClassif() == MathBraces.APOSTROPHES) {
			// jmathtex v0.7: incompatibility
			buffer.append("\\ddot"+(jmathtex?" ":"{\\ }"));
			serialize(braces.getArgument(0), buffer);
			buffer.append("\\ddot"+(jmathtex?" ":"{\\ }"));
			
		} else {
			throw new ArrayIndexOutOfBoundsException(
				"Unsupported function " + braces.getClassif());
		}
	}

	private void serialize(MathFunction function, StringBuffer buffer) {
		if(function.getFormula().getMetaModel().isGeneral(function.getName())) {

			if("^".equals(function.getName()) || "_".equals(function.getName()) ) {
				MathSequence parent = (MathSequence)function.getParent();
				int index = function.getParentIndex();
				if(index==0 || (index>0 && parent.getArgument(index-1) instanceof MathCharacter &&
					((MathCharacter)parent.getArgument(index-1)).isOperator())) {
					buffer.append("{\\triangleleft}");
				}
				buffer.append(function.getName() + '{');
				serialize(function.getArgument(0), buffer);
				buffer.append('}');

			} else if("frac".equals(function.getName())) {
				buffer.append("{"+function.getTexName());
				buffer.append("{");
				serialize(function.getArgument(0), buffer);
				buffer.append("}{");
				serialize(function.getArgument(1), buffer);
				buffer.append("}}");

			} else if("sqrt".equals(function.getName())) {
				buffer.append(function.getTexName());
				buffer.append("{");
				serialize(function.getArgument(0), buffer);
				buffer.append("}");

			} else if("nthroot".equals(function.getName())) {
				buffer.append(function.getTexName());
				buffer.append('[');
				serialize(function.getArgument(1), buffer);
				buffer.append("]{");
				serialize(function.getArgument(0), buffer);
				buffer.append('}');

			} else if("sum".equals(function.getName()) || "prod".equals(function.getName())) {
				buffer.append(function.getTexName());
				buffer.append("_{");
				serialize(function.getArgument(0),buffer);
				buffer.append('=');
				serialize(function.getArgument(1),buffer);
				buffer.append("}^");
				serialize(function.getArgument(2),buffer);
				boolean addBraces = currentBraces || 
						(function.getArgument(3).hasOperator());
				if(addBraces) {
					buffer.append('(');
				}
				serialize(function.getArgument(3),buffer);
				if(addBraces) {
					buffer.append(')');
				}

			} else if("nsum".equals(function.getName()) || "nprod".equals(function.getName())) {
				buffer.append(function.getTexName());
				buffer.append("_{");
				serialize(function.getArgument(0),buffer);
				buffer.append('=');
				serialize(function.getArgument(1),buffer);
				buffer.append('}');
				boolean addBraces = currentBraces || 
						(function.getArgument(2).hasOperator());
				if(addBraces) {
					buffer.append('(');
				}
				serialize(function.getArgument(2),buffer);					
				if(addBraces) {
					buffer.append(')');
				}

			} else if("int".equals(function.getName())) {
				buffer.append(function.getTexName());
				buffer.append('_');
				serialize(function.getArgument(0),buffer);
				buffer.append('^');
				serialize(function.getArgument(1),buffer);
				buffer.append('{');
				boolean addBraces = currentBraces;
				if(addBraces) {
					buffer.append('(');
				}
				serialize(function.getArgument(2),buffer);
				// jmathtex v0.7: incompatibility
				buffer.append(" "+(jmathtex ? "\\nbsp" : "\\ ")+" d");
				serialize(function.getArgument(3),buffer);
				if(addBraces) {
					buffer.append(')');
				}
				buffer.append('}');

			} else if("nint".equals(function.getName())) {
				buffer.append(function.getTexName());
				buffer.append((jmathtex ? "_{\\nbsp}" : "")+"{");
				boolean addBraces = currentBraces;
				if(addBraces) {
					buffer.append('(');
				}
				serialize(function.getArgument(0),buffer);
				// jmathtex v0.7: incompatibility
				buffer.append(" "+(jmathtex ? "\\nbsp" : "\\ ")+" d");
				serialize(function.getArgument(1),buffer);
				if(addBraces) {
					buffer.append(')');
				}
				buffer.append('}');

			} else if("lim".equals(function.getName())) {
				// lim not implemented in jmathtex
				if(!jmathtex) {
					buffer.append("\\");
				}
				buffer.append(function.getTexName());
				buffer.append("_{");
				serialize(function.getArgument(0),buffer);
				buffer.append(" \\rightarrow ");
				serialize(function.getArgument(1),buffer);
				// jmathtex v0.7: incompatibility
				buffer.append("} "+(jmathtex ? "\\nbsp" : "\\ ")+" {");
				boolean addBraces = currentBraces || 
						(function.getArgument(2).hasOperator() &&
						function.getParent().hasOperator());
				if(addBraces) {
					buffer.append('(');
				}
				serialize(function.getArgument(2),buffer);
				if(addBraces) {
					buffer.append(')');
				}
				buffer.append('}');

			} else if("factorial".equals(function.getName())) {
				boolean addBraces = currentBraces || 
						function.getArgument(0).hasOperator();
				if(addBraces) {
					buffer.append('(');
				} 
				serialize(function.getArgument(0),buffer);
				if(addBraces) {
					buffer.append(')');
				}
				buffer.append(function.getTexName());

			} else if("function".equals(function.getName())) {
				buffer.append("\\mathrm{"+function.getTexName()+"} ");
				// jmathtex v0.7: incompatibility
				buffer.append((jmathtex?"\\nbsp":"\\ ")+" ");
				serialize(function.getArgument(0),buffer);
				buffer.append('(');
				serialize(function.getArgument(1),buffer);
				buffer.append(")=");
				boolean addBraces = currentBraces || 
						(function.getArgument(2).hasOperator() &&
						function.getParent().hasOperator());
				if(addBraces) {
					buffer.append('(');
				}
				serialize(function.getArgument(2),buffer);					
				if(addBraces) {
					buffer.append(')');
				}
			}
		} else {
			if(!jmathtex && isLatexFunction(function.getTexName())) {
				buffer.append("{\\"+function.getTexName()+"(");
			} else {
				buffer.append("{\\mathrm{"+function.getTexName()+"}(");
			}
			for(int i=0;i<function.size();i++) {
				serialize(function.getArgument(i),buffer);
				if(i+1 < function.size()) {
					buffer.append(",");					
				}
			}
			buffer.append(")}");
		} 
	}

	private void serialize(MathSequence sequence, StringBuffer buffer) {
		boolean addBraces = 
			(sequence.hasChildren() || // {a^b_c}
			 sequence.size() > 1 || // {aa}
			(sequence.size() == 1 && letterLength(sequence,0) > 1) || // {\pi} 
			(sequence.size() == 0 && sequence != currentField) || // {\triangleright}
			(sequence.size() == 1 && sequence == currentField)) && // {a|}
			(buffer.length() > 0 && buffer.charAt(buffer.length()-1) != '{');

		if(addBraces) {
			// when necessary add curly braces
			buffer.append('{');
		}
		
		if(sequence.size()==0) {
			if (sequence == currentField) {
				buffer.append('|');
			} else {
				if(sequence.getParent() == null || /*symbol.getParent() instanceof MathOperator ||*/
				   (sequence.getParent() instanceof MathFunction &&
				   sequence.getParentIndex() == sequence.getParent().getInsertIndex())) {
					buffer.append("\\triangleleft");
				} else {
					buffer.append("\\triangleright");
				}
			}
		} else {
			if (sequence == currentField) {
				if(currentOffset>0) {
					serialize(sequence, buffer, 0, currentOffset);
				}
				buffer.append('|');
				if(currentOffset < sequence.size()) {
					serialize(sequence, buffer, currentOffset, sequence.size());
				}
			} else {
				serialize(sequence, buffer, 0, sequence.size());
			}
		}

		if(addBraces) {
			// when necessary add curly braces
			buffer.append('}');
		}
	}

	private void serialize(MathSequence sequence, StringBuffer buffer, int from, int to) {
		for(int i=from; i<to; i++) {
			if(sequence.getArgument(i) instanceof MathCharacter) {

				// jmathtex v0.7: incompatibility
				if(" ".equals(((MathCharacter)sequence.getArgument(i)).getName())) {
					buffer.append((jmathtex?"\\nbsp":"\\ ")+" ");
				} else {
					buffer.append(((MathCharacter)sequence.getArgument(i)).getTexName());
				}

				// safety space after operator / symbol
				if(((MathCharacter)sequence.getArgument(i)).isOperator() ||
				   ((MathCharacter)sequence.getArgument(i)).isSymbol()) {
					buffer.append(' ');
				}
			} else {
				serialize(sequence.getArgument(i),buffer);
			}
		}
	}

	private int letterLength(MathSequence symbol, int i) {
		if(symbol.getArgument(i) instanceof MathCharacter) {
			return ((MathCharacter)symbol.getArgument(i)).getTexName().length();
		} else {
			return 2;
		}
	}

	private boolean isLatexFunction(String texName) {
		for(int i=0; i<latexFunctions.length; i++) {
			if(latexFunctions[i].equals(texName)) {
				return true;
			} else if(texName.length() > latexFunctions[i].length()+1 &&
					  texName.startsWith(latexFunctions[i]) &&
					 (texName.charAt(latexFunctions[i].length())==' ' ||
					  texName.charAt(latexFunctions[i].length())=='^' ||
					  texName.charAt(latexFunctions[i].length())=='_' ||
					  texName.charAt(latexFunctions[i].length())=='{') ) {
				return true;
			}
		}
		return false;
	}

	private static final String latexFunctions[] = {
		"sin", "cos", "tan", 
		"sec", "csc", "cot", 
		"sinh", "cosh", "tanh", "coth", 
		"lim", "limsup", "liminf", 
		"min", "max", "sup", "exp", 
		"ln", "lg", "log", "ker", "deg", 
		"gcd", "det", "hom", "arg", "dim", 
		"sum", "prod", "int", "pmod"
	};
	
}

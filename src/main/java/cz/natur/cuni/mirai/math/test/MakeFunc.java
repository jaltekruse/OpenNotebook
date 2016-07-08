package cz.natur.cuni.mirai.math.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import cz.natur.cuni.mirai.math.meta.MetaComponent;
import cz.natur.cuni.mirai.math.meta.MetaFunction;
import cz.natur.cuni.mirai.math.meta.MetaGroup;
import cz.natur.cuni.mirai.math.meta.MetaModel;
import cz.natur.cuni.mirai.math.meta.MetaParameter;
import cz.natur.cuni.mirai.math.meta.MetaSymbol;

public class MakeFunc {
	public static void main(String[] args) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter("general.html"));
		writer.write("<html><head><title>Mirai Math 0.5 General</title></head>");
		writer.newLine();
		writer.write("<body><font size=-1>");
		writer.newLine();		
		writer.newLine();

		writer.write("<br><br><b>General</b><br><br>");
		writer.newLine();
		writer.newLine();

		writer.write("<table border=\"0\" cellpadding=\"5\" cellspacing=\"0\">");
		writer.newLine();

		//SymbolIcon.setType(ToolbarFolder.SMALL);
		MetaModel metaModel = new MetaModel("Octave.xml");
		MetaGroup groups[] = metaModel.getGroups(MetaModel.GENERAL);
		for(int j=0;j<groups.length;j++) {
			MetaComponent functions[] = groups[j].getComponents();
			for(int i=0;i<functions.length;i++) {
				writer.write("<tr>");
				try {
					MetaFunction function = (MetaFunction) functions[i];
					writer.write("<td >");
					writer.write("<img width=\"\" height=\"\" src=\"img/general/"+function.getIcon()+"\" />");
					writer.write("</td>");
					writer.write("<td >");
					writer.write("<font color=red size=-1>"+function.getSignature()+"</font><font size=-1> - "+function.getDescription()+"</font>");
					writer.write("</td>");
					System.out.println(functions[i].getName());
				} catch (IOException x) {
					x.printStackTrace();
				}
				writer.write("</tr>");
				writer.newLine();
			}
		}

		writer.write("</table>");
		writer.newLine();
		writer.newLine();

		writer.write("<br><br><b>Operators</b><br><br>");
		writer.newLine();
		writer.newLine();

		writer.write("<table border=\"0\" cellpadding=\"6\" cellspacing=\"0\">");
		writer.newLine();

		groups = metaModel.getGroups(MetaModel.OPERATORS);
		MetaComponent operators[] = groups[0].getComponents();
		for(int i=0;i<operators.length/4;i++) {
			writer.write("<tr>");
			for(int k=0;k<4;k++) {
				try {
					if(operators.length>i+(12*k)) {
					MetaSymbol operator = (MetaSymbol) operators[i+(12*k)];
					writer.write("<td ><font size=-1>");
					writer.write("<img width=\"16\" height=\"16\" src=\"img/operators/"+operator.getIcon()+"\" />");
					writer.write(" "+operator.getTip());
					writer.write("</font></td>");
					System.out.println(operators[i+(12*k)].getName());
					}
				} catch (IOException x) {
					x.printStackTrace();
				}
			}
			writer.write("</tr>");
			writer.newLine();
		}

		writer.write("</table>");
		writer.newLine();
		writer.newLine();

		writer.write("<br><br><b>Symbols</b><br><br>");
		writer.newLine();
		writer.newLine();

		writer.write("<table border=\"0\" cellpadding=\"6\" cellspacing=\"0\">");
		writer.newLine();

		groups = metaModel.getGroups(MetaModel.SYMBOLS);
		MetaComponent symbols[] = groups[0].getComponents();
		for(int i=0;i<symbols.length/4;i++) {
			writer.write("<tr>");
			for(int k=0;k<4;k++) {
				try {
					if(symbols.length>i+(12*k)) {
					MetaSymbol symbol = (MetaSymbol) symbols[i+(12*k)];
					writer.write("<td><font size=-1>");
					writer.write("<img width=\"16\" height=\"16\" src=\"img/symbols/"+symbol.getIcon()+"\" />");
					writer.write(" "+symbol.getTip());
					writer.write("</font></td>");
					System.out.println(symbols[i+(12*k)].getName());
					}
				} catch (IOException x) {
					x.printStackTrace();
				}
			}
			writer.write("</tr>");
			writer.newLine();
		}

		writer.write("</table>");
		writer.newLine();
		writer.newLine();

		writer.write("<br><br><b>Functions</b><br><br>");
		writer.newLine();
		writer.newLine();

		writer.write("<table border=\"0\" cellpadding=\"6\" cellspacing=\"0\">");
		writer.write("<tr>");
		writer.newLine();

		groups = metaModel.getGroups(MetaModel.FUNCTIONS);
		for(int j=0;j<groups.length;j++) {
			MetaComponent functions[] = groups[j].getComponents();
			for(int i=0;i<functions.length;i++) {
				try {
					MetaFunction function = (MetaFunction) functions[i];
					writer.write("<td >");
					//writer.write("<a href=\"functions/"+function.getName()+".html\" ");
					writer.write("<a onclick=\"return popitup('functions/"+function.getName()+".html')\" />");
					writer.write("<font color=red size=-1>");
					writer.write(function.getSignature());
					writer.write("</font>");
					writer.write("</a>");
					if(function.getDescription()!=null) {
						writer.write("<font size=-1> - "+function.getDescription()+"</font>");
					}
					writer.write("</td>");
					//save(function, function.getName()+".html");
					System.out.println(function.getName());
				} catch (IOException x) {
					x.printStackTrace();
				}
				writer.write("</tr>");
				writer.newLine();
			}
		}

		writer.write("</tr>");
		writer.write("</table>");
		writer.newLine();
		writer.newLine();

		writer.write("</font></body></html>");
		writer.flush();
		writer.newLine();
		writer.close();
	}

	public static void save(MetaFunction function, String filename) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		writer.write("<html><head><title>"+function.getSignature()+"</title></head>");
		writer.newLine();
		writer.write("<body><font size=-1>");
		writer.newLine();

		writer.write("<font color=\"red\">"+function.getSignature()+"</font><br>");
		writer.write(function.getDescription()+"<br><br>");
		writer.newLine();

		for(int i=0;i<function.size();i++) {
			MetaParameter parameter = function.getParameter(i);
			parameter.getName();
			if(parameter.getType()!=null) {
				writer.write(parameter.getName() + " - "+parameter.getType());
				if(parameter.getDescription()!=null) {
					writer.write(parameter.getType() + " - "+parameter.getDescription()+"<br>" );
				} else {
					writer.write("<br>");
				}
				writer.newLine();
			}
		}

		writer.write("</font></body></html>");
		writer.flush();
		writer.close();
	}
}

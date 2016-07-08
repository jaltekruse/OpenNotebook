package cz.natur.cuni.mirai.math.test;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;

import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXFormula;
import be.ugent.caagt.jmathtex.TeXIcon;

public class TestTeX {

	public static void main(String[] args) {
		JFrame frame = new JFrame("TestSwing.java");

		//Icon icon = new TestSwing();
		//TeXFormula formula = new TeXFormula("\\int_{t=0}^{2\\pi}\\frac{\\sqrt{t}}{1 + \\mathrm{cos}^2 t}\\nbsp dt");

		// scripts and cursor test
		//TeXFormula formula = new TeXFormula("x|y^{2\\pi}_{sub}");
		//TeXFormula formula = new TeXFormula("x|y^{j^{2}_{arg}}_{sub^{ab}_{cd}}");
		//##TeXFormula formula = new TeXFormula("\\sideset{_1^2}{_3^4}\\prod_a^b");
		//##TeXFormula formula = new TeXFormula("{}_1^2\\pi_3^4");
		//##TeXFormula formula = new TeXFormula("\\overset{\\alpha}{\\omega}");

		// frac and sqrt
		//TeXFormula formula = new TeXFormula("\\sqrt[3]{\\frac{x|y^{j^{2}_{arg}}_{sub^{ab}_{cd}}}{\\sqrt[4]{2x+3/y}}}");
		//TeXFormula formula = new TeXFormula("2 x y ^{\\frac{a }{|}}_{b /c }");

		// lim
		//TeXFormula formula = new TeXFormula("lim_{n \\to \\infty} \\nbsp x_n");

		// int and sum
		//TeXFormula formula = new TeXFormula("\\sum_{i=0}^{n} a_i \\int_{a}^{b} t^3 \\nbsp dt");

		// cos
		//TeXFormula formula = new TeXFormula("cos(x)");

		// eq
		//TeXFormula formula = new TeXFormula("a=(b+c)");

		//##TeXFormula formula = new TeXFormula("\\left ( \\frac{1}{2} \\right )");
		//##TeXFormula formula = new TeXFormula("\\big( \\Big( \\bigg( \\Bigg(");

		// () {}
		//TeXFormula formula = new TeXFormula("[\\frac{(b+c)}{x|y^{j^{2}}+2}]");
		//TeXFormula formula = new TeXFormula("\\lbrace\\frac{(b+c)}{x|y^{j^{2}}+\\triangleright}\\rbrace");

		// matrix
		//##TeXFormula formula = new TeXFormula("\\begin{matrix}  x & y \\\\  z & v \\end{matrix}");
		/*##TeXFormula formula = new TeXFormula(
				"\\pmatrix{a_{11} & a_{12} & \\ldots & a_{1n}\\cr "+
				"a_{21} & a_{22} & \\ldots & a_{2n}\\cr "+
				"\\vdots & \\vdots & \\ddots & \\vdots\\cr "+
				"a_{m1} & a_{m2} & \\ldots & a_{mn}} "+
				"\\qquad\\qquad "+
				"\\pmatrix{y_1\\cr\\vdots\\cr y_k}"); */

		TeXFormula formula = new TeXFormula(
				"\\frac{\\triangleleft}{\\triangleright} \\nbsp"+
				"{\\triangleleft}^{\\triangleright}_{\\triangleright} \\nbsp" +
				"\\sqrt[\\triangleright]{\\triangleleft} \\nbsp"+
				"\\triangleright ^{\\prime} \\frac"+
				"{\\prod_{\\triangleright=\\triangleright}^{\\triangleright}{\\triangleleft \\triangleright} \\nbsp}"+
				"{\\sum_{\\triangleright=\\triangleright}^{\\triangleright}{\\triangleleft \\triangleright} \\nbsp}"
		);

		TeXIcon icon = new TeXIcon(TeXConstants.STYLE_DISPLAY, 18);
		icon.setTeXFormula(formula);

		BufferedImage image = new BufferedImage(icon.getIconWidth(), icon
				.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		icon.paintIcon(new JLabel(), g2, 0, 0); // component can't be null

		final JLabel label = new JLabel();
		label.setIcon(icon);
		frame.getContentPane().add(label);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}

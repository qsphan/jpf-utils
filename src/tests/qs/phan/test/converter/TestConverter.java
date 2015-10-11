package qs.phan.test.converter;

import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.PathCondition;

public class TestConverter {
	
	public String convertPC(PathCondition pc) {

		String prefixPC = convertConstraint(pc.header);
		if (pc.header.and != null)
			prefixPC = "( and " + prefixPC + " )";
		return cleanExpr(prefixPC);

	}

	public String convertConstraint(Constraint c1) {
		Constraint c = c1;
		String prefix = "";
		PrintExpressionVisitor converter = new PrintExpressionVisitor();
		while (true) {
			
			c.getLeft().accept(converter);
			c.getRight().accept(converter);

			c = c.and;
			if (c == null)
				break;

		}

		return prefix;
	}

	public static String cleanExpr(String str) {

		String tmp1 = str.replaceAll("\\[(.*?)\\]", ""); // remove e.g.
															// [-1000000]
		String tmp2 = tmp1.replaceAll("CONST_", "");

		return tmp2;
	}
}

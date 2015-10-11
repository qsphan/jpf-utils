package qs.phan.spf.common;

/*
 * Utilities for Symbolic Execution
 */
public class SymExUtils {

	public static String cleanExpr(String str) {

		String tmp1 = str.replaceAll("\\[(.*?)\\]", ""); // remove e.g.
															// [-1000000]
		String tmp2 = tmp1.replaceAll("CONST_", "");

		return tmp2;
	}

	public static String cleanSymbol(String str) {

		return str.replaceAll("\\[(.*?)\\]", ""); // remove e.g. [-1000000]
	}

	public static String cleanConst(String str) {
		return str.replaceAll("CONST_", "");
	}
	
}

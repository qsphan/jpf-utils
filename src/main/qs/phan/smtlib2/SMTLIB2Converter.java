package qs.phan.smtlib2;

import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.util.HashSet;
import java.util.Hashtable;

public class SMTLIB2Converter {
	
	private static int counter = 0;
	
	private HashSet<SymbolicVariable> setOfSymVars; // relevant variables
	private Hashtable<String,String> impVals; // important variables
	
	public SMTLIB2Converter(HashSet<SymbolicVariable> set, Hashtable<String,String> imp){
		setOfSymVars = set;
		impVals = imp;
	}
	
	private static String genBooleanVariableName(){
		return "SPF_BOOL_VAR" + counter++;
	}
	
	private String getBooleanAbstraction(String t_atom){
		String abst = impVals.get(t_atom);
		if (abst == null){
			abst = genBooleanVariableName();
			impVals.put(t_atom, abst);
		}
		return abst;
	}
	
	public String convertPC(PathCondition pc){
		
		String prefixPC = convertConstraint(pc.header);
		if (pc.header.and != null)
			prefixPC = "( and " + prefixPC + " )";
		return cleanExpr(prefixPC);
		
	}
	
	public String convertConstraint(Constraint c1){
		Constraint c = c1;
		String prefix = "";
		while (true){
			String comp = c.getComparator().toString();
			String current = "";
			
			SMTLIB2Visitor converter = new SMTLIB2Visitor(setOfSymVars);
			c.getLeft().accept(converter);
			String left = converter.getExpression();
			
			SMTLIB2Visitor converter1 = new SMTLIB2Visitor(setOfSymVars);
			c.getRight().accept(converter1);
			String right = converter1.getExpression();
			
			switch(c.getComparator()){
			
			case EQ: // convert "==" to "=" for Z3
				current = "= " + left + " " + right;
				break;	
			case NE:
				current = "not (= " + left + " " + right + ")";
				break;
			default:
				current = comp + " " + left + " " + right;
				break;		
			}	
			String abst = getBooleanAbstraction(cleanExpr(current));
			prefix = abst + " " + prefix;
			
			c = c.and;
			if (c == null)	break;
				
		}
			
		return prefix;
	}
	
	public static String cleanExpr(String str){
		
		String tmp1 = str.replaceAll("\\[(.*?)\\]", ""); // remove e.g. [-1000000]
		String tmp2 = tmp1.replaceAll("CONST_", "");
		
		return tmp2;
	}
}


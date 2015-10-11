package qs.phan.spf;

import gov.nasa.jpf.symbc.bytecode.BytecodeUtils.VarType;
import gov.nasa.jpf.symbc.numeric.BinaryLinearIntegerExpression;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.ConstraintExpressionVisitor;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.RealConstant;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.numeric.SymbolicReal;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Stack;

import qs.phan.smtlib2.SymbolicVariable;

public class Smtlib2Converter extends ConstraintExpressionVisitor{
	
		protected Stack<String> stack;
		
		protected static int counter = 0;
		protected HashSet<SymbolicVariable> setOfSymVars; // relevant variables
		protected Hashtable<String,String> impVars; // important variables

		public Smtlib2Converter(){
			stack = new Stack<String>();
			setOfSymVars = new HashSet<SymbolicVariable>();
			impVars = new Hashtable<String,String>();
		}
		
		public String convertPC(PathCondition pc){
			
			String prefixPC = convertConstraint(pc.header);
			if (pc.header.and != null)
				prefixPC = "( and " + prefixPC + " )";
			return prefixPC;
			
		}
		
		public Smtlib2Converter(HashSet<SymbolicVariable> set, Hashtable<String,String> imp){
			stack = new Stack<String>();
			setOfSymVars = set;
			impVars = imp;
		}
		
		protected static String genBooleanVariableName(){
			return "SPF_BOOL_VAR" + counter++;
		}
		
		protected String getBooleanAbstraction(String t_atom){
			String abst = impVars.get(t_atom);
			if (abst == null){
				abst = genBooleanVariableName();
				impVars.put(t_atom, abst);
			}
			return abst;
		}
		
		public String convertConstraint(Constraint c){

			StringBuilder prefixBuilder = new StringBuilder();
			
			while (true){
				c.accept(this);				
				String abst = getBooleanAbstraction(cleanExpr(stack.peek()));
				prefixBuilder.insert(0, abst + " ");
				c = c.and;
				if (c == null)	break;
				stack.pop();	
			}
				
			return prefixBuilder.toString();
		}

		public String getExpression() {
			return cleanExpr(stack.peek());
		}

		@Override
		public void postVisit(Constraint constraint) {
			String l = stack.pop();
			String r = stack.pop();
			
			switch (constraint.getComparator()) {
			case EQ:
				stack.push("= " + l + " " + r);
				break;
			case NE:
				stack.push("not (= " + l + " " + r + ")");
				break;
			default:
				stack.push(constraint.getComparator().toString() + " " + l + " " + r);
				break;
			}
		}

		@Override
		public void postVisit(BinaryLinearIntegerExpression expression) {
			String l;
			String r;
			switch (expression.getOp()) {
			case PLUS:
				r = stack.pop();
				l = stack.pop();
				stack.push("( + " + l + " " + r + ")");
				break;
			case MINUS:
				r = stack.pop();
				l = stack.pop();
				stack.push("( - " + l + " " + r + ")");
				break;
			case MUL:
				r = stack.pop();
				l = stack.pop();
				stack.push("( * " + l + " " + r + ")");
				break;
			default:
				System.out.println("SolverTranslator : unsupported operation "
						+ expression.getOp());
				throw new RuntimeException();
			}
		}

		@Override
		public void postVisit(IntegerConstant constant) {
			stack.push(constant.toString());
		}

		@Override
		public void postVisit(RealConstant constant) {
			stack.push(constant.toString());
		}

		@Override
		public void postVisit(SymbolicReal expr) {
			setOfSymVars.add(new SymbolicVariable(VarType.REAL,expr.toString()));
			stack.push(expr.toString());
		}
		
		@Override
		public void postVisit(SymbolicInteger node) {
			setOfSymVars.add(new SymbolicVariable(VarType.INT,node.toString()));
			stack.push(node.toString());
		}
		
		public static String cleanExpr(String str){
			
			String tmp1 = str.replaceAll("\\[(.*?)\\]", ""); // remove e.g. [-1000000]
			String tmp2 = tmp1.replaceAll("CONST_", "");
			
			return tmp2;
		}
		
}

package qs.phan.smtlib2;

import gov.nasa.jpf.symbc.bytecode.BytecodeUtils.VarType;
import gov.nasa.jpf.symbc.concolic.FunctionExpression;
import gov.nasa.jpf.symbc.mixednumstrg.SpecialIntegerExpression;
import gov.nasa.jpf.symbc.mixednumstrg.SpecialRealExpression;
import gov.nasa.jpf.symbc.numeric.BinaryLinearIntegerExpression;
import gov.nasa.jpf.symbc.numeric.BinaryNonLinearIntegerExpression;
import gov.nasa.jpf.symbc.numeric.BinaryRealExpression;
import gov.nasa.jpf.symbc.numeric.ConstraintExpressionVisitor;
import gov.nasa.jpf.symbc.numeric.Expression;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.IntegerExpression;
import gov.nasa.jpf.symbc.numeric.MathRealExpression;
import gov.nasa.jpf.symbc.numeric.RealConstant;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.numeric.SymbolicReal;
import gov.nasa.jpf.symbc.string.DerivedStringExpression;
import gov.nasa.jpf.symbc.string.StringConstant;
import gov.nasa.jpf.symbc.string.StringExpression;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.symbc.string.SymbolicStringBuilder;

import java.util.HashSet;

public class SMTLIB2Visitor extends ConstraintExpressionVisitor{
	
	String str="";
	boolean stop = false;
	private HashSet<SymbolicVariable> setOfSymVals;
	
	public SMTLIB2Visitor(HashSet<SymbolicVariable> set){
		setOfSymVals = set;
	}
	
	public void reset(){
		str = "";
	}
	
	public String getExpression(){
		return str;
	}
	
	public void preVisit(Expression expr) {
		System.out.println("Expression");
	}

	@Override
	public void preVisit(IntegerExpression expr) {
		if(stop == true)
			return;
		if (expr instanceof BinaryLinearIntegerExpression){
			BinaryLinearIntegerExpression expr1 = (BinaryLinearIntegerExpression)expr;
			preVisit(expr1);
			return;
		}
		if (expr instanceof SymbolicInteger){
			SymbolicInteger expr1 = (SymbolicInteger)expr;
			preVisit(expr1);
			return;
		}
		if (expr instanceof IntegerConstant){
			IntegerConstant expr1 = (IntegerConstant)expr;
			preVisit(expr1);
			return;
		}
	}

	@Override
	public void preVisit(BinaryLinearIntegerExpression expr) {
				
		if(stop == true)
			return;
		
		SMTLIB2Visitor left = new SMTLIB2Visitor(setOfSymVals);
		left.preVisit(expr.getLeft());
		
		SMTLIB2Visitor right = new SMTLIB2Visitor(setOfSymVals);
		right.preVisit(expr.getRight());
		
		str = "( " + expr.getOp().toString() + " " + left.getExpression() + " " + right.getExpression() + " )";
		stop = true;
	}
	
	@Override
	public void preVisit(IntegerConstant expr) {
		
		if(stop == true)
			return;
	
		str = " " + expr.toString() + " ";
	}

	@Override
	public void preVisit(SymbolicInteger expr) {
		
		if(stop == true)
			return;
		
		str = " " + expr.toString() + " ";
		setOfSymVals.add(new SymbolicVariable(VarType.INT,expr.toString()));
	}

	@Override
	public void preVisit(RealExpression expr) {
		if(stop == true)
			return;
		if (expr instanceof BinaryRealExpression){
			BinaryRealExpression expr1 = (BinaryRealExpression)expr;
			preVisit(expr1);
			return;
		}
		if (expr instanceof SymbolicReal){
			SymbolicReal expr1 = (SymbolicReal)expr;
			preVisit(expr1);
			return;
		}
		if (expr instanceof RealConstant){
			RealConstant expr1 = (RealConstant)expr;
			preVisit(expr1);
			return;
		}
	}

	@Override
	public void preVisit(BinaryRealExpression expr) {
		if(stop == true)
			return;
		
		SMTLIB2Visitor left = new SMTLIB2Visitor(setOfSymVals);
		left.preVisit(expr.getLeft());
		
		SMTLIB2Visitor right = new SMTLIB2Visitor(setOfSymVals);
		right.preVisit(expr.getRight());
		
		str = "( " + expr.getOp().toString() + " " + left.getExpression() + " " + right.getExpression() + " )";
		stop = true;
	}

	@Override
	public void preVisit(RealConstant expr) {
		if(stop == true)
			return;
	
		str = " " + expr.toString() + " ";
	}

	@Override
	public void preVisit(SymbolicReal expr) {
		if(stop == true)
			return;
		
		str = " " + expr.toString() + " ";
		setOfSymVals.add(new SymbolicVariable(VarType.REAL,expr.toString()));
	}

	// public void preVisit(NonLinearIntegerExpression expr) {}
	// public void preVisit(LinearIntegerExpression expr) {}

	public void preVisit(SpecialRealExpression expr) {
		System.out.println("SpecialRealExpression");
	}
	
	public void preVisit(FunctionExpression expr) {
		System.out.println("FunctionExpression");
	}

	public void preVisit(MathRealExpression expr) {
		System.out.println("MathRealExpression");
	}
	
	public void preVisit(BinaryNonLinearIntegerExpression expr) {
		System.out.println("BinaryNonLinearIntegerExpression");
	}

	public void preVisit(SpecialIntegerExpression expr) {
		System.out.println("SpecialIntegerExpression");
	}
	
	public void preVisit(StringExpression expr) {
		System.out.println("StringExpression");
	}

	public void preVisit(DerivedStringExpression expr) {
		System.out.println("DerivedStringExpression");
	}

	public void preVisit(StringConstant expr) {
		System.out.println("StringConstant");
	}

	public void preVisit(StringSymbolic expr) {
		System.out.println("StringSymbolic");
	}

	public void preVisit(SymbolicStringBuilder expr) {
		System.out.println("SymbolicStringBuilder");
	}
}
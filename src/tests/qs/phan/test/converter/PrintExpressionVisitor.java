package qs.phan.test.converter;

import gov.nasa.jpf.symbc.concolic.FunctionExpression;
import gov.nasa.jpf.symbc.mixednumstrg.SpecialIntegerExpression;
import gov.nasa.jpf.symbc.mixednumstrg.SpecialRealExpression;
import gov.nasa.jpf.symbc.numeric.BinaryLinearIntegerExpression;
import gov.nasa.jpf.symbc.numeric.BinaryNonLinearIntegerExpression;
import gov.nasa.jpf.symbc.numeric.BinaryRealExpression;
import gov.nasa.jpf.symbc.numeric.ConstraintExpressionVisitor;
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

public class PrintExpressionVisitor extends ConstraintExpressionVisitor {
	
	int count = 0;
	
	@Override
	public void preVisit(IntegerExpression expr) {
		count++;
		System.out.println(count + " : IntegerExpression : ");
	}

	@Override
	public void preVisit(BinaryLinearIntegerExpression expr) {
		count++;		
		System.out.println(count + " : BinaryLinearIntegerExpression : " + expr.toString());
	}
	
	@Override
	public void preVisit(IntegerConstant expr) {
		count++;
		System.out.println(count + " : IntegerConstant : " + expr.toString());
	}

	@Override
	public void preVisit(SymbolicInteger expr) {
		count++;
		System.out.println(count + " : SymbolicInteger : " + expr.toString());
	}

	@Override
	public void preVisit(RealExpression expr) {
		count++;
		System.out.println(count + " : RealExpression : " + expr.toString());
	}

	@Override
	public void preVisit(BinaryRealExpression expr) {
		count++;
		System.out.println(count + " : BinaryRealExpression : " + expr.toString());
	}

	@Override
	public void preVisit(RealConstant expr) {
		count++;
		System.out.println(count + " : RealConstant : " + expr.toString());
	}

	@Override
	public void preVisit(SymbolicReal expr) {
		System.out.println(count + " : SymbolicReal : " + expr.toString());
	}

	// public void preVisit(NonLinearIntegerExpression expr) {}
	// public void preVisit(LinearIntegerExpression expr) {}

	public void preVisit(SpecialRealExpression expr) {
		count++;
		System.out.println(count + " : SpecialRealExpression : " + expr.toString());
	}
	
	public void preVisit(FunctionExpression expr) {
		count++;
		System.out.println(count + " : FunctionExpression : " + expr.toString());
	}

	public void preVisit(MathRealExpression expr) {
		count++;
		System.out.println(count + " : MathRealExpression : " + expr.toString());
	}
	
	public void preVisit(BinaryNonLinearIntegerExpression expr) {
		count++;
		System.out.println(count + " : BinaryNonLinearIntegerExpression : " + expr.toString());
	}

	public void preVisit(SpecialIntegerExpression expr) {
		count++;
		System.out.println(count + " : SpecialIntegerExpression : " + expr.toString());
	}
	
	public void preVisit(StringExpression expr) {
		count++;
		System.out.println(count + " : StringExpression : " + expr.toString());
	}

	public void preVisit(DerivedStringExpression expr) {
		count++;
		System.out.println(count + " : DerivedStringExpression : " + expr.toString());
	}

	public void preVisit(StringConstant expr) {
		count++;
		System.out.println(count + " : StringConstant : " + expr.toString());
	}

	public void preVisit(StringSymbolic expr) {
		count++;
		System.out.println(count + " : StringSymbolic : " + expr.toString());
	}

	public void preVisit(SymbolicStringBuilder expr) {
		count++;
		System.out.println(count + " : SymbolicStringBuilder : " + expr.toString());
	}
}

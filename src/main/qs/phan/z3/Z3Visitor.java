package qs.phan.z3;

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
import gov.nasa.jpf.symbc.numeric.Operator;
import gov.nasa.jpf.symbc.numeric.RealConstant;
import gov.nasa.jpf.symbc.numeric.RealExpression;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.numeric.SymbolicReal;
import gov.nasa.jpf.symbc.string.DerivedStringExpression;
import gov.nasa.jpf.symbc.string.StringConstant;
import gov.nasa.jpf.symbc.string.StringExpression;
import gov.nasa.jpf.symbc.string.StringSymbolic;
import gov.nasa.jpf.symbc.string.SymbolicStringBuilder;

import java.util.Hashtable;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Z3Exception;

public class Z3Visitor extends ConstraintExpressionVisitor {

	Expr z3Expr;
	boolean stop = false;
	private Hashtable<String,Expr> setOfSymVals;
	Context ctx;

	public Z3Visitor(Hashtable<String,Expr> set, Context context) {
		setOfSymVals = set;
		ctx = context;
	}

	public void reset() {
		z3Expr = null;
	}

	public Expr getExpression() {
		return z3Expr;
	}

	public void preVisit(Expression expr) {
		System.out.println("Expression");
	}

	@Override
	public void preVisit(IntegerExpression expr) {
		if (stop == true)
			return;
		if (expr instanceof BinaryLinearIntegerExpression) {
			BinaryLinearIntegerExpression expr1 = (BinaryLinearIntegerExpression) expr;
			preVisit(expr1);
			return;
		}
		if (expr instanceof SymbolicInteger) {
			SymbolicInteger expr1 = (SymbolicInteger) expr;
			preVisit(expr1);
			return;
		}
		if (expr instanceof IntegerConstant) {
			IntegerConstant expr1 = (IntegerConstant) expr;
			preVisit(expr1);
			return;
		}
	}

	@Override
	public void preVisit(BinaryLinearIntegerExpression expr) {

		if (stop == true)
			return;

		Z3Visitor left = new Z3Visitor(setOfSymVals, ctx);
		left.preVisit(expr.getLeft());

		Z3Visitor right = new Z3Visitor(setOfSymVals, ctx);
		right.preVisit(expr.getRight());

		try {
			z3Expr = binaryExpr((ArithExpr) left.getExpression(),
					(ArithExpr) right.getExpression(), expr.getOp());
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		stop = true;
	}

	private Expr binaryExpr(ArithExpr left, ArithExpr right, Operator op)
			throws Z3Exception {
		switch (op) {
		case PLUS:
			return ctx.mkAdd(new ArithExpr[] { left, right });
		case MINUS:
			return ctx.mkSub(new ArithExpr[] { left, right });
		case MUL:
			return ctx.mkMul(new ArithExpr[] { left, right });
		case DIV:
			return ctx.mkDiv(left, right);
		default:
			throw new RuntimeException(
					"## Error: BinaryRealSolution solution: l " + " op " + op
							+ " r ");
		}
	}

	public static String cleanSymbol(String str){
		
		return str.replaceAll("\\[(.*?)\\]", ""); // remove e.g. [-1000000]
	}
	
	public static String cleanConst(String str){
		return str.replaceAll("CONST_", "");
	}
	
	@Override
	public void preVisit(IntegerConstant expr) {

		if (stop == true)
			return;

		try {
			z3Expr = ctx.mkNumeral(Integer.parseInt(cleanConst(expr.toString())),
					ctx.mkIntSort());
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void preVisit(SymbolicInteger expr) {

		if (stop == true)
			return;

		String name = cleanSymbol(expr.toString());
		z3Expr = setOfSymVals.get(name);
		if(z3Expr == null){
			try {
				z3Expr = ctx.mkConst(ctx.mkSymbol(name), ctx.mkIntSort());
				setOfSymVals.put(name, z3Expr);
			} catch (Z3Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void preVisit(RealExpression expr) {
		if (stop == true)
			return;
		if (expr instanceof BinaryRealExpression) {
			BinaryRealExpression expr1 = (BinaryRealExpression) expr;
			preVisit(expr1);
			return;
		}
		if (expr instanceof SymbolicReal) {
			SymbolicReal expr1 = (SymbolicReal) expr;
			preVisit(expr1);
			return;
		}
		if (expr instanceof RealConstant) {
			RealConstant expr1 = (RealConstant) expr;
			preVisit(expr1);
			return;
		}
	}

	@Override
	public void preVisit(BinaryRealExpression expr) {
		if (stop == true)
			return;

		Z3Visitor left = new Z3Visitor(setOfSymVals, ctx);
		left.preVisit(expr.getLeft());

		Z3Visitor right = new Z3Visitor(setOfSymVals, ctx);
		right.preVisit(expr.getRight());

		try {
			z3Expr = binaryExpr((ArithExpr) left.getExpression(),
					(ArithExpr) right.getExpression(), expr.getOp());
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		stop = true;
	}

	@Override
	public void preVisit(RealConstant expr) {
		if (stop == true)
			return;

		try {
			z3Expr = ctx.mkReal(cleanConst(expr.toString()));
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void preVisit(SymbolicReal expr) {
		if (stop == true)
			return;

		String name = cleanSymbol(expr.toString());
		z3Expr = setOfSymVals.get(name);
		if(z3Expr == null){
			try {
				z3Expr = ctx.mkConst(ctx.mkSymbol(name), ctx.mkRealSort());
				setOfSymVals.put(name, z3Expr);
			} catch (Z3Exception e) {
				e.printStackTrace();
			}
		}
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
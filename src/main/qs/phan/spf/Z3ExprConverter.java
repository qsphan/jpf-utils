package qs.phan.spf;

import gov.nasa.jpf.symbc.numeric.BinaryLinearIntegerExpression;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.ConstraintExpressionVisitor;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.Operator;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.RealConstant;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;
import gov.nasa.jpf.symbc.numeric.SymbolicReal;

import java.util.Hashtable;
import java.util.Stack;

import qs.phan.spf.common.SymExUtils;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Z3Exception;

public class Z3ExprConverter extends ConstraintExpressionVisitor {

	protected Stack<Expr> stack;
	protected Hashtable<String, Expr> setOfSymVals;

	Expr z3Expr = null;
	Context ctx;

	public Z3ExprConverter(Hashtable<String, Expr> set, Context context) {
		stack = new Stack<Expr>();
		setOfSymVals = set;
		ctx = context;
	}

	public BoolExpr convertPC(PathCondition pc) {

		return convertConstraint(pc.header);
	}

	public BoolExpr convertConstraint(Constraint c) {

		BoolExpr expr = null;
		
		while (true) {
			c.accept(this);
			BoolExpr current = (BoolExpr) stack.pop();
			if(expr == null){
				expr =  current;
			}
			else{
				try {
					expr = ctx.mkAnd(new BoolExpr[] { expr, current });
				} catch (Z3Exception e) {
					e.printStackTrace();
				}
			}
			c = c.and;
			if (c == null)
				break;
		}

		return expr;
	}

	@Override
	public void postVisit(Constraint c) {

		Expr left = stack.pop();
		Expr right = stack.pop();
		try {
			switch (c.getComparator()) {
			case EQ:
				stack.push(ctx.mkEq(left, right));
				break;
			case NE:
				stack.push(ctx.mkNot(ctx.mkEq(left, right)));
				break;
			case LT:
				stack.push(ctx.mkLt((ArithExpr) left, (ArithExpr) right));
				break;
			case LE:
				stack.push(ctx.mkLe((ArithExpr) left, (ArithExpr) right));
				break;
			case GT:
				stack.push(ctx.mkGt((ArithExpr) left, (ArithExpr) right));
				break;
			case GE:
				stack.push(ctx.mkGe((ArithExpr) left, (ArithExpr) right));
				break;
			default:
				System.out.println("Unknown comparator");
			}
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
	}

	protected Expr binaryExpr(ArithExpr left, ArithExpr right, Operator op)
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

	@Override
	public void postVisit(BinaryLinearIntegerExpression expr) {
		ArithExpr left = (ArithExpr) stack.pop();
		ArithExpr right = (ArithExpr) stack.pop();

		try {
			z3Expr = binaryExpr(left, right, expr.getOp());
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		stack.push(z3Expr);
	}

	@Override
	public void postVisit(IntegerConstant expr) {
		try {
			z3Expr = ctx.mkInt(SymExUtils.cleanConst(expr.toString()));
		} catch (Z3Exception e) {
			e.printStackTrace();
		}

		stack.push(z3Expr);
	}

	@Override
	public void postVisit(RealConstant expr) {

		try {
			z3Expr = ctx.mkReal(SymExUtils.cleanConst(expr.toString()));
		} catch (Z3Exception e) {
			e.printStackTrace();
		}

		stack.push(z3Expr);
	}

	@Override
	public void postVisit(SymbolicReal expr) {
		String name = SymExUtils.cleanSymbol(expr.toString());
		z3Expr = setOfSymVals.get(name);
		if (z3Expr == null) {
			try {
				z3Expr = ctx.mkConst(ctx.mkSymbol(name), ctx.mkRealSort());
				setOfSymVals.put(name, z3Expr);
			} catch (Z3Exception e) {
				e.printStackTrace();
			}
		}
		stack.push(z3Expr);
	}

	@Override
	public void postVisit(SymbolicInteger expr) {
		String name = SymExUtils.cleanSymbol(expr.toString());
		z3Expr = setOfSymVals.get(name);
		if (z3Expr == null) {
			try {
				z3Expr = ctx.mkConst(ctx.mkSymbol(name), ctx.mkIntSort());
				setOfSymVals.put(name, z3Expr);
			} catch (Z3Exception e) {
				e.printStackTrace();
			}
		}
		stack.push(z3Expr);
	}

}

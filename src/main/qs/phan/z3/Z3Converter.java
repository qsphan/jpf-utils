package qs.phan.z3;

import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.util.Hashtable;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Z3Exception;

/*
 * Convert constraint to Z3 expression
 */
public class Z3Converter {

	private Hashtable<String, Expr> setOfSymVals;
	private Context ctx;

	public Z3Converter(Hashtable<String, Expr> set, Context context) {
		setOfSymVals = set;
		ctx = context;
	}

	public BoolExpr convertPC(PathCondition pc) throws Z3Exception {
		return convertConstraint(pc.header);
	}

	public BoolExpr convertHeadOfPC(Constraint c) throws Z3Exception {

		BoolExpr current = null;

		Z3Visitor converter = new Z3Visitor(setOfSymVals, ctx);
		c.getLeft().accept(converter);
		Expr left = converter.getExpression();

		Z3Visitor converter1 = new Z3Visitor(setOfSymVals, ctx);
		c.getRight().accept(converter1);
		Expr right = converter1.getExpression();

		switch (c.getComparator()) {

		case EQ:
			current = ctx.mkEq(left, right);
			break;
		case NE:
			current = ctx.mkNot(ctx.mkEq(left, right));
			break;
		case LT:
			current = ctx.mkLt((ArithExpr) left, (ArithExpr) right);
			break;
		case LE:
			current = ctx.mkLe((ArithExpr) left, (ArithExpr) right);
			break;
		case GT:
			current = ctx.mkGt((ArithExpr) left, (ArithExpr) right);
			break;
		case GE:
			current = ctx.mkGe((ArithExpr) left, (ArithExpr) right);
			break;
		default:
			System.out.println("Unknown comparator");
		}

		return current;
	}

	public BoolExpr convertConstraint(Constraint c1) throws Z3Exception {
		Constraint c = c1;
		BoolExpr expr = null;
		while (true) {

			BoolExpr current = null;

			Z3Visitor converter = new Z3Visitor(setOfSymVals, ctx);
			c.getLeft().accept(converter);
			Expr left = converter.getExpression();

			Z3Visitor converter1 = new Z3Visitor(setOfSymVals, ctx);
			c.getRight().accept(converter1);
			Expr right = converter1.getExpression();

			switch (c.getComparator()) {

			case EQ:
				current = ctx.mkEq(left, right);
				break;
			case NE:
				current = ctx.mkNot(ctx.mkEq(left, right));
				break;
			case LT:
				current = ctx.mkLt((ArithExpr) left, (ArithExpr) right);
				break;
			case LE:
				current = ctx.mkLe((ArithExpr) left, (ArithExpr) right);
				break;
			case GT:
				current = ctx.mkGt((ArithExpr) left, (ArithExpr) right);
				break;
			case GE:
				current = ctx.mkGe((ArithExpr) left, (ArithExpr) right);
				break;
			default:
				System.out.println("Unknown comparator");
			}

			if (expr == null)
				expr = current;
			else
				expr = ctx.mkAnd(new BoolExpr[] { expr, current });

			c = c.and;
			if (c == null)
				break;
		}

		// System.out.println("\nexpr is: " + expr.toString() + "\n");

		return expr;
	}
}

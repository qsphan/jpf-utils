package qs.phan.spf;

import gov.nasa.jpf.symbc.numeric.Constraint;

import java.util.Hashtable;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Z3Exception;

public class Z3ConverterWithBooleAbst extends Z3ExprConverter {

	protected static int counter = 0;
	
	protected Hashtable<String, BoolExpr> impVars; // important variables
	protected BoolExpr boolAbst;

	public Z3ConverterWithBooleAbst(Hashtable<String, Expr> set, Context context) {
		super(set,context);
	}
	
	/*
	 * This method needs to be called right after construction
	 */
	public void setBoolAbstStore(Hashtable<String,BoolExpr> vars, BoolExpr abst){
		impVars = vars;
		boolAbst = abst;
	}

	public BoolExpr getBooleanAbstraction() {
		return boolAbst;
	}
	
	protected static String genBooleanVariableName(){
		return "SPF_BOOL_VAR" + counter++;
	}
	
	protected BoolExpr getBooleanAbstraction(Expr expr) {
		String tmp = expr.toString();
		BoolExpr abst = impVars.get(tmp);
		if (abst == null) {
			String name = genBooleanVariableName();
			try {
				abst = ctx.mkBoolConst(name);
				boolAbst = ctx.mkAnd(new BoolExpr[] { boolAbst,
						ctx.mkEq(abst, expr) });
				impVars.put(tmp, abst);
			} catch (Z3Exception e) {
				e.printStackTrace();
			}
		}
		return abst;
	}

	@Override
	public BoolExpr convertConstraint(Constraint c) {

		BoolExpr expr = null;

		while (true) {
			c.accept(this);
			BoolExpr current = getBooleanAbstraction(stack.pop());
			if (expr == null) {
				expr = current;
			} else {
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

}

package qs.phan.spf.count;

import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.ConstraintExpressionVisitor;
import gov.nasa.jpf.symbc.numeric.PathCondition;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;

import java.util.HashSet;
import java.util.Set;

import qs.phan.spf.common.SymExUtils;

public class OmegaConverter extends ConstraintExpressionVisitor {

	protected Set<String> set;

	public OmegaConverter() {
		set = new HashSet<String>();
	}

	public String convertPcToOmegaExpr(PathCondition pc) {
		return convertConstraintToOmegaExpr(pc.header);
	}

	public String convertConstraintToOmegaExpr(Constraint c) {
		// collect symbolic integer
		Constraint tmp = c;
		while (tmp != null) {
			tmp.accept(this);
			tmp = tmp.and;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("D := {[");
		boolean isFirst = true;

		for(String symvar : set) {
			if (isFirst) {
				sb.append(symvar);
				isFirst = false;
			}
			else {
				sb.append("," + symvar);
			}
		}

		sb.append("]: " + SymExUtils.cleanExpr(c.toString()).replace("\n", " "));
		sb.append("};\nD;");
		return sb.toString();
	}

	@Override
	public void postVisit(SymbolicInteger expr) {
		set.add(SymExUtils.cleanSymbol(expr.toString()));
	}
}

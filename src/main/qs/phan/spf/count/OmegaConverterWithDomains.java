package qs.phan.spf.count;

import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.PathCondition;

import java.util.HashMap;
import java.util.Map;

import qs.phan.spf.common.SymExUtils;

public class OmegaConverterWithDomains {

	protected HashMap<String,Domain> domains;

	public OmegaConverterWithDomains(HashMap<String,Domain> d) {
		domains = d;
	}

	public String convertPcToOmegaExpr(PathCondition pc) {
		return convertConstraintToOmegaExpr(pc.header);
	}

	public String convertConstraintToOmegaExpr(Constraint c) {
		
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		StringBuilder sb3 = new StringBuilder();
		
		sb1.append("D1 := {[");
		sb2.append("D2 := {[");
		boolean isFirst = true;
		
		for (Map.Entry<String, Domain> entry : domains.entrySet()) {
			String symvar = entry.getKey();
		    Domain d = entry.getValue();
		    
		    if (isFirst) {
				sb1.append(symvar);
				sb2.append(symvar);
				sb3.append(domainToOmegaExpr(symvar,d));
				isFirst = false;
			}
			else {
				sb1.append("," + symvar);
				sb2.append("," + symvar);
				sb3.append(" && " + domainToOmegaExpr(symvar,d));
			}
		}

		sb1.append("]: " + sb3.toString() + "};");
		sb2.append("]: " + SymExUtils.cleanExpr(c.toString()).replace("\n", " "));		
		sb2.append("};\nD2 := D2 intersection D1;\nD2;");
		
		return sb1.toString() + "\n" + sb2.toString();
	}
	
	public String domainToOmegaExpr(String var, Domain d){

		if (d.getLowerBound() == null)
			return var + " <= " + d.getUpperBound();
		
		if (d.getLowerBound() == null)
			return d.getLowerBound() + " <= " + var;
		
		return d.getLowerBound() + " <= " + var + " " + d.getUpperBound();
	}

}

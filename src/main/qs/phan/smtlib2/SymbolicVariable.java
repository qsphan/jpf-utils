package qs.phan.smtlib2;

import gov.nasa.jpf.symbc.bytecode.BytecodeUtils.VarType;

public class SymbolicVariable {
	VarType type;
	String name;

	public SymbolicVariable(VarType type, String name) {
		this.type = type;
		this.name = name;
	}

	public VarType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object arg0) {
		SymbolicVariable obj = (SymbolicVariable) arg0;
		return name.equals(obj.getName());
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
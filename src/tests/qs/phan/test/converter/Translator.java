package qs.phan.test.converter;

import gov.nasa.jpf.symbc.numeric.BinaryLinearIntegerExpression;
import gov.nasa.jpf.symbc.numeric.Constraint;
import gov.nasa.jpf.symbc.numeric.ConstraintExpressionVisitor;
import gov.nasa.jpf.symbc.numeric.IntegerConstant;
import gov.nasa.jpf.symbc.numeric.SymbolicInteger;

import java.util.Stack;

public class Translator extends ConstraintExpressionVisitor {

	// private Stack<Expression> stack;
	private Stack<String> stack;

	public Translator() {
		// stack = new Stack<Expression>();
		stack = new Stack<String>();
	}

	// public Expression getExpression() {
	public String getExpression() {
		return stack.peek();
	}

	@Override
	public void postVisit(Constraint constraint) {
		// Expression l;
		// Expression r;
		String l;
		String r;
		switch (constraint.getComparator()) {
		case EQ:
			r = stack.pop();
			l = stack.pop();
			// stack.push(new Operation(Operation.Operator.EQ, l, r));
			stack.push(" = " + l + " " + r);
			break;
		case NE:
			r = stack.pop();
			l = stack.pop();
			// stack.push(new Operation(Operation.Operator.NE, l, r));
			stack.push(" != " + l + " " + r);
			break;
		case LT:
			r = stack.pop();
			l = stack.pop();
			// stack.push(new Operation(Operation.Operator.LT, l, r));
			stack.push("( < " + l + " " + r + ")");
			break;
		case LE:
			r = stack.pop();
			l = stack.pop();
			// stack.push(new Operation(Operation.Operator.LE, l, r));
			stack.push(" <= " + l + " " + r);
			break;
		case GT:
			r = stack.pop();
			l = stack.pop();
			// stack.push(new Operation(Operation.Operator.GT, l, r));
			stack.push(" > " + l + " " + r);
			break;
		case GE:
			r = stack.pop();
			l = stack.pop();
			// stack.push(new Operation(Operation.Operator.GE, l, r));
			stack.push(" >= " + l + " " + r);
			break;
		}
	}

	@Override
	public void postVisit(BinaryLinearIntegerExpression expression) {
		// Expression l;
		// Expression r;
		String l;
		String r;
		switch (expression.getOp()) {
		case PLUS:
			r = stack.pop();
			l = stack.pop();
			// stack.push(new Operation(Operation.Operator.ADD, l, r));
			stack.push("( + " + l + " " + r + ")");
			break;
		case MINUS:
			r = stack.pop();
			l = stack.pop();
			// stack.push(new Operation(Operation.Operator.SUB, l, r));
			stack.push("( - " + l + " " + r + ")");
			break;
		case MUL:
			r = stack.pop();
			l = stack.pop();
			// stack.push(new Operation(Operation.Operator.MUL, l, r));
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
		// stack.push(new IntConstant(constant.value));
		stack.push(constant.toString());
	}

	@Override
	public void postVisit(SymbolicInteger node) {
		// stack.push(new IntVariable(node.getName(), node, node._min,
		// node._max));
		stack.push(node.toString());
	}
	
	public void print(){
		for(int i = 0; i < stack.size(); i++){
			System.out.println(stack.get(i));
		}
	}
}
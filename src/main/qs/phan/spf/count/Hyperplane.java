package qs.phan.spf.count;

/*
 * Hyperplane of the form Ax <= b
 * Latte format is: b -A
 */
public class Hyperplane {	
	int b;
	int[] coefficients;
	
	public Hyperplane (int dimension) {
		assert dimension > 0;
		
		b = 0;
		coefficients = new int[dimension];
		// TODO: is it necessary ?
		for (int i = 0; i < dimension; i++) {
			coefficients[i] = 0;
		}
	}
	
	public void setB(int b){
		this.b = b;
	}
	
	public void setCoefficient(int index, int value){
		coefficients[index] = value;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(b + " ");
		for (int i = 0; i < coefficients.length; i++) {
			sb.append(coefficients[i]*(-1) + " ");
		}
		sb.setCharAt(sb.length()-1, '\n');
		return sb.toString();
	}
}

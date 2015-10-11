package qs.phan.spf.count;

public class Domain {

	// in case there is no min or max, set them to null
	private Integer min;
	private Integer max;
	private int index;
	
	public Domain(Integer min, Integer max, int index) {
		if (min == null && max == null) throw new RuntimeException("Invalid domain");
		this.min = min;
		this.max = max;
		this.index = index;
	}
	
	/*
	 * x <= b
	 */
	public Hyperplane upperBoundToHyperplane(int dimension){
		if(max == null) return null;
		
		Hyperplane h = new Hyperplane(dimension);
		h.setB(max);
		h.setCoefficient(index, 1);
		return h;
	}
	
	/*
	 * x >= b is transformed to -x <= -b
	 */
	public Hyperplane lowerBoundToHyperplane(int dimension){
		if(min == null) return null;
		
		Hyperplane h = new Hyperplane(dimension);
		h.setB(min * (-1));
		h.setCoefficient(index, -1);
		return h;
	}
	
	public Integer getLowerBound() {
		return min;
	}
	
	public Integer getUpperBound() {
		return max;
	}
}

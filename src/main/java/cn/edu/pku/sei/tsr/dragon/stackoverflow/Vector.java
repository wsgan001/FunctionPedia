package cn.edu.pku.sei.tsr.dragon.stackoverflow;

/**
 * Created by maxkibble on 2015/12/24.
 */
public class Vector {
	private String		word;
	private Double[]	vec	= new Double[105];

	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public void setVec(double val, int idx) {
		vec[idx] = val;
	}
	public Double[] getVec() {
		return vec;
	}
	public Double getVecEle(int idx) {
		return vec[idx];
	}

	public Vector() {
	}

	public double calcLen() {
		double length = 0.0;
		for (int i = 0; i < 100; i++) {
			double v = vec[i];
			length += v * v;
		}
		return Math.sqrt(length);
	}

	public Double cosineSimilarity(Vector obj) {
		Double re = 0.0;
		for (int i = 0; i < 100; i++) {
			re += obj.getVecEle(i) * vec[i];
		}
		// System.out.println(calcLen());
		double length = calcLen();
		re = re / (length * obj.calcLen());
		return re;
	}
}

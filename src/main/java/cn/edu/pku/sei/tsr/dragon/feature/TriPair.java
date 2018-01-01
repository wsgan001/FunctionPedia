package cn.edu.pku.sei.tsr.dragon.feature;

public class TriPair<A, B, C> {
	private A first;
	private B second;
	private C third;

	public TriPair(A first, B second, C third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public A getFirst() {
		return first;
	}

	public B getSecond() {
		return second;
	}

	public C getThird() {
		return third;
	}

	@Override
	public String toString() {
		return String.format("%s%s %s", third, second == null ? "" : " " + second, first);
	}

	@Override
	public int hashCode() {
		int code = 0;
		if (first != null) code ^= first.hashCode();
		if (second != null) code ^= second.hashCode();
		if (third != null) code ^= third.hashCode();
		return code;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof TriPair)) return false;
		TriPair<A, B, C> other = (TriPair<A, B, C>) obj;
		if (first == null) {
			if (other.first != null) return false;
		} else if (!first.equals(other.first)) return false;
		if (second == null) {
			if (other.second != null) return false;
		} else if (!second.equals(other.second)) return false;
		if (third == null) {
			if (other.third != null) return false;
		} else if (!third.equals(other.third)) return false;
		return true;
	}
}

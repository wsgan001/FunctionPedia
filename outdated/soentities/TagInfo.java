package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity;

import java.io.Serializable;
import java.util.HashSet;

public class TagInfo implements Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7647758507404462972L;
	private String				tag;

	public TagInfo(String tag) {
		this.tag = tag;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean equalsTag(TagInfo otherTag) {
		if (otherTag != null && this.tag != null && otherTag.getTag() != null) {
			return this.tag.equals(otherTag.getTag());
		}
		return false;
	}

	// public boolean equals(Object o) {
	// if (o != null && (o instanceof TagInfo)) {
	// TagInfo otherTag = (TagInfo) o;
	// if (this.tag != null && otherTag.getTag() != null)
	// return this.tag.equals(otherTag.getTag());
	// }
	// return super.equals(o);
	// }
	//
	// public int hashCode() {
	// if (tag != null)
	// return this.getClass().hashCode() + tag.hashCode();
	// else
	// return super.hashCode();
	// }

	public static void main(String[] args) {
		TagInfo t1 = new TagInfo("tag");
		TagInfo t2 = new TagInfo("other");
		TagInfo t3 = new TagInfo("tag");
		Object t4 = new TagInfo("tag");
		test t5 = new test("tag");
		System.out.println(t1);
		System.out.println(t2);
		System.out.println(t3);// same as t1
		System.out.println(t4);// same as t1
		System.out.println(t5);
		HashSet<Object> ts = new HashSet<>();
		ts.add(t1);
		ts.add(t2);
		ts.add(t3);
		ts.add(t4);
		ts.add(t5);
		System.out.println(ts);
		System.out.println(t1.equals(t1));
		System.out.println(t1.equals(t2));
		System.out.println(t1.equals(t3));
		System.out.println(t1.equals(t4));
		System.out.println();
		System.out.println(t1.equals(t5));
		System.out.println();
		System.out.println(t3.equals(t4));
		System.out.println(t4.equals(t3));
		System.out.println();
		System.out.println(t4.equals(t5));
		System.out.println(t5.equals(t4));
		System.out.println();
		System.out.println(t1.equals(null));
		t1.setTag(null);
		System.out.println();
		System.out.println(t1 + "\t" + t1.getTag());
		System.out.println(t2 + "\t" + t2.getTag());
		System.out.println(t3 + "\t" + t3.getTag());// same as t1
		System.out.println(t4 + "\t");// same as t1
		System.out.println(t5 + "\t" + t5.getTag());
		System.out.println(ts);
		t3.setTag(null);
		System.out.println();
		System.out.println(t1 + "\t" + t1.getTag());
		System.out.println(t2 + "\t" + t2.getTag());
		System.out.println(t3 + "\t" + t3.getTag());// same as t1
		System.out.println(t4 + "\t");// same as t1
		System.out.println(t5 + "\t" + t5.getTag());
		System.out.println(ts);
		System.out.println();
		System.out.println(t1.equals(t3));

	}
}

class test {
	String tag;
	public test(String tag) {
		this.tag = tag;
	}
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}

package cn.edu.pku.sei.tsr.dragon.codeparser.adt;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;

public class LispList<T> {
	private ImmutableList<T> list;

	private LispList(ImmutableList<T> list) {
		this.list = list;
	}

	public static <T> LispList<T> copyOf(Iterator<? extends T> iterator) {
		return new LispList<>(ImmutableList.copyOf(iterator));
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public T car() {
		return list.get(0);
	}

	public LispList<T> cdr() {
		return new LispList<>(list.subList(1, list.size()));
	}
}

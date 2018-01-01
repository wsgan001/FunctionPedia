package cn.edu.pku.sei.tsr.dragon.codeparser.utils;

import java.util.function.Function;

public class Functions {
	private Functions() {
	}

	public static <T, R> Function<T, R> cast(Class<R> targetClass) {
		return x -> (R) targetClass.cast(x);
	}

}

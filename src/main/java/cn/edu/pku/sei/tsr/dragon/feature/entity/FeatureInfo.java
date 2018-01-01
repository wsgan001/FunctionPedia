package cn.edu.pku.sei.tsr.dragon.feature.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import cn.edu.pku.sei.tsr.dragon.code.entity.JavaClass;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaMethod;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class FeatureInfo {
	private List<Pair<String, Float>> uuids = new ArrayList<>();

	public void addUuid(String uuid, float score) {
		uuids.add(new ImmutablePair<>(uuid, score));
	}

	public List<String> getCodeInfo(String lib) {
		List<String> codeList = new ArrayList<>();
		uuids.stream()
			.sorted((x, y) -> y.getRight().compareTo(x.getRight()))
			.forEach(x -> {
				String codeDirPath = ObjectIO.CODE_DATA + File.separator + lib;
				Object obj = ObjectIO.readObject(ObjectIO.getDataCodeObjDirectory(codeDirPath + File.separator + x.getLeft() + ObjectIO.DAT_FILE_EXTENSION));
				if (obj instanceof JavaClass) {
					JavaClass javaClass = (JavaClass) obj;
					codeList.add(String.format("[Class] %s.%s", javaClass.getJavaPackage(), javaClass.getName()));
				} else if (obj instanceof JavaMethod) {
					JavaMethod javaMethod = (JavaMethod) obj;
					codeList.add(String.format("[Method] %s (in %s.%s)", javaMethod.getName(), javaMethod.getBelongClass().getJavaPackage(), javaMethod.getBelongClass().getName().toString()));
				}
			});
		return codeList;
	}
}

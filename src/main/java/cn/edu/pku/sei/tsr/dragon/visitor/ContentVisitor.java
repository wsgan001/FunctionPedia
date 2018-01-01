package cn.edu.pku.sei.tsr.dragon.visitor;

import java.io.File;

import cn.edu.pku.sei.tsr.dragon.code.entity.JavaBase;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaClass;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaFile;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaMethod;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaPackage;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaVariable;
import cn.edu.pku.sei.tsr.dragon.content.JavaDocParser;
import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class ContentVisitor extends JavaBaseVisitor<Object> {
	private int num = 0;
	private String libraryName;

	public void setLibrary(String libraryName) {
		this.libraryName = libraryName;
	}

	@Override
	public Object visit(JavaBase javaBase) {
		return null;
	}

	@Override
	public Object visit(JavaVariable javaVariable) {
		return null;
	}

	@Override
	public Object visit(JavaMethod javaMethod) {
		if (javaMethod.getComment() == null) return null;
		String javaDoc = JavaDocParser.parseJavaDoc(javaMethod.getComment().toString());
		ContentInfo info = new ContentInfo(javaDoc);
		info.setLibraryName(libraryName);
		info.setParentUuid(javaMethod.getUuid());
		String filePath = ObjectIO.CONTENTPOOL_FROMCOMMENT + File.separator + libraryName
			+ File.separator + info.getUuid() + ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(info, filePath);
		return null;
	}

	@Override
	public Object visit(JavaClass javaClass) {
		if (javaClass.getComment() == null) return null;
		String javaDoc = JavaDocParser.parseJavaDoc(javaClass.getComment().toString());
		ContentInfo info = new ContentInfo(javaDoc);
		info.setLibraryName(libraryName);
		info.setParentUuid(javaClass.getUuid());
		String filePath = ObjectIO.CONTENTPOOL_FROMCOMMENT + File.separator + libraryName
			+ File.separator + info.getUuid() + ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(info, filePath);
		return null;
	}

	@Override
	public Object visit(JavaFile javaFile) {
		return null;
	}

	@Override
	public Object visit(JavaPackage javaPackage) {
		return null;
	}

}

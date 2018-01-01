package cn.edu.pku.sei.tsr.dragon.code.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONStringer;

import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import cn.edu.pku.sei.tsr.dragon.visitor.JavaBaseVisitor;

public class JavaPackage extends JavaBase {
	private static final long	serialVersionUID	= -519959172330957372L;
	private List<JavaPackage>	packages;
	private List<JavaFile>		files;
	private String				path;

	public JavaPackage(String path) {
		super(null);
		this.path = path;
		packages = new ArrayList<>();
		files = new ArrayList<>();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void accept(JavaBaseVisitor visitor) {
		visitor.visit(this);
		packages.forEach(p -> p.accept(visitor));
		files.forEach(f -> f.accept(visitor));
	}

	public static JavaPackage createFromJson(JSONObject json) {
		JavaPackage result = new JavaPackage(json.getString("path"));
		json.getJSONArray("packages")
				.forEach(x -> result.addPackage(JavaPackage.createFromJson((JSONObject) x)));
		json.getJSONArray("files")
				.forEach(x -> result.addFile(JavaFile.createFromJson((JSONObject) x)));
		return result;
	}

	public String getPath() {
		return path;
	}

	public void addPackage(JavaPackage subPackage) {
		packages.add(subPackage);
	}

	public void addFile(JavaFile javaFile) {
		files.add(javaFile);
	}

	@Override
	public void writeToFile(String projectName) {
		String filePath = ObjectIO.CODE_DATA + File.separator + projectName + File.separator
				+ getUuid() + ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(this, filePath);
		packages.forEach(x -> x.writeToFile(projectName));
		files.forEach(x -> x.writeToFile(projectName));
	}

	@Override
	public String toJSONString() {
		return new JSONStringer().object().key("path").value(path).key("packages").value(packages)
				.key("files").value(files).endObject().toString();
	}
}

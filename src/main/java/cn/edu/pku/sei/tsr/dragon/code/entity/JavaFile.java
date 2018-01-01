package cn.edu.pku.sei.tsr.dragon.code.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONStringer;

import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import cn.edu.pku.sei.tsr.dragon.visitor.JavaBaseVisitor;

public class JavaFile extends JavaBase {
	private static final long	serialVersionUID	= 325413956630213923L;
	private List<JavaClass>		classes;
	private String path;

	public JavaFile(Name name, String path) {
		super(name);
		classes = new ArrayList<>();
		this.path = path;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void accept(JavaBaseVisitor visitor) {
		visitor.visit(this);
		classes.forEach(c -> c.accept(visitor));
	}

	public static JavaFile createFromJson(JSONObject json) {
		JavaFile result = new JavaFile(new Name(json.getString("name")), json.getString("path"));
		json.getJSONArray("classes")
				.forEach(x -> result.addClass(JavaClass.createFromJson((JSONObject) x)));
		return result;
	}

	public void addClass(JavaClass javaClass) {
		classes.add(javaClass);
	}

	public String getPath() {
		return path;
	}

	@Override
	public void writeToFile(String projectName) {
		String filePath = ObjectIO.CODE_DATA + File.separator + projectName + File.separator
				+ getUuid() + ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(this, filePath);
		classes.forEach(x -> x.writeToFile(projectName));
	}

	@Override
	public String toJSONString() {
		return new JSONStringer().object().key("name").value(getName()).key("classes").key("path").value(path)
				.value(classes).endObject().toString();
	}
}

package cn.edu.pku.sei.tsr.dragon.code.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONStringer;

import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import cn.edu.pku.sei.tsr.dragon.visitor.JavaBaseVisitor;

public class JavaClass extends JavaBase {
	private static final long serialVersionUID = -5885119954883197168L;
	private List<JavaMethod> methods;
	private List<JavaVariable> fields;
	private String javaPackage;

	public JavaClass(Name name, String javaPackage) {
		super(name);
		methods = new ArrayList<>();
		fields = new ArrayList<>();
		this.javaPackage = javaPackage;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void accept(JavaBaseVisitor visitor) {
		visitor.visit(this);
		methods.forEach(m -> m.accept(visitor));
		fields.forEach(f -> f.accept(visitor));
	}

	public static JavaClass createFromJson(JSONObject json) {
		JavaClass result = new JavaClass(new Name(json.getString("name")), json.getString("package"));
		result.setComment(new Comment(json.optString("comment")));
		json.getJSONArray("methods")
			.forEach(x -> result.addMethod(JavaMethod.createFromJson((JSONObject) x)));
		json.getJSONArray("fields")
			.forEach(x -> result.addField(JavaVariable.createFromJson((JSONObject) x)));
		return result;
	}

	@Override
	public String toString() {
		String result = "";
		result += String.format("Class: %s\n", getName().toString());
		result += String.format("SOComment:\n%s\n", getComment().toString());
		result += "Fields:\n";
		for (JavaVariable field : fields) {
			result += String.format("%s\n", field);
		}
		result += "Methods:\n";
		for (JavaMethod method : methods) {
			result += String.format("%s\n", method);
		}
		return result;
	}

	public void addMethod(JavaMethod method) {
		methods.add(method);
	}

	public void addField(JavaVariable field) {
		fields.add(field);
	}

	public String getJavaPackage() {
		return javaPackage;
	}

	@Override
	public void writeToFile(String projectName) {
		String filePath = ObjectIO.CODE_DATA + File.separator + projectName + File.separator
			+ getUuid() + ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(this, filePath);
		methods.forEach(x -> x.writeToFile(projectName));
		fields.forEach(x -> x.writeToFile(projectName));
	}

	@Override
	public String toJSONString() {
		return new JSONStringer().object().key("name").value(getName()).key("comment")
			.value(getComment()).key("methods").value(methods).key("fields").value(fields).key("package").value(javaPackage)
			.endObject().toString();
	}
}

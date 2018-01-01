package cn.edu.pku.sei.tsr.dragon.code.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONStringer;

import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import cn.edu.pku.sei.tsr.dragon.visitor.JavaBaseVisitor;

public class JavaMethod extends JavaBase {
	private static final long	serialVersionUID	= 3479517823577386378L;
	private List<JavaVariable>	args;
	private List<JavaVariable>	localVariables;
	private JavaClass belongClass;

	public JavaMethod(Name name, JavaClass belongClass) {
		super(name);
		args = new ArrayList<>();
		localVariables = new ArrayList<>();
		this.belongClass = belongClass;
	}

	@Override
	public void accept(@SuppressWarnings("rawtypes") JavaBaseVisitor visitor) {
		visitor.visit(this);
		args.forEach(a -> a.accept(visitor));
		localVariables.forEach(l -> l.accept(visitor));
	}

	public static JavaMethod createFromJson(JSONObject json) {
		JavaMethod result = new JavaMethod(new Name(json.getString("name")), null);
		result.setComment(new Comment(json.optString("comment")));
		json.getJSONArray("args")
				.forEach(x -> result.addArg(JavaVariable.createFromJson((JSONObject) x)));
		json.getJSONArray("local_vars")
				.forEach(x -> result.addLocalVariable(JavaVariable.createFromJson((JSONObject) x)));
		return result;
	}

	@Override
	public void writeToFile(String projectName) {
		String filePath = ObjectIO.CODE_DATA + File.separator + projectName + File.separator
				+ getUuid() + ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(this, filePath);
		args.forEach(x -> x.writeToFile(projectName));
		localVariables.forEach(x -> x.writeToFile(projectName));
	}

	@Override
	public String toString() {
		String result = "";
		result += String.format("Method: %s\n", getName());
		result += String.format("SOComment:\n%s\n", getComment());
		result += "Arguments:\n";
		for (JavaVariable arg : args) {
			result += String.format("%s\n", arg);
		}
		result += "Local Variables:\n";
		for (JavaVariable localVariable : localVariables) {
			result += String.format("%s\n", localVariable);
		}
		return result;
	}

	public void setCommentForArg(String argName, Comment comment) {
		args.stream().filter(arg -> arg.getName().toString().equals(argName))
				.forEach(arg -> arg.setComment(comment));
	}

	public void addLocalVariable(JavaVariable variable) {
		localVariables.add(variable);
	}

	public void addArg(JavaVariable arg) {
		args.add(arg);
	}

	public JavaClass getBelongClass() {
		return belongClass;
	}

	@Override
	public String toJSONString() {
		return new JSONStringer().object().key("name").value(getName()).key("comment")
				.value(getComment()).key("args").value(args).key("local_vars").value(localVariables)
				.endObject().toString();
	}
}

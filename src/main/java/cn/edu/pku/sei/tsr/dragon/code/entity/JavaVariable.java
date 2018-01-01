package cn.edu.pku.sei.tsr.dragon.code.entity;

import java.io.File;

import org.json.JSONObject;
import org.json.JSONStringer;

import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;
import cn.edu.pku.sei.tsr.dragon.visitor.JavaBaseVisitor;

public class JavaVariable extends JavaBase {

	private static final long serialVersionUID = -5130041874170191319L;

	public JavaVariable(Name name) {
		super(name);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void accept(JavaBaseVisitor visitor) {
		visitor.visit(this);
	}

	public static JavaVariable createFromJson(JSONObject json) {
		JavaVariable result = new JavaVariable(new Name(json.getString("name")));
		result.setComment(new Comment(json.optString("comment")));
		return result;
	}

	@Override
	public void writeToFile(String projectName) {
		String filePath = ObjectIO.CODE_DATA + File.separator + projectName + File.separator
				+ getUuid() + ObjectIO.DAT_FILE_EXTENSION;
		ObjectIO.writeObject(this, filePath);
	}

	@Override
	public String toString() {
		String result = "";
		result += String.format("Variable: %s\n", getName());
		result += String.format("SOComment:\n%s", getComment());
		return result;
	}

	@Override
	public String toJSONString() {
		return new JSONStringer().object().key("name").value(getName()).key("comment")
				.value(getComment()).endObject().toString();
	}
}

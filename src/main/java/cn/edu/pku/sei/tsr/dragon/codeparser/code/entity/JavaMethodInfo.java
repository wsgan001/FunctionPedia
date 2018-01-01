package cn.edu.pku.sei.tsr.dragon.codeparser.code.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.Block;

import cn.edu.pku.sei.tsr.dragon.codeparser.visitor.projectvisitor.JavaProjectVisitor;

public class JavaMethodInfo extends JavaBaseInfo {
	private static final long serialVersionUID = 1688789836109064606L;
	private JavaTypeInfo belongType;
	private JavaTypeInfo returnType;
	private List<JavaVariableInfo> args = new ArrayList<>();
	private List<JavaVariableInfo> localVariables = new ArrayList<>();
	private transient Block body;

	private Set<JavaVariableInfo> changedFields = new HashSet<>();
	private Set<JavaVariableInfo> changedArgs = new HashSet<>();

	public JavaMethodInfo(String name, JavaTypeInfo belongType, JavaTypeInfo returnType, Block body) {
		super(name);
		this.belongType = belongType;
		this.returnType = returnType;
		this.body = body;
	}

	@Override
	public String toString() {
		return String.format("[JavaMethod] %s", getFullyQualifiedName());
	}

	public String getFullyQualifiedName() {
		return String.format("%s.%s", belongType.getFullyQualifiedName(), getName());
	}

	public String getSignature() {
		String result = "";
		result += "(";
		List<String> typedArgs = args.stream()
			.map(arg -> String.format("%s %s", arg.getType().getFullyQualifiedName(), arg.getName()))
			.collect(Collectors.toList());

		result += String.join(", ", typedArgs);
		result += ") -> ";
		result += returnType == null ? "void" : returnType.getFullyQualifiedName();

		return result;
	}

	public void addLocalVariable(JavaVariableInfo variable) {
		localVariables.add(variable);
	}

	public void addArg(JavaVariableInfo arg) {
		args.add(arg);
	}

	public Block getBody() {
		return body;
	}

	public void changeVariable(String varName) {
		// find in local variable
		if (localVariables.stream().anyMatch(x -> x.getName().equals(varName))) return;

		// find in args
		Optional<JavaVariableInfo> matchedArg = args.stream()
			.filter(x -> x.getName().equals(varName))
			.findAny();
		if (matchedArg.isPresent()) {
			changedArgs.add(matchedArg.get());
			return;
		}

		// find in fields
		JavaVariableInfo field = belongType.getFieldWithOuterType(varName);
		if (field != null) {
			changedFields.add(field);
			return;
		}

		System.err.println(String.format("Unknown variable %s in %s", varName, this));
	}

	public <R, A> void accept(JavaProjectVisitor<R, A> visitor, A arg) {
		visitor.visit(this, arg);
	}

	public JavaTypeInfo getBelongType() {
		return belongType;
	}

	public JavaTypeInfo getReturnType() {
		return returnType;
	}

	public List<JavaVariableInfo> getArgs() {
		return args;
	}

	public List<JavaVariableInfo> getLocalVariables() {
		return localVariables;
	}

	public Set<JavaVariableInfo> getChangedFields() {
		return changedFields;
	}

	public Set<JavaVariableInfo> getChangedArgs() {
		return changedArgs;
	}

}

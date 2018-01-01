package cn.edu.pku.sei.tsr.dragon.codeparser.ir;

import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRAbstractStatement;

public interface IRScope {
	void addStatement(IRAbstractStatement statement);
}

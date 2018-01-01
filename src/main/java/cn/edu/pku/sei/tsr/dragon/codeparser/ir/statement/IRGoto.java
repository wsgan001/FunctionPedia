package cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;

public class IRGoto implements IRAbstractStatement {
	private IRLabel label;

	public IRGoto(IRLabel label) {
		this.label = label;
	}

	@Override
	public String toString() {
		return String.format("goto %s", label);
	}

	@Override
	public BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block) {
		block.getCFG().recordGotoInfo(block, label);
		return block.getCFG().createRegularBlock();
	}

}

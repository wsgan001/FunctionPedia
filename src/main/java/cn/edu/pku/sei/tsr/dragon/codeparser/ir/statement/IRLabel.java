package cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;

public class IRLabel implements IRAbstractStatement {
	private int index;

	public IRLabel(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		return String.format("Label %d", index);
	}

	@Override
	public BasicCFGRegularBlock buildCFG(BasicCFGRegularBlock block) {
		BasicCFGRegularBlock nextBlock = block.getCFG().createRegularBlock();
		block.setNext(nextBlock);
		block.getCFG().mapLabelBlock(this, nextBlock);
		return nextBlock;
	}

}

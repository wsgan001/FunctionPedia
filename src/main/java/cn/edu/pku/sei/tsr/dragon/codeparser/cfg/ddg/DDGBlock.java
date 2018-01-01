package cn.edu.pku.sei.tsr.dragon.codeparser.cfg.ddg;

import java.util.HashSet;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.CFGBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.plaincfg.AbstractPlainCFGBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.plaincfg.PlainCFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.plaincfg.PlainCFGBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.plaincfg.PlainCFGConditionBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRStatement;

public class DDGBlock implements CFGBlock {
	private int id;
	private AbstractPlainCFGBlock plainCFGBlock;
	protected HashSet<DDGBlock> prevs = new HashSet<>();
	private HashSet<DDGBlock> nexts = new HashSet<>();

	public DDGBlock(DDG ddg, AbstractPlainCFGBlock plainCFGBlock) {
		this.id = ddg.getNextID();
		this.plainCFGBlock = plainCFGBlock;
	}

	public PlainCFG getPlainCFG() {
		return plainCFGBlock.getCFG();
	}

	public AbstractPlainCFGBlock getPlainCFGBlock() {
		return plainCFGBlock;
	}

	public void addNext(DDGBlock next) {
		nexts.add(next);
		next.prevs.add(this);
	}

	public int getID() {
		return id;
	}

//	public IRStatement getStatement() {
//		assert plainCFGBlock instanceof PlainCFGBlock;
//		return ((PlainCFGBlock) plainCFGBlock).getStatement();
//	}

	public IRExpression.IRAbstractVariable getDef() {
		return plainCFGBlock.getDef();
	}

	public Stream<IRExpression.IRAbstractVariable> getUse() {
		return plainCFGBlock.getUse();
	}


	@Override
	public ImmutableSet<DDGBlock> getPrevs() {
		return ImmutableSet.copyOf(prevs);
	}

	@Override
	public ImmutableList<IRStatement> getStatements() {
		return plainCFGBlock.getStatements();
	}

	@Override
	public ImmutableSet<DDGBlock> getNexts() {
		return ImmutableSet.copyOf(nexts);
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		String s;
		if (plainCFGBlock instanceof PlainCFGBlock) {
			PlainCFGBlock b = (PlainCFGBlock) plainCFGBlock;
			if (b.getStatement() == null) s = "";
			else s = b.getStatement().toString();
		} else s = ((PlainCFGConditionBlock) plainCFGBlock).getExpression().toString();
		return String.format("[DDGBlock %d] %s", id, s);
	}
}

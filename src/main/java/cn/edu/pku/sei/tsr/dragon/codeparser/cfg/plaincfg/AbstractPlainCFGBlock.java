package cn.edu.pku.sei.tsr.dragon.codeparser.cfg.plaincfg;

import java.util.HashSet;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.CFGBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.MiningNode;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;

public abstract class AbstractPlainCFGBlock implements CFGBlock {
	private int id;
	private PlainCFG cfg;
	protected HashSet<AbstractPlainCFGBlock> prevs = new HashSet<>();

	public AbstractPlainCFGBlock(PlainCFG cfg) {
		this.id = cfg.getNextID();
		this.cfg = cfg;
	}

	public PlainCFG getCFG() {
		return cfg;
	}

	public int getId() {
		return id;
	}

	public abstract void removeNext(AbstractPlainCFGBlock next);

	public abstract IRExpression.IRAbstractVariable getDef();

	public abstract Stream<IRExpression.IRAbstractVariable> getUse();

	public abstract MiningNode toMiningNode();

	@Override
	public abstract ImmutableSet<? extends AbstractPlainCFGBlock> getNexts();

	@Override
	public ImmutableSet<AbstractPlainCFGBlock> getPrevs() {
		return ImmutableSet.copyOf(prevs);
	}

	@Override
	public int hashCode() {
		return id;
	}
}

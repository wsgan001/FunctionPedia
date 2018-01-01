package cn.edu.pku.sei.tsr.dragon.codeparser.cfg.plaincfg;

import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.MiningNode;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRStatement;

public class PlainCFGBlock extends AbstractPlainCFGBlock {
	private IRStatement statement;
	private AbstractPlainCFGBlock next;

	public PlainCFGBlock(PlainCFG cfg) {
		super(cfg);
	}

	public PlainCFGBlock(PlainCFG cfg, IRStatement statement) {
		super(cfg);
		this.statement = statement;
	}

	public void setNext(AbstractPlainCFGBlock next) {
		if (this.next != null) this.next.prevs.remove(this.next);
		this.next = next;
		next.prevs.add(this);
	}

	@Override
	public void removeNext(AbstractPlainCFGBlock next) {
		assert this.next == next;
		this.next = null;
		next.prevs.remove(this);
	}

	@Override
	public IRExpression.IRAbstractVariable getDef() {
		if (statement == null) return null;
		return statement.getDef();
	}

	@Override
	public Stream<IRExpression.IRAbstractVariable> getUse() {
		if (statement == null) return Stream.empty();
		return statement.getUseVariables();
	}

	@Override
	public MiningNode toMiningNode() {
		if (statement == null) return MiningNode.EMPTY;
		else return statement.toMiningNode();
	}

	public IRStatement getStatement() {
		return statement;
	}

	@Override
	public ImmutableList<IRStatement> getStatements() {
		if (statement == null) return ImmutableList.of();
		return ImmutableList.of(statement);
	}

	@Override
	public ImmutableSet<AbstractPlainCFGBlock> getNexts() {
		return next == null ? ImmutableSet.of() : ImmutableSet.of(next);
	}

	@Override
	public String toString() {
		return String.format("[PlainCFGBlock %d] %s", getId(), statement);
	}
}

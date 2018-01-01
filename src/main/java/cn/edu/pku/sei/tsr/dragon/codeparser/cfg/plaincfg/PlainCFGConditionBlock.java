package cn.edu.pku.sei.tsr.dragon.codeparser.cfg.plaincfg;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.MiningNode;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining.MiningNode.OPType;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRStatement;

public class PlainCFGConditionBlock extends AbstractPlainCFGBlock{
	private IRExpression expression;
	private HashSet<Pair<IRExpression, AbstractPlainCFGBlock>> nexts = new HashSet<>();

	public PlainCFGConditionBlock(PlainCFG cfg, IRExpression expression) {
		super(cfg);
		this.expression = expression;
	}

	public void addNext(IRExpression condition, AbstractPlainCFGBlock next) {
		nexts.add(Pair.of(condition, next));
		next.prevs.add(this);
	}

	public IRExpression getExpression() {
		return expression;
	}

	public IRExpression getCondition(AbstractPlainCFGBlock next) {
		return nexts.stream().filter(n -> n.getRight() == next).findFirst().get().getLeft();
	}

	@Override
	public void removeNext(AbstractPlainCFGBlock next) {
		nexts.removeIf(n -> n.getRight() == next);
		next.prevs.remove(this);
	}

	@Override
	public IRExpression.IRAbstractVariable getDef() {
		return null;
	}

	@Override
	public Stream<IRExpression.IRAbstractVariable> getUse() {
		return (expression instanceof IRExpression.IRAbstractVariable) ? Stream.of(((IRExpression.IRAbstractVariable) expression)) : Stream.empty();
	}

	@Override
	public MiningNode toMiningNode() {
		return new MiningNode(OPType.CASE, expression.toString());
	}

	@Override
	public ImmutableList<IRStatement> getStatements() {
		return ImmutableList.of();
	}

	@Override
	public ImmutableSet<AbstractPlainCFGBlock> getNexts() {
		return ImmutableSet.copyOf(nexts.stream().map(Pair::getRight).collect(Collectors.toList()));
	}

	@Override
	public String toString() {
		return String.format("[PlainCFGConditionBlock %d] %s", getId(), expression);
	}
}

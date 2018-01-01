package cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRPhi;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRStatement;

public class BasicCFGConditionBlock extends AbstractBasicCFGBlock {
	private Condition condition;
	private List<IRPhi> phis = new ArrayList<>();

	public BasicCFGConditionBlock(BasicCFG cfg, int id, Condition condition) {
		super(cfg, id);
		this.condition = condition;
		condition.getNexts().forEach(n -> n.addPrev(this));
	}

	public void replaceNext(AbstractBasicCFGBlock oldNext, AbstractBasicCFGBlock newNext) {
		condition.replaceNext(oldNext, newNext);
	}

	public IRExpression getConditionExpr() {
		return condition.getExpression();
	}

	public ImmutableSet<Pair<IRExpression, AbstractBasicCFGBlock>> getNextsWithCondition() {
		return condition.getNextsWithCondition();
	}

	@Override
	public String toString() {
		return String.format("[Condition Block %d]", getID());
	}

	@Override
	public void visit() {
		if (reachable) return;
		reachable = true;
		getNexts().forEach(AbstractBasicCFGBlock::visit);
	}

	@Override
	public void insertPhi(CFGVariableImpl exp) {
		phis.add(new IRPhi(prevs.size(), exp));
	}

	@Override
	public ImmutableSet<AbstractBasicCFGBlock> getNexts() {
		return condition.getNexts();
	}

	@Override
	public ImmutableList<IRStatement> getStatements() {
		return ImmutableList.copyOf(phis);
	}

	public abstract static class Condition {
		private IRExpression expression;

		public Condition(IRExpression expression) {
			this.expression = expression;
		}

		public abstract ImmutableSet<IRExpression> getUses();

		public abstract ImmutableSet<AbstractBasicCFGBlock> getNexts();

		public abstract ImmutableSet<Pair<IRExpression, AbstractBasicCFGBlock>> getNextsWithCondition();

		public abstract void replaceNext(AbstractBasicCFGBlock oldNext, AbstractBasicCFGBlock newNext);

		public IRExpression getExpression() {
			return expression;
		}

		public static class BooleanCondition extends Condition {
			private AbstractBasicCFGBlock thenBranch;
			private AbstractBasicCFGBlock elseBranch;

			public BooleanCondition(IRExpression expression, AbstractBasicCFGBlock thenBranch, AbstractBasicCFGBlock elseBranch) {
				super(expression);
				this.thenBranch = thenBranch;
				this.elseBranch = elseBranch;
			}

			@Override
			public ImmutableSet<IRExpression> getUses() {
				return ImmutableSet.of(getExpression());
			}

			@Override
			public ImmutableSet<Pair<IRExpression, AbstractBasicCFGBlock>> getNextsWithCondition() {
				return ImmutableSet.of(Pair.of(IRExpression.TRUE, thenBranch), Pair.of(IRExpression.FALSE, elseBranch));
			}

			@Override
			public ImmutableSet<AbstractBasicCFGBlock> getNexts() {
				return ImmutableSet.of(thenBranch, elseBranch);
			}

			@Override
			public void replaceNext(AbstractBasicCFGBlock oldNext, AbstractBasicCFGBlock newNext) {
				if (thenBranch == oldNext) thenBranch = newNext;
				if (elseBranch == oldNext) elseBranch = newNext;
			}
		}

		public static class CaseCondition extends Condition {
			private List<MutablePair<IRExpression, AbstractBasicCFGBlock>> branches = new ArrayList<>();
			private AbstractBasicCFGBlock defaultBranch;

			public CaseCondition(IRExpression expression) {
				super(expression);
			}

			public void addNext(IRExpression value, AbstractBasicCFGBlock next) {
				branches.add(MutablePair.of(value, next));
			}

			public void setDefault(AbstractBasicCFGBlock defaultBranch) {
				this.defaultBranch = defaultBranch;
			}

			@Override
			public ImmutableSet<IRExpression> getUses() {
				return new ImmutableSet.Builder<IRExpression>().add(getExpression()).addAll(branches.stream().map(Pair::getLeft).collect(Collectors.toList())).build();
			}

			@Override
			public ImmutableSet<Pair<IRExpression, AbstractBasicCFGBlock>> getNextsWithCondition() {
				return new ImmutableSet.Builder<Pair<IRExpression, AbstractBasicCFGBlock>>().addAll(branches).add(Pair.of(IRExpression.DEFAULT, defaultBranch)).build();
			}

			@Override
			public ImmutableSet<AbstractBasicCFGBlock> getNexts() {
				return new ImmutableSet.Builder<AbstractBasicCFGBlock>().addAll(branches.stream().map(Pair::getRight).collect(Collectors.toList())).add(defaultBranch).build();
			}

			@Override
			public void replaceNext(AbstractBasicCFGBlock oldNext, AbstractBasicCFGBlock newNext) {
				branches.stream().filter(branch -> branch.getRight() == oldNext).forEach(branch -> branch.setRight(newNext));
				if (defaultBranch == oldNext) defaultBranch = newNext;
			}
		}
	}

}

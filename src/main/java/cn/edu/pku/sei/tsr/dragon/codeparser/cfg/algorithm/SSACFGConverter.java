package cn.edu.pku.sei.tsr.dragon.codeparser.cfg.algorithm;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cn.edu.pku.sei.tsr.dragon.codeparser.adt.WorkingList;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.AbstractBasicCFGBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGConditionBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.CFGVariableImpl;
import cn.edu.pku.sei.tsr.dragon.codeparser.graph.Node;
import cn.edu.pku.sei.tsr.dragon.codeparser.graph.algorithm.DominanceFrontierResolver;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression.IRAbstractVariable;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRPhi;

/**
 * 将CFG转换为SSA形式的工具类
 * @author huacy
 */
public class SSACFGConverter {
	private static Map<CFGVariableImpl, WrappedVariable> varMap = new HashMap<>();
	private static Multimap<Node, Node> idomTree;

	/**
	 * 将给定的<code>BasicCFG</code>转换为SSA形式，该函数会改变该<code>BasicCFG</code>
	 * @param cfg 需要转换为SSA形式的<code>BasicCFG</code>
	 */
	public static void convertToSSAForm(BasicCFG cfg) {
		if (cfg.isSSAForm()) return;

		cfg.ssaSetup();

		Multimap<Node, Node> df = DominanceFrontierResolver.resolve(cfg, cfg.getEntry());
		idomTree = DominanceFrontierResolver.getIDomTree();

		placePhi(cfg, df);

		varMap.clear();
		cfg.getVariables().stream().forEach(x -> varMap.put(x, new WrappedVariable()));
		renaming(cfg.getEntry());

	}

	private static void placePhi(BasicCFG cfg, Multimap<Node, Node> df) {
		WorkingList<AbstractBasicCFGBlock> workingList = new WorkingList<>();
		Multimap<Node, CFGVariableImpl> phiMap = HashMultimap.create();

		cfg.getVariables().stream().filter(
			v -> v.getVariableUnit().getDefBoxes().count() > 1
		).forEach(a -> {
			workingList.clear();
			workingList.addAll(a.getDefBlocks());
			while (!workingList.isEmpty()) {
				AbstractBasicCFGBlock n = workingList.pop();
				df.get(n).forEach(y -> {
					if (!phiMap.get(y).contains(a)) {
						((AbstractBasicCFGBlock) y).insertPhi(a);
						phiMap.put(y, a);
						if (!a.getDefBlocks().contains(n)) workingList.push((AbstractBasicCFGBlock) y);
					}
				});
			}
		});
	}

	private static void renaming(AbstractBasicCFGBlock block) {
		block.getStatements().forEach(s -> {
			if (!(s instanceof IRPhi)) {
				s.getUseVariables().forEach(v -> {
					CFGVariableImpl var = block.getCFG().getVariable(v.getVariable());
					int i = varMap.get(var).stack.peek();
					v.setVersion(i);
				});
			}
			if (s.getDef() == null) return;
			CFGVariableImpl variable = block.getCFG().getVariable(s.getDef().getVariable());
			WrappedVariable wrappedVariable = varMap.get(variable);
			++wrappedVariable.count;
			wrappedVariable.stack.add(wrappedVariable.count);
			s.getDef().setVersion(wrappedVariable.count);
		});

		if (block instanceof BasicCFGConditionBlock) {
			BasicCFGConditionBlock b = (BasicCFGConditionBlock) block;
			IRExpression condition = b.getConditionExpr();
			if (condition instanceof IRAbstractVariable) {
				IRExpression.IRAbstractVariable v = (IRAbstractVariable) condition;
				CFGVariableImpl var = block.getCFG().getVariable(v.getVariable());
				int i = varMap.get(var).stack.peek();
				v.setVersion(i);
			}
		}

		block.getNexts().forEach(y -> {
			int j = y.getPrevIndex(block);
			y.getStatements().stream().filter(x -> x instanceof IRPhi).forEach(x -> {
				IRPhi phi = (IRPhi) x;
				int i = varMap.get(phi.getVar()).stack.peek();
				phi.replaceVar(j, i);
			});
		});

		idomTree.get(block).forEach(x -> renaming((AbstractBasicCFGBlock) x));

		block.getStatements().forEach(s -> {
			if (s.getDef() == null) return;
			CFGVariableImpl variable = block.getCFG().getVariable(s.getDef().getVariable());
			WrappedVariable wrappedVariable = varMap.get(variable);
			wrappedVariable.stack.pop();
		});
	}

	private static class WrappedVariable {
		private int count = 0;
		private Stack<Integer> stack = new Stack<>();

		private WrappedVariable() {
			stack.push(0);
		}
	}
}

package cn.edu.pku.sei.tsr.dragon.codeparser.cfg.ddg;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.CFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.plaincfg.PlainCFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.graph.Node;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression.IRAbstractVariable;
import cn.edu.pku.sei.tsr.dragon.codeparser.utils.Predicates;

/**
 * 数据流图的实现
 * @author huacy
 */
public class DDG implements CFG {
	private int blockNum = 0;
	private DDGBlock entry;
	private DDGBlock exit;
	private DDGBlock[] blocks;
	private Map<DDGVariable, DDGBlock> defMap = new HashMap<>();

	private DDG(PlainCFG plainCFG) {
		blocks = new DDGBlock[plainCFG.getBlocks().size()];

		plainCFG.getBlocks().forEach(block -> {
			DDGBlock newBlock = new DDGBlock(this, block);
			blocks[newBlock.getID()] = newBlock;
			if (block == plainCFG.getEntry()) entry = newBlock;
			if (block == plainCFG.getExit()) exit = newBlock;
		});

		for (DDGBlock block : blocks) {
			IRAbstractVariable defVar = block.getDef();
			if (defVar == null) continue;
			DDGVariable variable = new DDGVariable(defVar);
			defMap.put(variable, block);
		}

		for (DDGBlock block : blocks) {
			Stream<IRAbstractVariable> useVars = block.getUse();
			useVars.map(DDGVariable::new).map(defMap::get).filter(Predicates.notNull()).forEach(x -> x.addNext(block));
		}
	}

	public static CFG createCFG(PlainCFG cfg) {
		if (cfg == null) return null;
		return new DDG(cfg);
	}

	public static CFG createCFG(String methodBody) {
		CFG cfg = BasicCFG.createCFG(methodBody, true);
		CFG plainCFG = PlainCFG.createCFG((BasicCFG) cfg);
		return DDG.createCFG((PlainCFG) plainCFG);
	}

	public int getNextID() {
		return blockNum++;
	}

	@Override
	public ImmutableSet<DDGBlock> getBlocks() {
		return new ImmutableSet.Builder<DDGBlock>().add(blocks).build();
	}

	@Override
	public DDGBlock getExit() {
		return exit;
	}

	@Override
	public DDGBlock getEntry() {
		return entry;
	}

	@Override
	public ImmutableSet<DDGVariable> getVariables() {
		return ImmutableSet.copyOf(defMap.keySet());
	}

	@Override
	public ImmutableSet<? extends Node> getNodes() {
		return getBlocks();
	}
}

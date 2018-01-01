package cn.edu.pku.sei.tsr.dragon.codeparser.cfg.plaincfg;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.CFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.AbstractBasicCFGBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGConditionBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGRegularBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFGSpecialBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.CFGVariableImpl;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.ddg.DDGBlock;
import cn.edu.pku.sei.tsr.dragon.codeparser.graph.Node;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.IRExpression;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.VariableUnit;
import cn.edu.pku.sei.tsr.dragon.codeparser.ir.statement.IRStatement;
import cn.edu.pku.sei.tsr.dragon.utils.SetUtils;

/**
 * 扁平化控制流图的实现，每条语句作为一个基本块
 *
 * @author huacy
 */
public class PlainCFG implements CFG {
	private int blockNum = 0;
	private AbstractPlainCFGBlock entry;
	private AbstractPlainCFGBlock exit;
	private Set<AbstractPlainCFGBlock> blocks = new HashSet<>();
	private Map<VariableUnit, CFGVariableImpl> variableMap = new HashMap<>();

	private PlainCFG(BasicCFG basicCFG) {
		Map<AbstractBasicCFGBlock, AbstractPlainCFGBlock> blockStartMap = new HashMap<>();
		Map<AbstractBasicCFGBlock, AbstractPlainCFGBlock> blockEndMap = new HashMap<>();

		basicCFG.getBlocks().forEach(oldBlock -> {
			ImmutableList<IRStatement> statements = oldBlock.getStatements();
			int size = statements.size();

			if (oldBlock instanceof BasicCFGRegularBlock) {
				BasicCFGRegularBlock block = (BasicCFGRegularBlock) oldBlock;
				AbstractPlainCFGBlock[] newBlocks = new AbstractPlainCFGBlock[size == 0 ? 1 : size];

				for (int i = 0; i < statements.size(); ++i) {
					IRStatement s = statements.get(i);
					PlainCFGBlock newBlock = new PlainCFGBlock(this, s);
					blocks.add(newBlock);
					newBlocks[i] = newBlock;
				}

				for (int i = 0; i < statements.size() - 1; ++i)
					((PlainCFGBlock) newBlocks[i]).setNext(newBlocks[i + 1]);

				blockStartMap.put(block, newBlocks[0]);
				blockEndMap.put(block, newBlocks[size - 1]);
			} else if (oldBlock instanceof BasicCFGConditionBlock) {
				BasicCFGConditionBlock block = (BasicCFGConditionBlock) oldBlock;
				AbstractPlainCFGBlock[] newBlocks = new AbstractPlainCFGBlock[size + 1];

				for (int i = 0; i < statements.size(); ++i) {
					IRStatement s = statements.get(i);
					PlainCFGBlock newBlock = new PlainCFGBlock(this, s);
					blocks.add(newBlock);
					newBlocks[i] = newBlock;
				}

				newBlocks[size] = new PlainCFGConditionBlock(this, block.getConditionExpr());
				blocks.add(newBlocks[size]);

				for (int i = 0; i < size; ++i) ((PlainCFGBlock) newBlocks[i]).setNext(newBlocks[i + 1]);

				blockStartMap.put(block, newBlocks[0]);
				blockEndMap.put(block, newBlocks[size]);
			} else if (oldBlock instanceof BasicCFGSpecialBlock.Entry) {
				PlainCFGBlock newBlock = new PlainCFGBlock(this);
				entry = newBlock;
				blocks.add(newBlock);
				blockStartMap.put(oldBlock, newBlock);
				blockEndMap.put(oldBlock, newBlock);
			} else if (oldBlock instanceof BasicCFGSpecialBlock.Exit) {
				PlainCFGBlock newBlock = new PlainCFGBlock(this);
				exit = newBlock;
				blocks.add(newBlock);
				blockStartMap.put(oldBlock, newBlock);
				blockEndMap.put(oldBlock, newBlock);
			}
		});

		basicCFG.getBlocks().forEach(oldEndBlock -> {
			AbstractPlainCFGBlock newEndBlock = blockEndMap.get(oldEndBlock);
			if (oldEndBlock instanceof BasicCFGConditionBlock) {
				for (Pair<IRExpression, AbstractBasicCFGBlock> pair : ((BasicCFGConditionBlock) oldEndBlock).getNextsWithCondition()) {
					AbstractPlainCFGBlock newStartBlock = blockStartMap.get(pair.getRight());
					((PlainCFGConditionBlock) newEndBlock).addNext(pair.getLeft(), newStartBlock);
				}
			} else {
				oldEndBlock.getNexts().stream().map(blockStartMap::get).forEach(x -> ((PlainCFGBlock) newEndBlock).setNext(x));
			}
		});

		basicCFG.getVariables().forEach(v -> variableMap.put(v.getVariableUnit(), v));
	}

	private PlainCFG(Set<DDGBlock> ddgBlocks) {
		if (ddgBlocks.isEmpty()) throw new RuntimeException("DDG Block Set is empty!");
		PlainCFG oldCFG = ddgBlocks.iterator().next().getPlainCFG();
		Map<PlainCFGBlock, PlainCFGBlock> blockMap = new HashMap<>();

		//// TODO: 16-3-31
//		oldCFG.getBlocks().forEach(block -> blockMap.put(block, new PlainCFGBlock(this, block.getStatement())));
		blocks.addAll(blockMap.values());

		entry = blockMap.get(oldCFG.getEntry());
		exit = blockMap.get(oldCFG.getExit());

		oldCFG.getBlocks().forEach(oldBlock -> {
			PlainCFGBlock newBlock = blockMap.get(oldBlock);
			//// TODO: 16-3-31
//			oldBlock.getNexts().stream().map(blockMap::get).forEach(newBlock::addNext);
		});

		oldCFG.getVariables().forEach(v -> variableMap.put(v.getVariableUnit(), v));

		Set<PlainCFGBlock> preservedBlocks = ddgBlocks.stream().map(DDGBlock::getPlainCFGBlock).map(blockMap::get).collect(Collectors.toSet());

		Sets.difference(blocks, preservedBlocks).immutableCopy().forEach(this::removeBlock);
	}

	public static CFG createCFG(String methodBody) {
		BasicCFG cfg = (BasicCFG) BasicCFG.createCFG(methodBody, true);
		return new PlainCFG(cfg);
	}

	public static CFG createCFG(BasicCFG basicCFG) {
		if (basicCFG == null) return null;
		return new PlainCFG(basicCFG);
	}

	public static CFG createCFG(Set<DDGBlock> ddgBlocks) {
		return new PlainCFG(ddgBlocks);
	}

	public int getNextID() {
		return blockNum++;
	}

	/**
	 * 从控制流图中删除一个基本块，该基本块非入口或出口
	 *
	 * @param block 从控制流图中删除的基本块，如果block为entry或exit，则该函数不会做任何改变
	 */
	public void removeBlock(AbstractPlainCFGBlock block) {
		if (block == entry || block == exit) return;
		SetUtils.cartesianProduct(block.getPrevs(), block.getNexts()).forEach(p -> {
			if (p.getLeft() instanceof PlainCFGBlock) {
				PlainCFGBlock cfgBlock = (PlainCFGBlock) p.getLeft();
				cfgBlock.setNext(p.getRight());
			} else {
				PlainCFGConditionBlock cfgBlock = (PlainCFGConditionBlock) p.getLeft();
				cfgBlock.addNext(cfgBlock.getCondition(p.getRight()), p.getRight());
			}
		});
		block.getNexts().forEach(block::removeNext);
		block.getPrevs().forEach(p -> p.removeNext(block));
		blocks.remove(block);
	}

	@Override
	public ImmutableSet<AbstractPlainCFGBlock> getBlocks() {
		return new ImmutableSet.Builder<AbstractPlainCFGBlock>().addAll(blocks).add(entry).add(exit).build();
	}

	@Override
	public AbstractPlainCFGBlock getExit() {
		return exit;
	}

	@Override
	public AbstractPlainCFGBlock getEntry() {
		return entry;
	}

	@Override
	public ImmutableSet<CFGVariableImpl> getVariables() {
		return ImmutableSet.copyOf(variableMap.values());
	}

	@Override
	public ImmutableSet<Node> getNodes() {
		return new ImmutableSet.Builder<Node>().addAll(blocks).add(entry).add(exit).build();
	}
}

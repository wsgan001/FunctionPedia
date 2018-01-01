package cn.edu.pku.sei.tsr.dragon.codeparser.cfg;

import java.io.File;

import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.basiccfg.BasicCFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.ddg.DDG;
import cn.edu.pku.sei.tsr.dragon.codeparser.cfg.plaincfg.PlainCFG;
import cn.edu.pku.sei.tsr.dragon.codeparser.utils.CFGUtil;
import cn.edu.pku.sei.tsr.dragon.utils.FileUtils;

public class Test {
	public static void main(String[] args) {
//		String[] files = new String[]{
//				"switch1",
//				"switch2"
////				"jsonarray1",
////				"jsonarray2"
//		};
//		List<DDG> ddgs = new ArrayList<>();
//
//		for (String file : files) {
//			String s = FileUtils.getFileContent(new File("testdata/cfg/" + file));
//			DDG ddg = (DDG) DDG.createCFG(s);
//			ddgs.add(ddg);
//		}
//
//		List<Graph<MiningNode, Integer>> graphs = Miner.mineGraphFromDDG(ddgs, Miner.createSetting(2, 2));
//
//		for (Graph<MiningNode, Integer> graph : graphs) {
//			CFG cfg = MiningGraph.createCFGFromMiningGraph(ddgs, graph);
//			CFGUtil.saveCFG(cfg, "testdata/cfg/switch.png");
//			IRRepresentation ir = new IRRepresentation((PlainCFG) cfg);
//			ir.output();
//		}

		String s = FileUtils.getFileContent(new File("testdata/cfg/switch1"));
		BasicCFG basicCFG = (BasicCFG) BasicCFG.createCFG(s, true);
		CFGUtil.saveCFG(basicCFG, "testdata/cfg/switch-basic.png");
		PlainCFG plainCFG = (PlainCFG) PlainCFG.createCFG(basicCFG);
		CFGUtil.saveCFG(plainCFG, "testdata/cfg/switch-plain.png");
		DDG ddg = (DDG) DDG.createCFG(plainCFG);
		CFGUtil.saveCFG(ddg, "testdata/cfg/switch-ddg.png");
	}

}

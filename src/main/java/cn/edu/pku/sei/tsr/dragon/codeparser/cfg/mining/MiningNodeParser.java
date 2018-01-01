package cn.edu.pku.sei.tsr.dragon.codeparser.cfg.mining;

import java.text.ParseException;

import de.parsemis.parsers.LabelParser;

public class MiningNodeParser implements LabelParser<MiningNode> {

	private static final long serialVersionUID = -8921418174456111602L;

	@Override
	public MiningNode parse(String text) throws ParseException {
		return new MiningNode(text);
	}

	@Override
	public String serialize(MiningNode label) {
		return label.toString();
	}
}

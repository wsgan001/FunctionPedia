package cn.edu.pku.sei.tsr.dragon.visitor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.edu.pku.sei.tsr.dragon.code.entity.JavaBase;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaClass;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaFile;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaMethod;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaPackage;
import cn.edu.pku.sei.tsr.dragon.code.entity.JavaVariable;
import cn.edu.pku.sei.tsr.dragon.content.JavaDocParser;
import cn.edu.pku.sei.tsr.dragon.content.SentenceParser;
import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseExtractor;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseFilter;
import edu.stanford.nlp.trees.Tree;

public class ExtractVisitor extends JavaBaseVisitor<Object> {
	private int num = 0;

	private void extractJavaDoc(String javaDoc) {
		javaDoc = JavaDocParser.parseJavaDoc(javaDoc);
		ParagraphInfo paragraph = new ParagraphInfo(javaDoc);
		SentenceParser.separateParagraphToSentences(paragraph);
		paragraph.getSentences().forEach(sentence -> {
			SentenceParser.parseGrammaticalTree(sentence);
			Tree tree = sentence.getGrammaticalTree();
			if (tree != null) {
				tree.pennPrint();
				System.out.println(Arrays.asList(tree.children()));
				PhraseExtractor.extractVerbPhrases(sentence);
				sentence.getPhrases().forEach(phrase -> {
					PhraseFilter.filter(phrase, sentence);
					System.out.println("=============");
					phrase.getStemmedTree().pennPrint();
					System.out.println(phrase.getProofString());
				});
			}
		});
	}

	@Override
	public Object visit(JavaBase javaBase) {
		return null;
	}

	@Override
	public Object visit(JavaVariable javaVariable) {
		return null;
	}

	@Override
	public Object visit(JavaMethod javaMethod) {
		++num;
		System.out.println(num);
		extractJavaDoc(javaMethod.getComment().toString());
		return null;
	}

	@Override
	public Object visit(JavaClass javaClass) {
		extractJavaDoc(javaClass.getComment().toString());
		return null;
	}

	@Override
	public Object visit(JavaFile javaFile) {
		return null;
	}

	@Override
	public Object visit(JavaPackage javaPackage) {
		return null;
	}

	public void serialize(String path) {
		try {
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void restoreFromFile(String path) {
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<PhraseInfo> getPhraseList() {
		List<PhraseInfo> result = new ArrayList<>();
		return result;
	}
}

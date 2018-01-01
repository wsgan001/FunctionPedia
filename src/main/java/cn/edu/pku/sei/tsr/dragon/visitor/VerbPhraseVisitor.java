package cn.edu.pku.sei.tsr.dragon.visitor;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;

public class VerbPhraseVisitor extends JavaBaseVisitor<Object> {
	private List<PhraseInfo> phrasesInClass = new ArrayList<>();
	private List<PhraseInfo> phrasesInMethod = new ArrayList<>();
	private int num = 0;

	private void extractJavaDoc(String javaDoc, List<PhraseInfo> phrases) {
		javaDoc = JavaDocParser.parseJavaDoc(javaDoc);
		ParagraphInfo paragraph = new ParagraphInfo(javaDoc);
		SentenceParser.separateParagraphToSentences(paragraph);
		paragraph.getSentences().forEach(sentence -> {
			System.out.println(sentence);
			
			SentenceParser.parseGrammaticalTree(sentence);
			if (sentence.getGrammaticalTree() != null){
				sentence.getGrammaticalTree().pennPrint();

				System.out.println("-----");
				PhraseExtractor.extractVerbPhrases(sentence);
				sentence.getPhrases().forEach(System.out::println);

				sentence.getPhrases().forEach(phrase -> PhraseFilter.filter(phrase, sentence));
				phrases.addAll(
					sentence.getPhrases().stream()
						.filter(phrase -> phrase.getProofTotalScore() > Proof.MID)
						.collect(Collectors.toList())
				);
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
		extractJavaDoc(javaMethod.getComment().toString(), phrasesInMethod);
		return null;
	}

	@Override
	public Object visit(JavaClass javaClass) {
		extractJavaDoc(javaClass.getComment().toString(), phrasesInClass);
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

	public void print() {
		System.out.println("class:");
		phrasesInClass.forEach(System.out::println);
		System.out.println("methods:");
		phrasesInMethod.forEach(System.out::println);
	}

	public void serialize(String path) {
		try {
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(phrasesInClass.size());
			for (PhraseInfo phrase : phrasesInClass) {
				objectOut.writeObject(phrase);
			}
			objectOut.writeObject(phrasesInMethod.size());
			for (PhraseInfo phrase : phrasesInMethod) {
				objectOut.writeObject(phrase);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void restoreFromFile(String path) {
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			Integer classNum = (Integer) objectIn.readObject();
			for (int i = 0; i < classNum; ++i) {
				phrasesInClass.add((PhraseInfo) objectIn.readObject());
			}
			Integer methodNum = (Integer) objectIn.readObject();
			for (int i = 0; i < methodNum; ++i) {
				phrasesInMethod.add((PhraseInfo) objectIn.readObject());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public List<PhraseInfo> getPhraseList() {
		List<PhraseInfo> result = new ArrayList<>();
		result.addAll(phrasesInClass);
		result.addAll(phrasesInMethod);
		return result;
	}
}

package cn.edu.pku.sei.tsr.dragon.outdated;

import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.ContentParser;
import cn.edu.pku.sei.tsr.dragon.content.PreProcessor;
import cn.edu.pku.sei.tsr.dragon.content.SentenceParser;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.entity.sodata.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseFilter;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.PhraseExtractor;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Proof;

@Deprecated
public class Main {
	public static final Logger	logger	= Logger.getLogger(Main.class);

	public static void main(String[] args) {
		PreProcessor preProcessor = new PreProcessor();
		preProcessor.intepretRawDataToSOThreads();
		for (ThreadInfo thread : preProcessor.getSoThreads()) {

			// ThreadContentParser.parseHTMLContentInPostsOfThread(thread);
			System.out.println("=== [Thread] " + thread.getTitle() + " ===");
			long t1 = System.currentTimeMillis();
			// SentenceParser.separateParagraphToSentencesInThread(thread);
			long t2 = System.currentTimeMillis();
			// logger.info("[Main] separate sentence, time: " + (t2 - t1) +
			// "ms");
			List<SentenceInfo> sentences = thread.getSentences();
			for (SentenceInfo sentenceInfo : sentences) {
				System.out.println("=== [Sentence] ===");
				System.out.println(sentenceInfo.getContent());
				t1 = System.currentTimeMillis();

				PhraseExtractor.extractVerbPhrases(sentenceInfo);
				PhraseExtractor.extractNounPhrases(sentenceInfo);

				for (PhraseInfo phraseInfo : sentenceInfo.getPhrases()) {
					PhraseFilter.filter(phraseInfo, sentenceInfo);
					if (phraseInfo.getProofTotalScore() > Proof.MID) {
						System.out.println("[Phrase] " + phraseInfo.getContent());
						System.out.println(phraseInfo.getProofString());
						phraseInfo.getStemmedTree().pennPrint();
					}
				}
				t2 = System.currentTimeMillis();
				// logger.info("[Main] sentence finished, time: " + (t2 - t1) +
				// "ms");
			}

		}
	}

}

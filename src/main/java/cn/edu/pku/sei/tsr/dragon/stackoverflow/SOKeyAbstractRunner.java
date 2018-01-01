package cn.edu.pku.sei.tsr.dragon.stackoverflow;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.ContentParser;
import cn.edu.pku.sei.tsr.dragon.content.SyntaxParser;
import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.VerbalPhraseInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.VerbalPhraseStructureInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.keysentences.KeySentence;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class SOKeyAbstractRunner {
	public static final Logger logger = Logger.getLogger(SOKeyAbstractRunner.class);

	public static void main(String[] args) {

		File threadDir = ObjectIO.getDataObjDirectory(ObjectIO.SOTHREAD_LIBRARY);
		if (threadDir.exists() && threadDir.listFiles() != null) {
			File[] subdirs = threadDir.listFiles();
			for (File file : subdirs) {
				if (file.isDirectory()) {
					File[] libContents = file.listFiles();
					for (File threadFile : libContents) {
						OldThreadInfo thread = (OldThreadInfo) ObjectIO.readObject(threadFile);

						List<SentenceInfo> sentences = new ArrayList<>();

						SentenceInfo titleSentence = new SentenceInfo(
								thread.getTitle().getContent());
						sentences.add(titleSentence);

						ContentInfo questionContent = thread.getQuestion().getContent();
						ContentParser.parseContent(questionContent);

						boolean firstSent = true;

						List<ParagraphInfo> paraList = questionContent.getParagraphList();
						if (paraList != null) {
							for (ParagraphInfo paragraphInfo : paraList) {
								// Paragraphs that contain only code elements
								// will produce null sentenceList
								if (paragraphInfo.getSentences() != null)
									for (SentenceInfo sentenceInfo : paragraphInfo.getSentences()) {
										if (firstSent) {
											sentences.add(sentenceInfo);
											firstSent = false;
										}
										else if (KeySentence.judge(sentenceInfo.getContent()))
											sentences.add(sentenceInfo);
									}
							}
						}

						// write contentInfos to files
						sentences.forEach(sentence -> {
							SyntaxParser.extractPhrases(sentence);
							if (sentence.getPhrases() != null && sentence.getPhrases().size() > 0) {
								logger.info(sentence.getContent());
								logger.info("\t" + new VerbalPhraseStructureInfo(
										(VerbalPhraseInfo) sentence.getPhrases().get(0)));

								String sentenceLibFilePath = ObjectIO.STRUCTURED_SENTENCE_LIBRARY
										+ File.separator + thread.getLibraryName() + File.separator
										+ sentence.getUuid() + ObjectIO.DAT_FILE_EXTENSION;
								ObjectIO.writeObject(sentence, sentenceLibFilePath);
							}
						});

					}
				}
			}
		}

	}

}

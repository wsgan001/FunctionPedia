package cn.edu.pku.sei.tsr.dragon.stackoverflow;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.ContentParser;
import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.content.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.experiment.APILibrary;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.utils.Config;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

/**
 * Created by maxkibble on 2015/12/24.
 */
public class CorpusBuilder {
	public static final Logger logger = Logger.getLogger(CorpusBuilder.class);

	public static void main(String[] args) throws Exception {

		File threadDir = ObjectIO.getDataObjDirectory(ObjectIO.SOTHREAD_LIBRARY);
		if (threadDir.exists() && threadDir.listFiles() != null) {
			File[] subdirs = threadDir.listFiles();
			for (File file : subdirs) {
				if (file.isDirectory()) {
					if (!file.getName().equals(APILibrary.POI))
						continue;
					String sentenceLibFilePath = Config.getDataObjDir() + File.separator
							+ ObjectIO.SOCORPUS + File.separator + file.getName() + "_corpus.txt";
					PrintStream out = new PrintStream(sentenceLibFilePath);
					File[] libContents = file.listFiles();
					for (File threadFile : libContents) {
						OldThreadInfo thread = (OldThreadInfo) ObjectIO.readObject(threadFile);

						List<SentenceInfo> sentences = new ArrayList<>();

						SentenceInfo titleSentence = new SentenceInfo(
								thread.getTitle().getContent());
						sentences.add(titleSentence);

						ContentInfo questionContent = thread.getQuestion().getContent();
						ContentParser.parseContent(questionContent);

						List<ParagraphInfo> paraList = questionContent.getParagraphList();
						if (paraList != null) {
							for (ParagraphInfo paragraphInfo : paraList) {
								// Paragraphs that contain only code elements
								// will produce null sentenceList
								if (paragraphInfo.getSentences() != null)
									for (SentenceInfo sentenceInfo : paragraphInfo.getSentences()) {
										sentences.add(sentenceInfo);
									}
							}
						}

						for (PostInfo answer : thread.getAnswers()) {
							ContentInfo content = answer.getContent();

							paraList = content.getParagraphList();
							if (paraList != null) {
								for (ParagraphInfo paragraphInfo : paraList) {
									// Paragraphs that contain only code
									// elements
									// will produce null sentenceList
									if (paragraphInfo.getSentences() != null)
										for (SentenceInfo sentenceInfo : paragraphInfo
												.getSentences()) {
											sentences.add(sentenceInfo);
										}
								}
							}
						}

						// write contentInfos to files
						sentences.forEach(sentence -> {
							ContentParser.replaceCodeTags(sentence);
							out.println(sentence.getContent());
						});

					}
				}
			}
		}
	}
}

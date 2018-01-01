package cn.edu.pku.sei.tsr.dragon.stackoverflow;

import java.io.File;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import cn.edu.pku.sei.tsr.dragon.content.HtmlParser;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.utils.ObjectIO;

public class SOCodeSnippetCounter {

	public static final Logger logger = Logger.getLogger(SOCodeSnippetCounter.class);

	public static void main(String[] args) {

		File threadDir = ObjectIO.getDataObjDirectory(ObjectIO.SOTHREAD_LIBRARY);
		if (threadDir.exists() && threadDir.listFiles() != null) {
			File[] subdirs = threadDir.listFiles();
			for (File file : subdirs) {
				System.out.println(file.getName());
				if (file.isDirectory()) {
					File[] libContents = file.listFiles();

					int[] codeLengthDistribution = new int[301];
					int[] commentedSnippetCountByLength = new int[301];
					int[] commentCountBySnnipetLength = new int[301];

					for (int i : codeLengthDistribution) {
						codeLengthDistribution[i] = 0;
						commentedSnippetCountByLength[i] = 0;
						commentCountBySnnipetLength[i] = 0;
					}

					for (File threadFile : libContents) {
						int codeSnippetCount = 0;
						int codeSnippetLength = 0;
						int commentLineCount = 0;
						OldThreadInfo thread = (OldThreadInfo) ObjectIO.readObject(threadFile);

						String questionContent = thread.getQuestion().getContent().getContent();
						List<String> strs = HtmlParser.parseHTMLContent(questionContent);
						for (String string : strs) {
							if (string.startsWith("<code")) {
								codeSnippetCount++;
								int linewrapper = StringUtils.countMatches(string, "\n");
								int commentCount = StringUtils.countMatches(string, "//");

								codeSnippetLength += linewrapper;
								commentLineCount += commentCount;
								// System.out.println(linewrapper);
								if (linewrapper < 300) {
									codeLengthDistribution[linewrapper]++;
									if (commentCount > 0) {

										// count snippets with comments
										commentedSnippetCountByLength[linewrapper]++;
										// count comment line numbers
										commentCountBySnnipetLength[linewrapper] += commentCount;
									}
								}
								else {
									codeLengthDistribution[300]++;
									if (commentCount > 0) {

										// if (commentCount > 10)
										// System.err.println(commentCount +
										// "\t" + string);

										// count snippets with comments
										commentedSnippetCountByLength[linewrapper]++;
										// count comment line numbers
										commentCountBySnnipetLength[linewrapper] += commentCount;
									}
								}
							}
						}

						for (PostInfo ansPost : thread.getAnswers()) {
							int score = ansPost.getScore();
							String ansContent = ansPost.getContent().getContent();
							List<String> strs2 = HtmlParser.parseHTMLContent(ansContent);
							for (String string : strs2) {
								if (string.startsWith("<code")) {
									codeSnippetCount++;
									int linewrapper = StringUtils.countMatches(string, "\n");
									int commentCount = StringUtils.countMatches(string, "//");

									codeSnippetLength += linewrapper;
									commentLineCount += commentCount;
									// System.out.println(linewrapper);
									if (linewrapper < 300) {
										codeLengthDistribution[linewrapper]++;
										if (commentCount > 0) {

											// count snippets with comments
											commentedSnippetCountByLength[linewrapper]++;
											// count comment line numbers
											commentCountBySnnipetLength[linewrapper] += commentCount;
										}
									}
									else {
										codeLengthDistribution[300]++;
										if (commentCount > 0) {

											// if (commentCount > 10)
											// System.err.println(commentCount +
											// "\t" + string);

											// count snippets with comments
											commentedSnippetCountByLength[linewrapper]++;
											// count comment line numbers
											commentCountBySnnipetLength[linewrapper] += commentCount;
										}
									}
								}
							}
						}

						// System.out.println(codeSnippetCount + "\t" +
						// codeSnippetLength);
						if (codeSnippetCount > 0 && codeSnippetLength / codeSnippetCount >= 30
								&& commentLineCount <= 1)
						// && questionContent.contains("convert")
						// && questionContent.contains("csv"))//&& new
						// Random().nextInt(10) <= 1) {
						{
							System.err.println(codeSnippetCount + "\t" + commentLineCount);
							System.err.println(thread.getTitle());
							System.err.println(HtmlParser.parseHTMLContent(
									thread.getQuestion().getContent().getContent()));
						}

					}
					System.out.println("========================================");
					for (int i = 1; i < codeLengthDistribution.length; i++) {
						System.out.println(i + "\t" + codeLengthDistribution[i]);
					}
					System.out.println("========================================");

					System.out.println("========================================");
					for (int i = 1; i < commentedSnippetCountByLength.length; i++) {
						System.out.println(i + "\t" + commentedSnippetCountByLength[i] + "\t"
								+ commentCountBySnnipetLength[i]);
					}
					System.out.println("========================================");

				}
			}
		}

	}

}

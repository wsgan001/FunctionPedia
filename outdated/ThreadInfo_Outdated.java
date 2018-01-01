package cn.edu.pku.sei.tsr.dragon.outdated;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import cn.edu.pku.sei.tsr.dragon.entity.contentdata.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.entity.sodata.CommentInfo;
import cn.edu.pku.sei.tsr.dragon.entity.sodata.PostInfo;
import cn.edu.pku.sei.tsr.dragon.entity.sodata.TagInfo;
import cn.edu.pku.sei.tsr.dragon.entity.contentdata.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.parser.Rules;

public class ThreadInfo_Outdated implements Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8646684690231890312L;
	private long				id;
	private String				uuid;
	private String				title;
	private ParagraphInfo	titleAsParagraph;
	private PostInfo			question;
	private PostInfo			acceptedAnswer		= null;
	private List<PostInfo>		answerList			= null;
	private int					viewCount			= 0;
	private int					favoriteCount		= 0;
	private List<TagInfo>		tags				= null;

	public static final String	THREAD_KEY			= "Th";
	public static final String	TITLE_KEY			= "Tl";
	public static final String	QUESTION_KEY		= "Q";
	public static final String	ACCEPTED_ANSWER_KEY	= "Ac";
	public static final String	ANSWER_KEY			= "As";
	public static final String	PARAGRAPH_KEY		= "P";
	public static final String	COMMENT_KEY			= "C";
	public static final String	SENTENCE_KEY		= "S";
	public static final String	PHRASE_KEY			= "F";

	public ThreadInfo_Outdated() {
		uuid = UUID.randomUUID().toString();
	}

	public Map<String, PhraseInfo> getMarkedPhrasesMap() {
		HashMap<String, PhraseInfo> phraseMap = new HashMap<String, PhraseInfo>();

		String threadKey = THREAD_KEY + id;

		// title
		Map<String, PhraseInfo> titlePhraseMap = getMarkedPhrasesMapFromParagraph(titleAsParagraph);
		for (Entry<String, PhraseInfo> entry : titlePhraseMap.entrySet()) {
			String key = threadKey + TITLE_KEY + entry.getKey();
			phraseMap.put(key, entry.getValue());
		}

		// question
		Map<String, PhraseInfo> questionPhraseMap = getMarkedPhrasesMapFromPost(question);
		for (Entry<String, PhraseInfo> entry : questionPhraseMap.entrySet()) {
			String key = threadKey + QUESTION_KEY + entry.getKey();
			phraseMap.put(key, entry.getValue());
		}

		// accpeted answer
		Map<String, PhraseInfo> acptAnswerPhraseMap = getMarkedPhrasesMapFromPost(acceptedAnswer);
		for (Entry<String, PhraseInfo> entry : acptAnswerPhraseMap.entrySet()) {
			String key = threadKey + ACCEPTED_ANSWER_KEY + entry.getKey();
			phraseMap.put(key, entry.getValue());
		}

		// answers
		for (int i = 0; i < answerList.size(); i++) {
			PostInfo answer = answerList.get(i);
			Map<String, PhraseInfo> answerPhraseMap = getMarkedPhrasesMapFromPost(answer);
			for (Entry<String, PhraseInfo> entry : acptAnswerPhraseMap.entrySet()) {
				String key = threadKey + ANSWER_KEY + (i + 1) + entry.getKey();
				phraseMap.put(key, entry.getValue());
			}
		}

		return phraseMap;
	}

	private Map<String, PhraseInfo> getMarkedPhrasesMapFromPost(PostInfo post) {
		HashMap<String, PhraseInfo> phraseMap = new HashMap<String, PhraseInfo>();
		if (post == null)
			return phraseMap;
		for (int i = 0; i < post.getContent().getParagraphs().size(); i++) {
			ParagraphInfo paragraph = post.getContent().getParagraphs().get(i);
			if (paragraph instanceof ParagraphInfo) {
				ParagraphInfo textPara = (ParagraphInfo) paragraph;
				Map<String, PhraseInfo> paraPhraseMap = getMarkedPhrasesMapFromParagraph(textPara);
				for (Entry<String, PhraseInfo> entry : paraPhraseMap.entrySet()) {
					String key = PARAGRAPH_KEY + (i + 1) + entry.getKey();
					phraseMap.put(key, entry.getValue());
				}
			}
		}
		for (int i = 0; i < post.getComments().size(); i++) {
			ParagraphInfo textPara = post.getComments().get(i).getContent();
			Map<String, PhraseInfo> paraPhraseMap = getMarkedPhrasesMapFromParagraph(textPara);
			for (Entry<String, PhraseInfo> entry : paraPhraseMap.entrySet()) {
				String key = COMMENT_KEY + (i + 1) + entry.getKey();
				phraseMap.put(key, entry.getValue());
			}
		}
		return phraseMap;
	}

	private Map<String, PhraseInfo> getMarkedPhrasesMapFromParagraph(ParagraphInfo para) {
		HashMap<String, PhraseInfo> phraseMap = new HashMap<String, PhraseInfo>();
		for (int i = 0; i < para.getSentences().size(); i++) {
			SentenceInfo sentence = para.getSentences().get(i);
			for (int j = 0; j < sentence.getPhrases().size(); j++) {
				PhraseInfo phrase = sentence.getPhrases().get(j);
				if (phrase.getProofTotalScore() > Rules.THRESHOLD) {
					String key = SENTENCE_KEY + (i + 1) + PHRASE_KEY + (j + 1);
					phraseMap.put(key, phrase);
				}
			}
		}

		return phraseMap;
	}

	public List<PhraseInfo> getPhrases() {
		List<PhraseInfo> phrases = new ArrayList<PhraseInfo>();
		for (SentenceInfo sentence : getSentences()) {
			try {
				phrases.addAll(sentence.getPhrases());
			}
			catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		return phrases;
	}

	public List<PostInfo> getPosts() {
		List<PostInfo> posts = new ArrayList<PostInfo>();
		posts.add(question);
		posts.addAll(answerList);
		return posts;
	}

	public List<ParagraphInfo> getParagraphs() {
		List<ParagraphInfo> paras = new ArrayList<ParagraphInfo>();
		paras.add(titleAsParagraph);
		for (PostInfo post : getPosts()) {
			paras.addAll(post.getContent().getParagraphs());
			for (CommentInfo comment : post.getComments()) {
				paras.add(comment.getContent());
			}
		}
		return paras;
	}

	public List<ParagraphInfo> getTextParagraphs() {
		List<ParagraphInfo> paras = new ArrayList<ParagraphInfo>();
		paras.add(titleAsParagraph);
		for (PostInfo post : getPosts()) {
			List<ParagraphInfo> postContentParagraphs = post.getContent().getParagraphs();
			for (ParagraphInfo para : postContentParagraphs) {
				if (para instanceof ParagraphInfo)
					paras.add((ParagraphInfo) para);
			}
			for (CommentInfo comment : post.getComments()) {
				paras.add(comment.getContent());
			}
		}
		return paras;
	}

	public List<SentenceInfo> getSentences() {
		List<SentenceInfo> sentences = new ArrayList<SentenceInfo>();
		for (ParagraphInfo textPara : getTextParagraphs()) {
			sentences.addAll(textPara.getSentences());
		}
		return sentences;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public int getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public List<TagInfo> getTags() {
		return tags;
	}

	public void setTags(List<TagInfo> tags) {
		this.tags = tags;
	}

	public PostInfo getQuestion() {
		return question;
	}

	public void setQuestion(PostInfo question) {
		this.question = question;
	}

	public PostInfo getAcceptedAnswer() {
		return acceptedAnswer;
	}

	public void setAcceptedAnswer(PostInfo acceptedAnswer) {
		this.acceptedAnswer = acceptedAnswer;
	}

	public List<PostInfo> getAnswerList() {
		return answerList;
	}

	public void setAnswerList(List<PostInfo> answerList) {
		this.answerList = answerList;
	}

	public ParagraphInfo getTitleAsParagraph() {
		return titleAsParagraph;
	}

	public void setTitleAsParagraph(ParagraphInfo titleAsParagraph) {
		this.titleAsParagraph = titleAsParagraph;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}

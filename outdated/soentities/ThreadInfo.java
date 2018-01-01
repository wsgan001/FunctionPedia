package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.edu.pku.sei.tsr.dragon.content.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.feature.entity.LibraryInfo;
import cn.edu.pku.sei.tsr.dragon.experiment.APILibrary;
import cn.edu.pku.sei.tsr.dragon.utils.UUIDInterface;

public class ThreadInfo implements UUIDInterface, Serializable {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5435484271326966093L;
	private int				id;
	private String				uuid;
	private ContentInfo			title;
	private PostInfo			question;
	private List<PostInfo>		answers;
	private int					viewCount			= 0;
	private int					favoriteCount		= 0;
	private List<TagInfo>		tags;
	private String				libraryName;

	public static final String	THREAD_KEY			= "Th";
	public static final String	TITLE_KEY			= "Ttl";
	public static final String	QUESTION_KEY		= "Q";
	public static final String	ACCEPTED_ANSWER_KEY	= "Ac";
	public static final String	ANSWER_KEY			= "As";
	public static final String	PARAGRAPH_KEY		= "P";
	public static final String	COMMENT_KEY			= "C";
	public static final String	SENTENCE_KEY		= "S";
	public static final String	PHRASE_KEY			= "F";

	public ThreadInfo() {
		uuid = UUID.randomUUID().toString();
		answers = new ArrayList<PostInfo>();
		tags = new ArrayList<TagInfo>();
	}

	public LibraryInfo getLibrary() {
		if (libraryName != null)
			return APILibrary.getLibrary(APILibrary.judgeProjectByTags(libraryName));
		return null;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
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

	public List<PostInfo> getAnswers() {
		return answers;
	}

	public void setAnswers(List<PostInfo> answerList) {
		this.answers = answerList;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ContentInfo getTitle() {
		return title;
	}

	public void setTitle(ContentInfo title) {
		this.title = title;
	}

	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}
}

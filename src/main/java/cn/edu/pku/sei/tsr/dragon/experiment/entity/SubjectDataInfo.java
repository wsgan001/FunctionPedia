package cn.edu.pku.sei.tsr.dragon.experiment.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cn.edu.pku.sei.tsr.dragon.document.entity.ContentInfo;
import cn.edu.pku.sei.tsr.dragon.document.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.document.entity.SentenceInfo;
import cn.edu.pku.sei.tsr.dragon.nlp.entity.PhraseInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.CommentInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.ThreadInfo;
import cn.edu.pku.sei.tsr.dragon.task.entity.TaskInfo;

public class SubjectDataInfo implements Serializable {
	private static final long					serialVersionUID	= -5145246421192551214L;

	private String								name;

	public HashMap<Integer, ThreadInfo>			threadMap			= new HashMap<>();
	public HashMap<Integer, PostInfo>			postMap				= new HashMap<>();
	public HashMap<Integer, CommentInfo>		commentMap			= new HashMap<>();
	public HashMap<Integer, ContentInfo>		contentMap			= new HashMap<>();
	public HashMap<Integer, ParagraphInfo>		paragraphMap		= new HashMap<>();
	public HashMap<Integer, SentenceInfo>		sentenceMap			= new HashMap<>();
	public HashMap<Integer, PhraseInfo>			phraseMap			= new HashMap<>();

	public HashMap<Integer, TaskInfo>			taskMap				= new HashMap<>();

	public HashMap<Integer, List<Integer>>		threadToContentMap	= new HashMap<>();
	public HashMap<Integer, List<Integer>>		postToContentMap	= new HashMap<>();
	public HashMap<Integer, List<Integer>>		commentToContentMap	= new HashMap<>();
	
	//only valid phrases. refer to subjectdatahandler
	public HashMap<Integer, List<Integer>>		threadToPhrasesMap	= new HashMap<>(); 
	public HashMap<Integer, Set<Integer>>		threadToTasksMap	= new HashMap<>();
	
	public HashMap<Integer, List<Integer>>		taskToPhrasesMap	= new HashMap<>();
	public HashMap<Integer, Set<Integer>>		taskToThreadsMap	= new HashMap<>();

	public SubjectDataInfo(String name) {
		super();
		this.name = name;
	}
	public String printCount() {
		StringBuilder sb = new StringBuilder();

		sb.append("============Subject:" + name + "===========\n");
		sb.append("threadMap:" + threadMap.size() + "\n");
		sb.append("postMap:" + postMap.size() + "\n");
		sb.append("commentMap:" + commentMap.size() + "\n");
		sb.append("contentMap:" + contentMap.size() + "\n");
		sb.append("paragraphMap:" + paragraphMap.size() + "\n");
		sb.append("sentenceMap:" + sentenceMap.size() + "\n");
		sb.append("phraseMap:" + phraseMap.size() + "\n");
		sb.append("taskMap:" + taskMap.size() + "\n\n");
		sb.append("thread->content:" + threadToContentMap.size() + "\n");
		sb.append("post->content:" + postToContentMap.size() + "\n");
		sb.append("comment->content:" + commentToContentMap.size() + "\n");
		sb.append("thread->phrase:" + threadToPhrasesMap.size() + "\n");
		sb.append("thread->task:" + threadToTasksMap.size() + "\n");
		sb.append("task->phrase:" + taskToPhrasesMap.size() + "\n");
		sb.append("task->thread:" + taskToThreadsMap.size() + "\n");

		return sb.toString();
	}

	public String print() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n=============thread map=============\n");
		sb.append(threadMap.toString());
		sb.append("\n=============post map=============\n");
		sb.append(postMap.toString());
		sb.append("\n=============comment map=============\n");
		sb.append(commentMap.toString());
		sb.append("\n=============content map=============\n");
		sb.append(contentMap.toString());
		sb.append("\n=============paragraph map=============\n");
		sb.append(paragraphMap.toString());
		sb.append("\n=============sentence map=============\n");
		sb.append(sentenceMap.toString());
		sb.append("\n=============phrase map=============\n");
		sb.append(phraseMap.toString());
		sb.append("\n=============task map=============\n");
		sb.append(taskMap.toString());
		sb.append("\n=============thread-content id map=============\n");
		sb.append(threadToContentMap.toString());
		sb.append("\n=============post-content id map=============\n");
		sb.append(postToContentMap.toString());
		sb.append("\n=============comment-content id map=============\n");
		sb.append(commentToContentMap.toString());
		sb.append("\n");
		return sb.toString();
	}

	public HashMap<Integer, ThreadInfo> getThreadMap() {
		return threadMap;
	}

	public void setThreadMap(HashMap<Integer, ThreadInfo> threadMap) {
		this.threadMap = threadMap;
	}

	public HashMap<Integer, PostInfo> getPostMap() {
		return postMap;
	}

	public void setPostMap(HashMap<Integer, PostInfo> postMap) {
		this.postMap = postMap;
	}

	public HashMap<Integer, CommentInfo> getCommentMap() {
		return commentMap;
	}

	public void setCommentMap(HashMap<Integer, CommentInfo> commentMap) {
		this.commentMap = commentMap;
	}

	public HashMap<Integer, ContentInfo> getContentMap() {
		return contentMap;
	}

	public void setContentMap(HashMap<Integer, ContentInfo> contentMap) {
		this.contentMap = contentMap;
	}

	public HashMap<Integer, ParagraphInfo> getParagraphMap() {
		return paragraphMap;
	}

	public void setParagraphMap(HashMap<Integer, ParagraphInfo> paragraphMap) {
		this.paragraphMap = paragraphMap;
	}

	public HashMap<Integer, SentenceInfo> getSentenceMap() {
		return sentenceMap;
	}

	public void setSentenceMap(HashMap<Integer, SentenceInfo> sentenceMap) {
		this.sentenceMap = sentenceMap;
	}

	public HashMap<Integer, PhraseInfo> getPhraseMap() {
		return phraseMap;
	}

	public void setPhraseMap(HashMap<Integer, PhraseInfo> phraseMap) {
		this.phraseMap = phraseMap;
	}

	public HashMap<Integer, TaskInfo> getTaskMap() {
		return taskMap;
	}

	public void setTaskMap(HashMap<Integer, TaskInfo> taskMap) {
		this.taskMap = taskMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

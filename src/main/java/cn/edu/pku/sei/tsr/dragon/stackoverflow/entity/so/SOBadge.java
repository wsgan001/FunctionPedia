package cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.so;

import java.io.Serializable;
import java.util.Date;

public class SOBadge implements Serializable {
	private static final long	serialVersionUID	= 8206155471620461241L;
	public static final String	TABLE_NAME			= "sobadges";

	private int					id;
	private int					userId;
	private String				name;
	private Date				date;
	private int					clazz;
	private int					tagBased;

	public SOBadge() {
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date datetime) {
		this.date = datetime;
	}
	public int getClazz() {
		return clazz;
	}
	public void setClazz(int clazz) {
		this.clazz = clazz;
	}
	public int getTagBased() {
		return tagBased;
	}
	public void setTagBased(int tagBased) {
		this.tagBased = tagBased;
	}

}

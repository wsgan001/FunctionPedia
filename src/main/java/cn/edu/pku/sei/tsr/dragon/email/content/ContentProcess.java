package cn.edu.pku.sei.tsr.dragon.email.content;

import cn.edu.pku.sei.tsr.dragon.email.entity.Email;

/**
 * @ClassName: ContentProcess
 * @Description: process of the content
 * @author: left
 * @date: 2013.12.26 9:01:47
 */

public interface ContentProcess {

	public void process(Email e);
}

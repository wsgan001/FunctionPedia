package cn.edu.pku.sei.tsr.dragon.test;

import cn.edu.pku.sei.tsr.dragon.content.entity.ParagraphInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.CommentInfo;
import cn.edu.pku.sei.tsr.dragon.stackoverflow.entity.PostInfo;

public class ClassReflectionTest {

	public static void main(String[] args) {
		Object[] o = new Object[10];
		OldThreadInfo t = new OldThreadInfo();
		PostInfo p = new PostInfo();
		CommentInfo cm = new CommentInfo();
		o[0]=t;
		o[1]=p;
		o[2]=cm;
		Object x= new ParagraphInfo("hahah");
		
		System.out.println(t.getClass().getName());
		System.out.println(x.getClass().toString());

		System.out.println(o[0].getClass().getName());
		System.out.println(o[2].getClass().getSimpleName());
		
		Class<? extends Object> c=o[1].getClass();
		
	}
}

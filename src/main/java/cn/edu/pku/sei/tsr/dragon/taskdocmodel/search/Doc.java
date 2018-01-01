package cn.edu.pku.sei.tsr.dragon.taskdocmodel.search;
import java.util.ArrayList;
import java.util.List;

public class Doc{
	String name = "";
	int id;
	double factor = 0;
	List<Task> tasks = new ArrayList<Task>();
	public void addtask(Task task){
		tasks.add(task);
	}
}
package cn.edu.pku.sei.tsr.dragon.task.dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import cn.edu.pku.sei.tsr.dragon.task.entity.TaskInfo;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;

public class TaskDAO extends CommonDAO<TaskInfo, Integer> {
	public static final String	FIELD_ID			= "Id";
	public static final String	FIELD_TEXT			= "Text";
	public static final String	FIELD_KERNEL_VERB	= "KernelVerb";
	public static final String	FIELD_KERNEL_NOUN	= "KernelNoun";

	public TaskDAO(Connection conn) {
		super(TaskInfo.class, TaskInfo.TABLE_NAME, conn);
	}

	public TaskInfo addTask(TaskInfo task) {
		if (task == null || task.getText() == null)
			return null;
		try {
			List<String> keys = new ArrayList<>();
			List<Object> values = new ArrayList<>();
			
			if (task.getId() > 0) {
				keys.add(FIELD_ID);
				values.add(task.getId());
			}

			keys.add(FIELD_TEXT);
			values.add(task.getText());

			if (!StringUtils.isBlank(task.getKernelVerb())) {
				keys.add(FIELD_KERNEL_VERB);
				values.add(task.getKernelVerb());
			}

			if (!StringUtils.isBlank(task.getKernelNoun())) {
				keys.add(FIELD_KERNEL_NOUN);
				values.add(task.getKernelNoun());
			}

			int resultCode = insert(keys.toArray(), values.toArray());
			if (resultCode <= 0)
				return null;

			/** auto-increment id is blank until insertion **/
			int id = getLastInsertedId();
			task.setId(id);

			return task;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}

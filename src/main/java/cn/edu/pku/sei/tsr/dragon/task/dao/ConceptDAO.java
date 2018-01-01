package cn.edu.pku.sei.tsr.dragon.task.dao;

import java.sql.Connection;

import cn.edu.pku.sei.tsr.dragon.task.entity.ConceptInfo;
import cn.edu.pku.sei.tsr.dragon.utils.database.CommonDAO;

public class ConceptDAO extends CommonDAO<ConceptInfo, Integer> {
	public ConceptDAO(Connection conn) {
		super(ConceptInfo.class, ConceptInfo.TABLE_NAME, conn);
	}
}

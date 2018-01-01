package cn.edu.pku.sei.tsr.dragon.outdated;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.ResultSetHandler;

import cn.edu.pku.sei.tsr.dragon.utils.Utils;

public class IntListResultSetHandler implements ResultSetHandler<List<Integer>> {
	private int[] resultArray;

	@Override
	public List<Integer> handle(ResultSet rs) throws SQLException {
		List<Integer> idList = new ArrayList<>();
		while (rs.next()) {
			idList.add(rs.getInt(1));
		}
		resultArray = Utils.convertIntegerListToIntArray(idList);
		return idList;
	}

	public int[] getResultArray() {
		return resultArray;
	}
}

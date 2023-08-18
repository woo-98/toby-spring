package com.springbook.user.sqlservice.updatable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.springbook.user.sqlservice.SqlNotFoundException;
import com.springbook.user.sqlservice.SqlUpdateFailureException;
import com.springbook.user.sqlservice.UpdatableSqlRegistry;

public class ConcurrentHashMapSqlRegistry implements UpdatableSqlRegistry {
	private Map<String, String> sqlMap = new ConcurrentHashMap<String, String>();

	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if (sql == null)  throw new SqlNotFoundException(key + "�� �̿��ؼ� SQL�� ã�� �� �����ϴ�");
		else return sql;
	}

	public void registerSql(String key, String sql) { sqlMap.put(key, sql);	}

	public void updateSql(String key, String sql) throws SqlUpdateFailureException {
		if (sqlMap.get(key) == null) {
			throw new SqlUpdateFailureException(key + "�� �ش��ϴ� SQL�� ã�� �� �����ϴ�");
		}
		
		sqlMap.put(key, sql);
	}

	public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException {
		for(Map.Entry<String, String> entry : sqlmap.entrySet()) {
			updateSql(entry.getKey(), entry.getValue());
		}
	}
}

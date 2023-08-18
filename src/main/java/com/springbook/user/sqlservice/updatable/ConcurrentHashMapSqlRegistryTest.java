package com.springbook.user.sqlservice.updatable;

import com.springbook.user.sqlservice.UpdatableSqlRegistry;


public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		return new ConcurrentHashMapSqlRegistry();
	}
}

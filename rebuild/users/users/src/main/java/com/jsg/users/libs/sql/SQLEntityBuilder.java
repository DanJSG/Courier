package com.jsg.users.libs.sql;

import java.sql.ResultSet;

public interface SQLEntityBuilder<T extends SQLEntity> {

	public T fromResultSet(ResultSet sqlResults);
	
}

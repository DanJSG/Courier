package com.jsg.chatterbox.libs.sql;

import java.sql.ResultSet;

public interface SQLEntityBuilder<T extends SQLEntity> {

	public T fromResultSet(ResultSet sqlResults);
	
}

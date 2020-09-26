package com.jsg.chatterbox.libs.sql;

import java.sql.ResultSet;

public interface SQLEntityBuilder<T extends SQLEntity> {

	T fromResultSet(ResultSet sqlResults);
	
}

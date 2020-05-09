package com.jsg.courier_old.repositories;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jsg.courier_old.datatypes.UserInfo;

public class UserInfoRepository extends MySQLRepository implements SQLRepository<UserInfo> {

	public UserInfoRepository() throws Exception {
		this.tableName = "users.info";
		super.openConnection();
	}
	
	@Override
	public Boolean save(UserInfo item) throws Exception {
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("id", item.getId());
		valueMap.put("displayname", item.getDisplayName());
		valueMap.put("bio", item.getBio());
		try {
			super.save(valueMap);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There is already a user in the database with the ID: " + item.getId() + ".");
			return false;
		}
	}

	@Override
	public <V> List<UserInfo> findWhereEqual(String searchColumn, V value) {
		return findWhereEqual(searchColumn, value, 0);
	}

	@Override
	public <V> List<UserInfo> findWhereEqual(String searchColumn, V value, int limit) {
		try {
			ResultSet results = super.findWhereEquals(searchColumn, value, "*", limit);
			ArrayList<UserInfo> usersInfo = new ArrayList<>();
			while(results.next()) {
				usersInfo.add(new UserInfo(results.getLong("id"), results.getString("displayname"), results.getString("bio")));
			}
			if(usersInfo.size() == 0) {
				throw new Exception();
			}
			return usersInfo;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Boolean closeConnection() throws Exception {
		return super.closeConnection();
	}

}

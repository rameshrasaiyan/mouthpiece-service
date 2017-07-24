package com.selsoft.mouthpiece.dao.impl;

import org.springframework.stereotype.Repository;

import com.selsoft.mouthpiece.dao.UserDao;
import com.selsoft.mouthpiece.models.User;

@Repository
public class UserDaoImpl implements UserDao {

	@Override
	public void saveNewUser(User user) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean findUserByEmailId(User user) {
		// TODO Auto-generated method stub
		return false;
	}

}

package com.selsoft.mouthpiece.service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.selsoft.mouthpiece.dao.impl.UserDaoImpl;
import com.selsoft.mouthpiece.models.Error;
import com.selsoft.mouthpiece.models.User;
import com.selsoft.mouthpiece.utils.constants.ErrorConstants;

@Service
public class UserService {
	
	@Autowired
	private UserDaoImpl userDao = null;
	
	public User saveNewUser(User user) {
		if(user == null) {
			user = new User();
			Error error = new Error(ErrorConstants.INVALID_USER_DATA, ErrorConstants.INVALID_USER_DATA_ERROR_MESSAGE);
			user.setError(error);
		} else {
			if(StringUtils.isBlank(user.getFirstName()) ||
					StringUtils.isBlank(user.getLastName()) ||
					StringUtils.isBlank(user.getEmailId()) ||
					StringUtils.isBlank(user.getPassword())) {
				Error error = new Error(ErrorConstants.INVALID_USER_DATA, ErrorConstants.INVALID_USER_DATA_ERROR_MESSAGE);
				user.setError(error);
			}
		}
		
		userDao.findUserByEmailId(user);
		return user;
	}

}

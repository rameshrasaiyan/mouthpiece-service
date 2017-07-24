package com.selsoft.mouthpiece.dao;

import com.selsoft.mouthpiece.models.User;

public interface UserDao {
	
	public void saveNewUser(User user);/* {
		userDataRepository.save(user);
	}*/
	
	public boolean findUserByEmailId(User user);/* {
		return (userDataRepository.findByEmailId(StringUtils.lowerCase(StringUtils.trim(user.getEmailId()))) != null);
	}*/

}

package com.smartfit.backend.service;


import com.smartfit.backend.entity.UserProfile;
import com.smartfit.backend.mapper.UserProfileMapper;
import org.springframework.stereotype.Service;


@Service
public class UserProfileService {


    private final UserProfileMapper userProfileMapper;


    public UserProfileService(UserProfileMapper userProfileMapper){

        this.userProfileMapper = userProfileMapper;

    }


    public UserProfile getUserById(Long id){

        return userProfileMapper.findById(id);

    }
    public void save(UserProfile userProfile){

        userProfileMapper.insert(userProfile);

    }
    public void update(UserProfile userProfile){

        userProfileMapper.update(userProfile);

    }
    public void delete(Long id){

        userProfileMapper.delete(id);

    }

}
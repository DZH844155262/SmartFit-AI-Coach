package com.smartfit.backend.service;

import com.smartfit.backend.dto.FitnessProfileCreateRequest;
import com.smartfit.backend.entity.FitnessProfile;
import com.smartfit.backend.mapper.AppUserMapper;
import com.smartfit.backend.mapper.FitnessProfileMapper;
import org.springframework.stereotype.Service;
import com.smartfit.backend.exception.BusinessException;
import org.springframework.http.HttpStatus;

@Service
public class FitnessProfileService {

    private final AppUserMapper appUserMapper;
    private final FitnessProfileMapper fitnessProfileMapper;


    public FitnessProfileService(
            AppUserMapper appUserMapper,
            FitnessProfileMapper fitnessProfileMapper
    ) {
        this.appUserMapper = appUserMapper;
        this.fitnessProfileMapper = fitnessProfileMapper;
    }


    public FitnessProfile createProfile(
            Long userId,
            FitnessProfileCreateRequest request
    ) {

        // 1. 判断用户是否真实存在
        if (appUserMapper.countById(userId) == 0) {

            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "用户不存在"
            );
        }


        // 2. 判断用户是否已经完成过建档
        FitnessProfile existingProfile =
                fitnessProfileMapper.findByUserId(userId);

        if (existingProfile != null) {

            throw new BusinessException(
                    HttpStatus.CONFLICT,
                    "该用户已经完成健身建档"
            );
        }


        // 3. DTO 转换成 Entity
        FitnessProfile profile = new FitnessProfile();

        profile.setUserId(userId);
        profile.setSex(request.getSex());
        profile.setAge(request.getAge());
        profile.setHeightCm(request.getHeightCm());
        profile.setWeightKg(request.getWeightKg());
        profile.setGoal(request.getGoal());
        profile.setExperienceLevel(request.getExperienceLevel());
        profile.setWeeklyFrequency(request.getWeeklyFrequency());


        // 4. 保存
        fitnessProfileMapper.insert(profile);


        // 5. 重新查询，拿到数据库生成的时间等完整信息
        return fitnessProfileMapper.findByUserId(userId);
    }
}
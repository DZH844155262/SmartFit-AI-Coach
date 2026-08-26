package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.FitnessProfile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

public interface FitnessProfileMapper {

    @Select("""
            SELECT *
            FROM fitness_profile
            WHERE user_id = #{userId}
            """)
    FitnessProfile findByUserId(Long userId);


    @Insert("""
            INSERT INTO fitness_profile
            (
                user_id,
                sex,
                age,
                height_cm,
                weight_kg,
                goal,
                experience_level,
                weekly_frequency
            )
            VALUES
            (
                #{userId},
                #{sex},
                #{age},
                #{heightCm},
                #{weightKg},
                #{goal},
                #{experienceLevel},
                #{weeklyFrequency}
            )
            """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "id"
    )
    int insert(FitnessProfile fitnessProfile);
}
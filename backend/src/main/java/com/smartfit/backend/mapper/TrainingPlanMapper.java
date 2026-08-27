package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.TrainingPlan;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface TrainingPlanMapper {

    // 根据 planId 查询完整的 Plan 主体
    @Select("""
            SELECT *
            FROM training_plan
            WHERE id = #{id}
            """)
    TrainingPlan findById(Long id);


    // 判断某个计划是否属于指定用户
    @Select("""
            SELECT COUNT(*)
            FROM training_plan
            WHERE id = #{planId}
              AND user_id = #{userId}
            """)
    int countByIdAndUserId(
            @Param("planId") Long planId,
            @Param("userId") Long userId
    );


    // 创建训练计划
    @Insert("""
            INSERT INTO training_plan
            (
                user_id,
                name,
                goal,
                status,
                start_date,
                end_date,
                weekly_frequency
            )
            VALUES
            (
                #{userId},
                #{name},
                #{goal},
                #{status},
                #{startDate},
                #{endDate},
                #{weeklyFrequency}
            )
            """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "id"
    )
    int insert(TrainingPlan trainingPlan);
}
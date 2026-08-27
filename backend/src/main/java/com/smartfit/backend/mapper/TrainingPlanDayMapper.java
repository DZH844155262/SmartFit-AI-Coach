package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.TrainingPlanDay;
import com.smartfit.backend.vo.TrainingPlanDayVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TrainingPlanDayMapper {

    // 创建训练计划日
    @Insert("""
            INSERT INTO training_plan_day
            (
                plan_id,
                day_number,
                title,
                notes
            )
            VALUES
            (
                #{planId},
                #{dayNumber},
                #{title},
                #{notes}
            )
            """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "id"
    )
    int insert(TrainingPlanDay trainingPlanDay);


    // 查询某个 Plan 下的所有训练日
    @Select("""
            SELECT
                id,
                day_number,
                title,
                notes
            FROM training_plan_day
            WHERE plan_id = #{planId}
            ORDER BY day_number
            """)
    List<TrainingPlanDayVO> findByPlanId(Long planId);


    // 根据 planDayId 查询具体的训练日
    @Select("""
            SELECT
                id,
                plan_id,
                day_number,
                title,
                notes
            FROM training_plan_day
            WHERE id = #{id}
            """)
    TrainingPlanDay findById(Long id);
}
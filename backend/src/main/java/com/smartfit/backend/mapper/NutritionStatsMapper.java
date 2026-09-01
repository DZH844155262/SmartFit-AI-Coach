package com.smartfit.backend.mapper;

import com.smartfit.backend.vo.DailyNutritionSummaryVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

public interface NutritionStatsMapper {

    @Select("""
            SELECT
                nm.meal_date AS date,

                ROUND(
                    COALESCE(
                        SUM(nmi.calories),
                        0
                    ),
                    2
                ) AS total_calories,

                ROUND(
                    COALESCE(
                        SUM(nmi.protein_g),
                        0
                    ),
                    2
                ) AS total_protein_g,

                ROUND(
                    COALESCE(
                        SUM(nmi.carbohydrate_g),
                        0
                    ),
                    2
                ) AS total_carbohydrate_g,

                ROUND(
                    COALESCE(
                        SUM(nmi.fat_g),
                        0
                    ),
                    2
                ) AS total_fat_g

            FROM nutrition_meal nm

            JOIN nutrition_meal_item nmi
                ON nmi.meal_id = nm.id

            WHERE nm.user_id = #{userId}

              AND nm.meal_date
                  BETWEEN #{startDate}
                  AND #{endDate}

            GROUP BY nm.meal_date

            ORDER BY nm.meal_date
            """)
    List<DailyNutritionSummaryVO> findDailySummary(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
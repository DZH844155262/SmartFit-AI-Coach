package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.NutritionMeal;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface NutritionMealMapper {

    @Insert("""
            INSERT INTO nutrition_meal
            (
                user_id,
                meal_date,
                meal_type,
                notes
            )
            VALUES
            (
                #{userId},
                #{mealDate},
                #{mealType},
                #{notes}
            )
            """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "id"
    )
    int insert(NutritionMeal meal);
}
package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.NutritionMealItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

public interface NutritionMealItemMapper {

    @Insert("""
            INSERT INTO nutrition_meal_item
            (
                meal_id,
                food_id,
                food_name,
                portion_value,
                portion_unit,
                estimated_weight_g,
                confirmed_weight_g,
                used_weight_g,
                weight_source,
                record_source,
                calories,
                protein_g,
                carbohydrate_g,
                fat_g
            )
            VALUES
            (
                #{mealId},
                #{foodId},
                #{foodName},
                #{portionValue},
                #{portionUnit},
                #{estimatedWeightG},
                #{confirmedWeightG},
                #{usedWeightG},
                #{weightSource},
                #{recordSource},
                #{calories},
                #{proteinG},
                #{carbohydrateG},
                #{fatG}
            )
            """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "id"
    )
    int insert(NutritionMealItem item);
}
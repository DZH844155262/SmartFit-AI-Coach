package com.smartfit.backend.mapper;

import com.smartfit.backend.entity.Food;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface FoodMapper {

    /*
     * 根据ID查询食物
     */
    @Select("""
            SELECT
                id,
                name,
                name_en,
                calories_per_100g,
                protein_per_100g,
                carbohydrate_per_100g,
                fat_per_100g,
                created_time,
                updated_time
            FROM food
            WHERE id = #{id}
            """)
    Food findById(
            Long id
    );

    @Select("""
        SELECT
            f.id,
            f.name,
            f.name_en,
            f.calories_per_100g,
            f.protein_per_100g,
            f.carbohydrate_per_100g,
            f.fat_per_100g,
            f.created_time,
            f.updated_time

        FROM food f

        INNER JOIN food_alias fa

        ON f.id = fa.food_id

        WHERE fa.alias LIKE CONCAT(
            '%',
            #{keyword},
            '%'
        )

        LIMIT 1
        """)
    Food findByAlias(
            @Param("keyword")
            String keyword
    );
    @Select("""
        SELECT
            id,
            name,
            name_en,
            calories_per_100g,
            protein_per_100g,
            carbohydrate_per_100g,
            fat_per_100g,
            created_time,
            updated_time
        FROM food

        WHERE name LIKE CONCAT(
                '%',
                #{keyword},
                '%'
        )

        OR name_en LIKE CONCAT(
                '%',
                #{keyword},
                '%'
        )

        LIMIT 1
        """)
    Food findBestMatchByName(
            String keyword
    );


    /*
     * 根据名称模糊搜索。
     *
     * 后面Vision识别“鸡胸肉”后，
     * 就可以用这个方法匹配food表。
     */


    @Select("""
            SELECT
                id,
                name,
                name_en,
                calories_per_100g,
                protein_per_100g,
                carbohydrate_per_100g,
                fat_per_100g,
                created_time,
                updated_time
            FROM food

            WHERE name LIKE CONCAT(
                    '%',
                    #{keyword},
                    '%'
            )

            OR name_en LIKE CONCAT(
                    '%',
                    #{keyword},
                    '%'
            )

            ORDER BY id

            LIMIT 10
            """)
    List<Food> findByKeyword(
            @Param("keyword")
            String keyword
    );


    /*
     * 新增食物
     */
    @Insert("""
            INSERT INTO food
            (
                name,
                name_en,
                calories_per_100g,
                protein_per_100g,
                carbohydrate_per_100g,
                fat_per_100g
            )
            VALUES
            (
                #{name},
                #{nameEn},
                #{caloriesPer100g},
                #{proteinPer100g},
                #{carbohydratePer100g},
                #{fatPer100g}
            )
            """)
    @Options(
            useGeneratedKeys = true,
            keyProperty = "id"
    )
    int insert(
            Food food
    );
}
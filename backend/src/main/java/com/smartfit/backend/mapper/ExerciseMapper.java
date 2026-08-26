package com.smartfit.backend.mapper;

import org.apache.ibatis.annotations.Select;

public interface ExerciseMapper {

    @Select("""
            SELECT COUNT(*)
            FROM exercise
            WHERE id = #{id}
            """)
    int countById(Long id);
}
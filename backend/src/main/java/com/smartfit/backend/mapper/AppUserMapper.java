package com.smartfit.backend.mapper;

import org.apache.ibatis.annotations.Select;

public interface AppUserMapper {

    @Select("""
            SELECT COUNT(*)
            FROM app_user
            WHERE id = #{id}
            """)
    int countById(Long id);
}
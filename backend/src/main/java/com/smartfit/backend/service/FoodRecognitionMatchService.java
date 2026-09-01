package com.smartfit.backend.service;

import com.smartfit.backend.entity.Food;
import com.smartfit.backend.mapper.FoodMapper;
import com.smartfit.backend.vo.FoodMatchVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class FoodRecognitionMatchService {


    private final FoodMapper foodMapper;


    public FoodRecognitionMatchService(
            FoodMapper foodMapper
    ) {
        this.foodMapper = foodMapper;
    }



    /**
     * Vision识别结果匹配数据库食物
     */
    public FoodMatchVO match(
            String recognizedName,
            BigDecimal suggestedWeightG
    ) {


        // 1. 先清洗AI名称
        String keyword =
                normalizeFoodName(
                        recognizedName
                );


        // 2. 查询food表
        Food food =
                foodMapper.findByAlias(
                        keyword
                );


        if(food == null){

            food =
                    foodMapper.findBestMatchByName(
                            keyword
                    );

        }

        FoodMatchVO result =
                new FoodMatchVO();


        result.setRecognizedName(
                recognizedName
        );


        result.setSuggestedWeightG(
                suggestedWeightG
        );



        // 3. 没匹配到
        if(food == null){

            result.setMatchConfidence(
                    "LOW"
            );

            result.setFoodId(
                    null
            );

            result.setMatchedFoodName(
                    null
            );


            return result;
        }



        // 4. 匹配成功

        result.setFoodId(
                food.getId()
        );


        result.setMatchedFoodName(
                food.getName()
        );


        result.setMatchConfidence(
                "HIGH"
        );


        return result;
    }




    /**
     * AI名称清洗
     *
     * 生鸡胸肉
     * ↓
     * 鸡胸肉
     */
    private String normalizeFoodName(
            String name
    ){

        if(name == null){
            return "";
        }


        return name
                .replace(
                        "生",
                        ""
                )
                .replace(
                        "熟",
                        ""
                )
                .replace(
                        "的",
                        ""
                )
                .trim();
    }

}
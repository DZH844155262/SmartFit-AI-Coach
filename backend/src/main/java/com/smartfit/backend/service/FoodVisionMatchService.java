package com.smartfit.backend.service;


import com.smartfit.backend.vo.FoodMatchVO;
import com.smartfit.backend.vo.FoodVisionItemVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class FoodVisionMatchService {


    private final FoodRecognitionMatchService matchService;



    public FoodVisionMatchService(
            FoodRecognitionMatchService matchService
    ){

        this.matchService =
                matchService;

    }



    public List<FoodMatchVO> matchFoods(
            List<FoodVisionItemVO> foods
    ){

        List<FoodMatchVO> result =
                new ArrayList<>();


        for(
                FoodVisionItemVO item
                : foods
        ){


            FoodMatchVO match =
                    matchService.match(
                            item.getFoodName(),
                            item.getSuggestedWeightG()
                    );


            result.add(
                    match
            );
        }


        return result;
    }

}
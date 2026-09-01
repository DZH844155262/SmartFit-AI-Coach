package com.smartfit.backend.controller;

import com.smartfit.backend.common.Result;
import com.smartfit.backend.service.FoodVisionMatchService;
import com.smartfit.backend.service.FoodVisionService;
import com.smartfit.backend.vo.FoodMatchVO;
import com.smartfit.backend.vo.FoodVisionRecognitionVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/vision")
public class FoodVisionController {


    private final FoodVisionService foodVisionService;


    private final FoodVisionMatchService foodVisionMatchService;



    public FoodVisionController(
            FoodVisionService foodVisionService,
            FoodVisionMatchService foodVisionMatchService
    ){

        this.foodVisionService =
                foodVisionService;

        this.foodVisionMatchService =
                foodVisionMatchService;
    }



    /**
     * 食物图片识别
     *
     * POST
     * /api/vision/food
     */
    @PostMapping("/food")
    public Result<List<FoodMatchVO>> recognizeFood(

            @RequestPart("file")
            MultipartFile file
    ) {


        // 第一步：
        // Vision识别图片
        FoodVisionRecognitionVO visionResult =
                foodVisionService.recognizeFood(
                        file
                );


        // 第二步：
        // 匹配food数据库
        List<FoodMatchVO> matchedFoods =
                foodVisionMatchService.matchFoods(
                        visionResult.getFoods()
                );


        // 第三步：
        // 返回标准食物
        return Result.success(
                matchedFoods
        );
    }
}
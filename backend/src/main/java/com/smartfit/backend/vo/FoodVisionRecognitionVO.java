package com.smartfit.backend.vo;

import java.util.List;

public class FoodVisionRecognitionVO {

    /*
     * 图片中识别到的所有主要食物。
     */
    private List<FoodVisionItemVO> foods;


    public List<FoodVisionItemVO> getFoods() {
        return foods;
    }

    public void setFoods(
            List<FoodVisionItemVO> foods
    ) {
        this.foods = foods;
    }
}
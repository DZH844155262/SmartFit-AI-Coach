package com.smartfit.backend.service;

import com.smartfit.backend.dto.NutritionMealCreateRequest;
import com.smartfit.backend.dto.NutritionMealItemRequest;
import com.smartfit.backend.entity.Food;
import com.smartfit.backend.entity.NutritionMeal;
import com.smartfit.backend.entity.NutritionMealItem;
import com.smartfit.backend.exception.BusinessException;
import com.smartfit.backend.mapper.AppUserMapper;
import com.smartfit.backend.mapper.FoodMapper;
import com.smartfit.backend.mapper.NutritionMealItemMapper;
import com.smartfit.backend.mapper.NutritionMealMapper;
import com.smartfit.backend.vo.NutritionMealCreateVO;
import com.smartfit.backend.vo.NutritionMealItemVO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartfit.backend.mapper.NutritionStatsMapper;
import com.smartfit.backend.vo.DailyNutritionSummaryVO;
import com.smartfit.backend.vo.NutritionPeriodSummaryVO;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import com.smartfit.backend.dto.NutritionVisionMealCreateRequest;
import com.smartfit.backend.dto.NutritionVisionMealItemRequest;


@Service
public class NutritionService {

    private final AppUserMapper appUserMapper;

    private final FoodMapper foodMapper;

    private final NutritionMealMapper nutritionMealMapper;

    private final NutritionMealItemMapper nutritionMealItemMapper;

    private final NutritionStatsMapper nutritionStatsMapper;

    public List<DailyNutritionSummaryVO> getNutritionSummary(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        /*
         * 1. 用户必须存在
         */
        if (appUserMapper.countById(userId) == 0) {

            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "用户不存在"
            );
        }


        /*
         * 2. 日期不能为空
         */
        if (startDate == null || endDate == null) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "startDate和endDate不能为空"
            );
        }


        /*
         * 3. 开始时间不能晚于结束时间
         */
        if (startDate.isAfter(endDate)) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "startDate不能晚于endDate"
            );
        }


        /*
         * 4. V1最多一次查看一年，
         * 防止请求过大的日期范围。
         */
        if (startDate.plusYears(1).isBefore(endDate)) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "查询范围不能超过1年"
            );
        }


        return nutritionStatsMapper.findDailySummary(
                userId,
                startDate,
                endDate
        );
    }

    public NutritionPeriodSummaryVO getNutritionPeriodSummary(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        /*
         * 1. 复用之前已经写好的每日营养统计
         */
        List<DailyNutritionSummaryVO> dailyData =
                getNutritionSummary(
                        userId,
                        startDate,
                        endDate
                );


        /*
         * 2. 查询区间一共有多少天
         *
         * 例如：
         * 8月25日到8月31日
         * = 7天
         */
        int periodDays =
                (int) ChronoUnit.DAYS.between(
                        startDate,
                        endDate
                ) + 1;


        /*
         * 3. 实际有饮食记录的天数
         *
         * 比如最近7天只记录了3天：
         * recordedDays = 3
         */
        int recordedDays =
                dailyData.size();


        /*
         * 4. 初始化周期总营养
         */
        BigDecimal totalCalories =
                BigDecimal.ZERO;

        BigDecimal totalProtein =
                BigDecimal.ZERO;

        BigDecimal totalCarbohydrate =
                BigDecimal.ZERO;

        BigDecimal totalFat =
                BigDecimal.ZERO;


        /*
         * 5. 把每天的数据累加起来
         */
        for (DailyNutritionSummaryVO day : dailyData) {

            totalCalories =
                    totalCalories.add(
                            safe(
                                    day.getTotalCalories()
                            )
                    );

            totalProtein =
                    totalProtein.add(
                            safe(
                                    day.getTotalProteinG()
                            )
                    );

            totalCarbohydrate =
                    totalCarbohydrate.add(
                            safe(
                                    day.getTotalCarbohydrateG()
                            )
                    );

            totalFat =
                    totalFat.add(
                            safe(
                                    day.getTotalFatG()
                            )
                    );
        }


        /*
         * 6. 创建最终返回结果
         */
        NutritionPeriodSummaryVO result =
                new NutritionPeriodSummaryVO();


        result.setStartDate(
                startDate
        );

        result.setEndDate(
                endDate
        );

        result.setPeriodDays(
                periodDays
        );

        result.setRecordedDays(
                recordedDays
        );


        /*
         * 7. 记录覆盖率
         *
         * 例如：
         * 最近7天记录了3天
         *
         * 3 / 7 × 100
         * = 42.9%
         */
        BigDecimal coverage =
                BigDecimal.valueOf(
                                recordedDays
                        )
                        .divide(
                                BigDecimal.valueOf(
                                        periodDays
                                ),
                                4,
                                RoundingMode.HALF_UP
                        )
                        .multiply(
                                BigDecimal.valueOf(100)
                        )
                        .setScale(
                                1,
                                RoundingMode.HALF_UP
                        );


        result.setRecordCoveragePercent(
                coverage
        );


        /*
         * 8. 周期总摄入
         */
        result.setTotalCalories(
                totalCalories
        );

        result.setTotalProteinG(
                totalProtein
        );

        result.setTotalCarbohydrateG(
                totalCarbohydrate
        );

        result.setTotalFatG(
                totalFat
        );


        /*
         * 9. 平均值
         *
         * 按你刚刚确定的规则：
         *
         * 只除以“实际有记录的天数”
         *
         * 不把没记录的日子当0。
         */
        if (recordedDays > 0) {

            BigDecimal divisor =
                    BigDecimal.valueOf(
                            recordedDays
                    );


            result.setAverageCaloriesPerRecordedDay(
                    totalCalories.divide(
                            divisor,
                            2,
                            RoundingMode.HALF_UP
                    )
            );


            result.setAverageProteinGPerRecordedDay(
                    totalProtein.divide(
                            divisor,
                            2,
                            RoundingMode.HALF_UP
                    )
            );


            result.setAverageCarbohydrateGPerRecordedDay(
                    totalCarbohydrate.divide(
                            divisor,
                            2,
                            RoundingMode.HALF_UP
                    )
            );


            result.setAverageFatGPerRecordedDay(
                    totalFat.divide(
                            divisor,
                            2,
                            RoundingMode.HALF_UP
                    )
            );
        }


        /*
         * 10. 每天的数据一起返回
         *
         * 前端以后用它画7天/30天趋势图。
         */
        result.setDailyData(
                dailyData
        );


        return result;
    }
    public NutritionService(
            AppUserMapper appUserMapper,
            FoodMapper foodMapper,
            NutritionMealMapper nutritionMealMapper,
            NutritionMealItemMapper nutritionMealItemMapper,
            NutritionStatsMapper nutritionStatsMapper
    ) {

        this.appUserMapper = appUserMapper;
        this.foodMapper = foodMapper;
        this.nutritionMealMapper = nutritionMealMapper;
        this.nutritionMealItemMapper =
                nutritionMealItemMapper;
        this.nutritionStatsMapper = nutritionStatsMapper;
    }


    @Transactional
    public NutritionMealCreateVO createManualMeal(
            Long userId,
            NutritionMealCreateRequest request
    ) {

        /*
         * 1. 用户存在性检查
         */
        if (appUserMapper.countById(userId) == 0) {

            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "用户不存在"
            );
        }


        /*
         * 2. mealType校验
         */
        List<String> allowedMealTypes =
                List.of(
                        "BREAKFAST",
                        "LUNCH",
                        "DINNER",
                        "SNACK"
                );


        if (!allowedMealTypes.contains(
                request.getMealType()
        )) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "非法mealType"
            );
        }


        /*
         * 3. 创建Meal
         */
        NutritionMeal meal =
                new NutritionMeal();

        meal.setUserId(
                userId
        );

        meal.setMealDate(
                request.getMealDate()
        );

        meal.setMealType(
                request.getMealType()
        );

        meal.setNotes(
                request.getNotes()
        );


        nutritionMealMapper.insert(
                meal
        );


        /*
         * 4. 总营养
         */
        BigDecimal totalCalories =
                BigDecimal.ZERO;

        BigDecimal totalProtein =
                BigDecimal.ZERO;

        BigDecimal totalCarbohydrate =
                BigDecimal.ZERO;

        BigDecimal totalFat =
                BigDecimal.ZERO;


        List<NutritionMealItemVO> itemVOs =
                new ArrayList<>();


        /*
         * 5. 一项一项处理食物
         */
        for (NutritionMealItemRequest itemRequest
                : request.getItems()) {


            Food food =
                    foodMapper.findById(
                            itemRequest.getFoodId()
                    );


            if (food == null) {

                throw new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "食物不存在，foodId="
                                + itemRequest.getFoodId()
                );
            }


            BigDecimal weight =
                    itemRequest.getWeightG();


            /*
             * =================================================
             * 营养计算：
             *
             * 每100g营养
             * ×
             * 实际重量
             * ÷
             * 100
             * =================================================
             */
            BigDecimal calories =
                    calculateByWeight(
                            food.getCaloriesPer100g(),
                            weight
                    );


            BigDecimal protein =
                    calculateByWeight(
                            food.getProteinPer100g(),
                            weight
                    );


            BigDecimal carbohydrate =
                    calculateByWeight(
                            food.getCarbohydratePer100g(),
                            weight
                    );


            BigDecimal fat =
                    calculateByWeight(
                            food.getFatPer100g(),
                            weight
                    );


            /*
             * 6. 保存历史快照
             *
             * 当前是MANUAL路径：
             *
             * estimatedWeight = NULL
             * confirmedWeight = 用户输入
             * usedWeight = 用户输入
             */
            NutritionMealItem mealItem =
                    new NutritionMealItem();


            mealItem.setMealId(
                    meal.getId()
            );


            mealItem.setFoodId(
                    food.getId()
            );


            mealItem.setFoodName(
                    food.getName()
            );


            mealItem.setPortionValue(
                    weight
            );


            mealItem.setPortionUnit(
                    "GRAM"
            );


            mealItem.setEstimatedWeightG(
                    null
            );


            mealItem.setConfirmedWeightG(
                    weight
            );


            mealItem.setUsedWeightG(
                    weight
            );


            mealItem.setWeightSource(
                    "USER_INPUT"
            );


            mealItem.setRecordSource(
                    "MANUAL"
            );


            mealItem.setCalories(
                    calories
            );


            mealItem.setProteinG(
                    protein
            );


            mealItem.setCarbohydrateG(
                    carbohydrate
            );


            mealItem.setFatG(
                    fat
            );


            nutritionMealItemMapper.insert(
                    mealItem
            );


            /*
             * 7. 累加整餐营养
             */
            totalCalories =
                    totalCalories.add(
                            calories
                    );

            totalProtein =
                    totalProtein.add(
                            protein
                    );

            totalCarbohydrate =
                    totalCarbohydrate.add(
                            carbohydrate
                    );

            totalFat =
                    totalFat.add(
                            fat
                    );


            /*
             * 8. 返回给前端的单项VO
             */
            NutritionMealItemVO itemVO =
                    new NutritionMealItemVO();


            itemVO.setFoodId(
                    food.getId()
            );

            itemVO.setFoodName(
                    food.getName()
            );

            itemVO.setWeightG(
                    weight
            );

            itemVO.setCalories(
                    calories
            );

            itemVO.setProteinG(
                    protein
            );

            itemVO.setCarbohydrateG(
                    carbohydrate
            );

            itemVO.setFatG(
                    fat
            );


            itemVOs.add(
                    itemVO
            );
        }


        /*
         * 9. 返回整餐
         */
        NutritionMealCreateVO result =
                new NutritionMealCreateVO();


        result.setMealId(
                meal.getId()
        );

        result.setMealDate(
                meal.getMealDate()
        );

        result.setMealType(
                meal.getMealType()
        );

        result.setTotalCalories(
                totalCalories
        );

        result.setTotalProteinG(
                totalProtein
        );

        result.setTotalCarbohydrateG(
                totalCarbohydrate
        );

        result.setTotalFatG(
                totalFat
        );

        result.setItems(
                itemVOs
        );


        return result;
    }

    @Transactional
    public NutritionMealCreateVO createVisionMeal(
            Long userId,
            NutritionVisionMealCreateRequest request
    ) {

        /*
         * 1. 用户检查
         */
        if (appUserMapper.countById(userId) == 0) {

            throw new BusinessException(
                    HttpStatus.NOT_FOUND,
                    "用户不存在"
            );
        }


        /*
         * 2. mealType检查
         */
        List<String> allowedMealTypes =
                List.of(
                        "BREAKFAST",
                        "LUNCH",
                        "DINNER",
                        "SNACK"
                );


        if (!allowedMealTypes.contains(
                request.getMealType()
        )) {

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "非法mealType"
            );
        }


        /*
         * 3. 创建这一餐
         */
        NutritionMeal meal =
                new NutritionMeal();

        meal.setUserId(
                userId
        );

        meal.setMealDate(
                request.getMealDate()
        );

        meal.setMealType(
                request.getMealType()
        );

        meal.setNotes(
                request.getNotes()
        );


        nutritionMealMapper.insert(
                meal
        );


        /*
         * 4. 初始化整餐总营养
         */
        BigDecimal totalCalories =
                BigDecimal.ZERO;

        BigDecimal totalProtein =
                BigDecimal.ZERO;

        BigDecimal totalCarbohydrate =
                BigDecimal.ZERO;

        BigDecimal totalFat =
                BigDecimal.ZERO;


        List<NutritionMealItemVO> itemVOs =
                new ArrayList<>();


        /*
         * 5. 逐个处理Vision识别出的食物
         */
        for (NutritionVisionMealItemRequest itemRequest
                : request.getItems()) {


            Food food =
                    foodMapper.findById(
                            itemRequest.getFoodId()
                    );


            if (food == null) {

                throw new BusinessException(
                        HttpStatus.NOT_FOUND,
                        "食物不存在，foodId="
                                + itemRequest.getFoodId()
                );
            }


            BigDecimal estimatedWeight =
                    itemRequest.getEstimatedWeightG();


            BigDecimal confirmedWeight =
                    itemRequest.getConfirmedWeightG();


            /*
             * =====================================================
             * 最关键的HITL规则：
             *
             * 用户修改过重量
             * → 使用用户值
             *
             * 用户没有修改
             * → 使用Vision估计
             * =====================================================
             */
            BigDecimal usedWeight =
                    confirmedWeight != null
                            ? confirmedWeight
                            : estimatedWeight;


            String weightSource =
                    confirmedWeight != null
                            ? "USER_INPUT"
                            : "VISION_ESTIMATED";


            /*
             * 6. Java确定性计算营养
             */
            BigDecimal calories =
                    calculateByWeight(
                            food.getCaloriesPer100g(),
                            usedWeight
                    );


            BigDecimal protein =
                    calculateByWeight(
                            food.getProteinPer100g(),
                            usedWeight
                    );


            BigDecimal carbohydrate =
                    calculateByWeight(
                            food.getCarbohydratePer100g(),
                            usedWeight
                    );


            BigDecimal fat =
                    calculateByWeight(
                            food.getFatPer100g(),
                            usedWeight
                    );


            /*
             * 7. 保存nutrition_meal_item
             */
            NutritionMealItem mealItem =
                    new NutritionMealItem();


            mealItem.setMealId(
                    meal.getId()
            );


            mealItem.setFoodId(
                    food.getId()
            );


            mealItem.setFoodName(
                    food.getName()
            );


            /*
             * portion记录最终实际采用的份量
             */
            mealItem.setPortionValue(
                    usedWeight
            );


            mealItem.setPortionUnit(
                    "GRAM"
            );


            /*
             * 保留Vision原始估计
             */
            mealItem.setEstimatedWeightG(
                    estimatedWeight
            );


            /*
             * 如果用户没修改就是NULL
             */
            mealItem.setConfirmedWeightG(
                    confirmedWeight
            );


            /*
             * 真正用于计算的重量
             */
            mealItem.setUsedWeightG(
                    usedWeight
            );


            mealItem.setWeightSource(
                    weightSource
            );


            /*
             * 这条记录来源于图片识别
             */
            mealItem.setRecordSource(
                    "VISION"
            );


            mealItem.setCalories(
                    calories
            );


            mealItem.setProteinG(
                    protein
            );


            mealItem.setCarbohydrateG(
                    carbohydrate
            );


            mealItem.setFatG(
                    fat
            );


            nutritionMealItemMapper.insert(
                    mealItem
            );


            /*
             * 8. 整餐营养累计
             */
            totalCalories =
                    totalCalories.add(
                            calories
                    );

            totalProtein =
                    totalProtein.add(
                            protein
                    );

            totalCarbohydrate =
                    totalCarbohydrate.add(
                            carbohydrate
                    );

            totalFat =
                    totalFat.add(
                            fat
                    );


            /*
             * 9. 返回单项结果
             */
            NutritionMealItemVO itemVO =
                    new NutritionMealItemVO();


            itemVO.setFoodId(
                    food.getId()
            );

            itemVO.setFoodName(
                    food.getName()
            );

            itemVO.setWeightG(
                    usedWeight
            );

            itemVO.setCalories(
                    calories
            );

            itemVO.setProteinG(
                    protein
            );

            itemVO.setCarbohydrateG(
                    carbohydrate
            );

            itemVO.setFatG(
                    fat
            );


            itemVOs.add(
                    itemVO
            );
        }


        /*
         * 10. 返回整餐结果
         */
        NutritionMealCreateVO result =
                new NutritionMealCreateVO();


        result.setMealId(
                meal.getId()
        );

        result.setMealDate(
                meal.getMealDate()
        );

        result.setMealType(
                meal.getMealType()
        );

        result.setTotalCalories(
                totalCalories
        );

        result.setTotalProteinG(
                totalProtein
        );

        result.setTotalCarbohydrateG(
                totalCarbohydrate
        );

        result.setTotalFatG(
                totalFat
        );

        result.setItems(
                itemVOs
        );


        return result;
    }

    /*
     * 每100g营养值
     * ×
     * 实际重量
     * ÷100
     */
    private BigDecimal calculateByWeight(
            BigDecimal per100g,
            BigDecimal weightG
    ) {

        if (per100g == null) {

            return BigDecimal.ZERO;
        }


        return per100g
                .multiply(
                        weightG
                )
                .divide(
                        BigDecimal.valueOf(100),
                        2,
                        RoundingMode.HALF_UP
                );
    }
    private BigDecimal safe(
            BigDecimal value
    ) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }
}
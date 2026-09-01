package com.smartfit.backend.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfit.backend.client.DeepSeekVisionClient;
import com.smartfit.backend.exception.BusinessException;
import com.smartfit.backend.vo.FoodVisionRecognitionVO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.Base64;



@Service
public class FoodVisionService {


    private final DeepSeekVisionClient deepSeekVisionClient;


    private final ObjectMapper objectMapper;



    public FoodVisionService(
            DeepSeekVisionClient deepSeekVisionClient,
            ObjectMapper objectMapper
    ){

        this.deepSeekVisionClient =
                deepSeekVisionClient;

        this.objectMapper =
                objectMapper;
    }



    /**
     * 食物图片识别
     */
    public FoodVisionRecognitionVO recognizeFood(
            MultipartFile file
    ){


        if(file == null || file.isEmpty()){

            throw new BusinessException(
                    HttpStatus.BAD_REQUEST,
                    "请上传食物图片"
            );
        }



        try{


            // 图片转base64

            String base64 =
                    Base64.getEncoder()
                            .encodeToString(
                                    file.getBytes()
                            );



            /*
             * 健身饮食识别Prompt
             */
            String prompt = """

你是一名健身饮食分析助手。

请分析用户上传的食物图片，
用于每日饮食记录和热量估算。

要求：

1. 只识别主要食物。

2. 优先识别：
- 高蛋白食物
- 主食
- 明显影响热量的食物


3. 不需要拆分少量配菜。

例如沙拉：
不要输出：
玉米
胡萝卜
紫甘蓝

可以合并：
蔬菜沙拉


4. 最多输出3个主要食物。

5. 给出估计重量。


只返回JSON：

{
 "foods":[
   {
    "foodName":"",
    "suggestedWeightG":0
   }
 ]
}

不要输出解释文字。

""";


            String response =
                    deepSeekVisionClient.analyzeImage(
                            file.getBytes(),
                            file.getContentType(),
                            prompt
                    );



            System.out.println(
                    "====== Vision Raw Response ======"
            );

            System.out.println(response);

            System.out.println(
                    "================================="
            );



            /*
             * 清洗模型返回
             */
            String json =
                    cleanJson(response);



            return objectMapper.readValue(
                    json,
                    FoodVisionRecognitionVO.class
            );



        }catch(Exception e){


            e.printStackTrace();


            throw new BusinessException(
                    HttpStatus.BAD_GATEWAY,
                    "视觉模型返回了非法识别信息"
            );
        }

    }



    /**
     * 清理DeepSeek返回
     *
     * 防止：
     *
     * ```json
     * {...}
     * ```
     *
     * 或：
     *
     * 分析结果：
     * {...}
     */
    private String cleanJson(
            String response
    ){


        if(response == null){

            return "{}";
        }



        String result =
                response.trim();



        // 去掉markdown

        if(result.startsWith("```")){


            result =
                    result.replace(
                            "```json",
                            ""
                    );


            result =
                    result.replace(
                            "```",
                            ""
                    );


            result =
                    result.trim();

        }



        /*
         * 截取JSON主体
         */

        int start =
                result.indexOf("{");


        int end =
                result.lastIndexOf("}");



        if(start >=0 && end >=0){

            result =
                    result.substring(
                            start,
                            end + 1
                    );
        }



        return result;

    }

}
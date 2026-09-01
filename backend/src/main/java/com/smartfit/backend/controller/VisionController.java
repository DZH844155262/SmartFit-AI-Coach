package com.smartfit.backend.controller;

import com.smartfit.backend.common.Result;
import com.smartfit.backend.service.EquipmentVisionService;
import com.smartfit.backend.vo.EquipmentRecognitionVO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/vision")
public class VisionController {

    private final EquipmentVisionService equipmentVisionService;


    public VisionController(
            EquipmentVisionService equipmentVisionService
    ) {
        this.equipmentVisionService =
                equipmentVisionService;
    }


    @PostMapping("/users/{userId}/equipment")
    public Result<EquipmentRecognitionVO> recognizeEquipment(

            @PathVariable("userId")
            Long userId,

            @RequestParam(
                    name = "planDayId",
                    required = false
            )
            Long planDayId,

            @RequestPart("file")
            MultipartFile file
    ) {

        EquipmentRecognitionVO result =
                equipmentVisionService.recognize(
                        userId,
                        planDayId,
                        file
                );


        return Result.success(
                result
        );
    }
}
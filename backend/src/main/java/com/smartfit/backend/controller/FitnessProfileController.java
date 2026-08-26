package com.smartfit.backend.controller;

import com.smartfit.backend.common.Result;
import com.smartfit.backend.dto.FitnessProfileCreateRequest;
import com.smartfit.backend.entity.FitnessProfile;
import com.smartfit.backend.service.FitnessProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class FitnessProfileController {

    private final FitnessProfileService fitnessProfileService;


    public FitnessProfileController(
            FitnessProfileService fitnessProfileService
    ) {
        this.fitnessProfileService = fitnessProfileService;
    }


    @PostMapping("/{userId}/fitness-profile")
    public Result<FitnessProfile> createProfile(
            @PathVariable Long userId,
            @Valid @RequestBody FitnessProfileCreateRequest request
    ) {

        FitnessProfile profile =
                fitnessProfileService.createProfile(userId, request);

        return Result.success(profile);
    }
}
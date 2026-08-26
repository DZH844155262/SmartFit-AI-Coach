package com.smartfit.backend.controller;


import com.smartfit.backend.entity.UserProfile;
import com.smartfit.backend.service.UserProfileService;
import com.smartfit.backend.common.Result;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class UserController {


    private final UserProfileService userProfileService;


    public UserController(UserProfileService userProfileService){

        this.userProfileService = userProfileService;

    }


    @GetMapping("/{id}")
    public Result<UserProfile> getUser(
            @PathVariable Long id
    ){

        UserProfile user =
                userProfileService.getUserById(id);


        return Result.success(user);

    }
    @PostMapping
    public Result saveUser(
            @RequestBody UserProfile userProfile
    ){

        userProfileService.save(userProfile);

        return Result.success();

    }
    @PutMapping
    public Result updateUser(
            @RequestBody UserProfile userProfile
    ){

        userProfileService.update(userProfile);

        return Result.success(null);

    }
    @DeleteMapping("/{id}")
    public Result deleteUser(
            @PathVariable Long id
    ){

        userProfileService.delete(id);

        return Result.success(null);

    }

}

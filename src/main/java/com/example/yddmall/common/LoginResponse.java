package com.example.yddmall.common;

import com.example.yddmall.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private User user;
    private String token;
}
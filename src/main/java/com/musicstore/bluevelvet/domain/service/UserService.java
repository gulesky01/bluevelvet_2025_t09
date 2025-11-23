package com.musicstore.bluevelvet.domain.service;

import com.musicstore.bluevelvet.api.request.RegisterRequest;
import org.springframework.stereotype.Service;

@Service

public interface UserService {
    void registerNewUser(RegisterRequest request);
}
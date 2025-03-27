// service/KycService.java
package com.example.user.service;

import com.example.bankSphere.dto.UserRequestDto;
import org.springframework.stereotype.Service;

@Service
public class KycService {
    public boolean verifyUser(UserRequestDto userDto) {
        // Call to external KYC API (simulate verification)
        // For now, assume verification always passes
        return true;
    }
}

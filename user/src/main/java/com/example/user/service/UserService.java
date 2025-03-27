package com.example.user.service;

import com.example.bankSphere.dto.AccountDto;
import com.example.bankSphere.entity.User;
import com.example.bankSphere.exception.UserNotFoundException;
import com.example.bankSphere.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountService accountService;

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public List<AccountDto> getUserAccounts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getAccounts().stream()
                .map(account -> {
                    AccountDto accountDto = new AccountDto();
                    accountDto.setAccountId(account.getId());
                    accountDto.setUserId(userId);
                    accountDto.setBalance(account.getBalance());
                    accountDto.setAccountType(account.getAccountType());
                    return accountDto;
                })
                .collect(Collectors.toList());
    }

    public User retrieveUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}

package com.ibs.notification_service.cache;

import com.ibs.notification_service.dtos.responseDto.UserResponseDTO;
import com.ibs.notification_service.feign.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCacheService {

    private final UserClient userClient;

    @Cacheable("usersCache")
    public List<UserResponseDTO> getAllUsers() {
        log.info("Fetching users from UserService via Feign...");
        return userClient.getAllUsers();
    }
}

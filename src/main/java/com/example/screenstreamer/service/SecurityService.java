package com.example.screenstreamer.service;

import com.example.screenstreamer.exception.StatusException;
import com.example.screenstreamer.model.config.SecuritySettings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.function.Function;

@Service
public class SecurityService {
    private final SecuritySettings securitySettings;

    public SecurityService(SecuritySettings securitySettings) {
        this.securitySettings = securitySettings;
    }

    public void checkPassword(String password, Function<SecuritySettings, Boolean> isSecureFunction) {
        var isSecure = isSecureFunction.apply(securitySettings);
        if (isSecure && !Objects.equals(password, securitySettings.getPassword())) {
            throw new StatusException(HttpStatus.BAD_REQUEST);
        }
    }
}

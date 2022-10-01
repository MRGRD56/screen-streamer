package com.example.screenstreamer.model.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "security")
@ConfigurationPropertiesScan
public class SecuritySettings {
    public static final String PASSWORD_HEADER = "X-Password";

    private String password;
    private boolean isSecureView;
    private boolean isSecureControl;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSecureView() {
        return isSecureView;
    }

    public void setSecureView(boolean secureView) {
        isSecureView = secureView;
    }

    public boolean isSecureControl() {
        return isSecureControl;
    }

    public void setSecureControl(boolean secureControl) {
        isSecureControl = secureControl;
    }
}

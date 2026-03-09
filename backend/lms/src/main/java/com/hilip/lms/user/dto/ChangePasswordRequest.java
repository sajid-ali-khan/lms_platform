package com.hilip.lms.user.dto;

public record ChangePasswordRequest(String currentPassword, String newPassword) {}

package com.cryptocurrency.service.email;

public interface EmailService {

    void sendSimpleEmail(String toAddress, String subject, String message);
}

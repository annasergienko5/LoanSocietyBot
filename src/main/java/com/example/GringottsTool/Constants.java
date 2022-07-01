package com.example.GringottsTool;

import org.springframework.beans.factory.annotation.Value;

public interface Constants {
    String BOT_TOKEN=System.getenv("TOKEN_BOT");
    String BOT_USERNAME=System.getenv("BOT_USERNAME");
    String WEBHOOK_PATH=System.getenv("WEBHOOK_PATH");
    String APPLICATION_NAME=System.getenv("APPLICATION_NAME");
    String CREDENTIALS_FILE_PATH=System.getenv("CREDENTIALS_FILE_PATH");
    String TOKENS_DIRECTORY_PATH=System.getenv("TOKENS_DIRECTORY_PATH");
    String SHEET_ID=System.getenv("SHEET_ID");
}

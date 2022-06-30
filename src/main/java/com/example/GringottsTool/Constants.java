package com.example.GringottsTool;

public interface Constants {
    String bot_token=System.getenv("TOKEN_BOT");
    String bot_username=System.getenv("BOT_USERNAME");
    long CREATOR_ID=Long.parseLong(System.getenv("CREATOR_ID"));
    String START_REPLY = "Start using the telegram bot if you are lonely or bored";
    String CHOOSE_OPTION = "Make a choice";
    String DISCUSSION = "Let's discuss!";
    String SMALL_TALK = "Let's talk!";
}

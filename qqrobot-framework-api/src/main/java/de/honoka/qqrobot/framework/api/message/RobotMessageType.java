package de.honoka.qqrobot.framework.api.message;

public enum RobotMessageType {

    TEXT,

    //content为要at的QQ号
    AT,

    //content为InputStream
    IMAGE,

    //content为InputStream
    FILE,

    //content为InputStream
    VOICE
}

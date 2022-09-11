package de.honoka.qqrobot.starter.framework.tester.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("honoka.qqrobot.tester")
public class TesterProperties {}

package cn.keking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "channel")
@Data
public class ChannelConfig {

  private Map<String, Map<String, String>> configs = new HashMap<>();
}

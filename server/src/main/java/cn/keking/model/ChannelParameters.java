package cn.keking.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ChannelParameters {

  /**
   * 渠道名，来自请求
   */
  private String channel;

  /**
   * 渠道参数，用于获取文档内容，来自请求
   */
  private Map<String, String> parameters = new HashMap<>();

  /**
   * 渠道配置，来自配置文件
   */
  private Map<String, String> config;

  public String get(String key) {
    return parameters.get(key);
  }

  public void set(String key, String value) {
    parameters.put(key, value);
  }
}

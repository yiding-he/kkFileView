package cn.keking.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.SneakyThrows;

import java.util.Map;

import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL;

/**
 * 使用 Jackson 序列化和反序列化
 */
public class Jackson {

  private static final ObjectMapper STRICT;                   // 带类型的序列化和反序列化

  private static final ObjectMapper LOOSE;                    // 不带类型的序列化和反序列化

  private static final ObjectMapper LOOSE_NON_NULL_PROP;      // 不带类型，且忽略 null 属性

  static {
    STRICT = new JsonMapper();
    STRICT.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    LOOSE = STRICT.copy();
    LOOSE_NON_NULL_PROP = STRICT.copy();

    STRICT.activateDefaultTyping(STRICT.getPolymorphicTypeValidator(), NON_FINAL);
    LOOSE_NON_NULL_PROP.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
  }

  /**
   * 将 Object 序列化为带类型的 JSON 字符串，只有 Jackson 自己能反序列化
   */
  @SneakyThrows
  public static String serialize(Object object) {
    return STRICT.writeValueAsString(object);
  }

  /**
   * 将 Object 序列化为不带类型的 JSON 字符串，任何 JSON 框架都能反序列化
   * 包含 null 属性
   */
  @SneakyThrows
  public static String serializeStandardJson(Object object) {
    return serializeStandardJson(object, false);
  }

  /**
   * 将 Object 序列化为不带类型的 JSON 字符串，任何 JSON 框架都能反序列化
   * 用户可以决定是否要忽略 null 属性
   */
  @SneakyThrows
  public static String serializeStandardJson(Object object, boolean ignoreNullProperty) {
    return (ignoreNullProperty ? LOOSE_NON_NULL_PROP : LOOSE).writeValueAsString(object);
  }

  /**
   * 将带类型的 JSON 字符串反序列化为对象
   */
  @SuppressWarnings("unchecked")
  @SneakyThrows
  public static <T> T deserialize(String serialized) {
    return (T) STRICT.readValue(serialized, Object.class);
  }

  /**
   * 将不带类型的 JSON 字符串反序列化为对象
   */
  @SneakyThrows
  public static <T> T deserializeStandardJson(String untypedJson, Class<T> type) {
    return LOOSE.reader().readValue(untypedJson, type);
  }

  /**
   * 将任何 Map 对象解析为 Bean 对象
   */
  @SneakyThrows
  public static <T> T deserializeMap(Map<String, Object> map, Class<T> type) {
    return LOOSE.convertValue(map, type);
  }

  /**
   * 将不带类型的 JSON 字符串反序列化为 TreeNode 对象
   */
  @SneakyThrows
  public static JsonNode deserializeNode(String untypedJson) {
    return LOOSE.readTree(untypedJson);
  }

  /**
   * 将任何对象转化为基于 {@code Map<String, Object>} 的类型，例如 org.bson.Document 或 com.alibaba.fastjson.JSONObject
   */
  public static <T extends Map<String, Object>> T convert(Object pojo, Class<T> dstType, boolean ignoreNullValue) {
    return (ignoreNullValue ? LOOSE_NON_NULL_PROP : LOOSE).convertValue(pojo, dstType);
  }

  /**
   * 生成带缩进的 JSON 字符串
   */
  @SneakyThrows
  public static String toIndentString(Object o) {
    return LOOSE.writer().with(SerializationFeature.INDENT_OUTPUT).writeValueAsString(o);
  }
}

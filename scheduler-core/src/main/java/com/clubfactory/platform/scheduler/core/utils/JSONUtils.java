package com.clubfactory.platform.scheduler.core.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * json utils
 */
public class JSONUtils {

  private static final Logger logger = LoggerFactory.getLogger(JSONUtils.class);

  /**
   * can use static singleton, inject: just make sure to reuse!
   */
  private static final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * init
   */
  private static final JSONUtils instance = new JSONUtils();


  private JSONUtils() {
    //Feature that determines whether encountering of unknown properties, false means not analyzer unknown properties
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setTimeZone(TimeZone.getDefault());
  }

  /**
   * json representation of object
   * @param object
   * @return object to json string
   */
  public static String toJson(Object object) {
    try{
      return JSONObject.toJSONString(object,false);
    } catch (Exception e) {
      logger.error("object to json exception!",e);
    }

    return null;
  }


  /**
   *
   * This method deserializes the specified Json into an object of the specified class. It is not
   * suitable to use if the specified class is a generic type since it will not have the generic
   * type information because of the Type Erasure feature of Java. Therefore, this method should not
   * be used if the desired type is a generic type. Note that this method works fine if the any of
   * the fields of the specified object are generics, just the object itself should not be a
   * generic type.
   *
   * @param json the string from which the object is to be deserialized
   * @param clazz the class of T
   * @return an object of type T from the string
   * classOfT
   */
  public static <T> T parseObject(String json, Class<T> clazz) {
    if (StringUtils.isEmpty(json)) {
      return null;
    }

    try {
      return JSONObject.parseObject(json, clazz);
    } catch (Exception e) {
      logger.error("parse object exception!",e);
    }
    return null;
  }


  /**
   * json to list
   *
   * @param json
   * @param clazz c
   * @param <T>
   * @return
   */
  public static <T> List<T> toList(String json, Class<T> clazz) {
    if (StringUtils.isEmpty(json)) {
      return new ArrayList<>();
    }
    try {
      return JSONArray.parseArray(json, clazz);
    } catch (Exception e) {
      logger.error("JSONArray.parseArray exception!",e);
    }

    return new ArrayList<>();
  }



  /**
   * check json object valid
   *
   * @param json
   * @return
   */
  public static boolean checkJsonVaild(String json) {

    if (StringUtils.isEmpty(json)) {
      return false;
    }

    try {
      objectMapper.readTree(json);
      return true;
    } catch (IOException e) {
      logger.error("check json object valid exception!",e);
    }

    return false;
  }


  /**
   * Method for finding a JSON Object field with specified name in this
   * node or its child nodes, and returning value it has.
   * If no matching field is found in this node or its descendants, returns null.
   *
   * @param fieldName Name of field to look for
   *
   * @return Value of first matching node found, if any; null if none
   */
  public static String findValue(JsonNode jsonNode, String fieldName) {
    JsonNode node = jsonNode.findValue(fieldName);

    if (node == null) {
      return null;
    }

    return node.toString();
  }


  /**
   * json to map
   *
   * {@link #toMap(String, Class, Class)}
   *
   * @param json
   * @return
   */
  public static Map<String, String> toMap(String json) {
    if (StringUtils.isEmpty(json)) {
      return null;
    }

    try {
      return JSONObject.parseObject(json, new TypeReference<HashMap<String, String>>(){});
    } catch (Exception e) {
      logger.error("json to map exception!",e);
    }

    return null;
  }

  /**
   *
   * json to map
   *
   * <pre>
   *         String jsonStr = "{\"id\":\"1001\",\"name\":\"Jobs\"}";
   *         Map<String,String> models = JSONUtils.toMap(jsonStr, String.class,String.class);
   * </pre>
   * @param json
   * @param classK
   * @param classV
   * @param <K>
   * @param <V>
   * @return
   */
  public static <K, V> Map<K, V> toMap(String json, Class<K> classK, Class<V> classV) {
    if (StringUtils.isEmpty(json)) {
      return null;
    }

    try {
      return JSONObject.parseObject(json, new TypeReference<HashMap<K, V>>() {});
    } catch (Exception e) {
      logger.error("json to map exception!",e);
    }

    return null;
  }

  /**
   * 对象装json字符串 <p>
   *
   * @return json string
   */
  public static String toJsonString(Object object) {
    try{
      return JSONObject.toJSONString(object,false);
    } catch (Exception e) {
      throw new RuntimeException("Json deserialization exception.", e);
    }
  }

  public static JSONObject parseObject(String text) {
    try{
      return JSONObject.parseObject(text);
    } catch (Exception e) {
      throw new RuntimeException("Json deserialization exception.", e);
    }
  }

  public static JSONArray parseArray(String text) {
    try{
      return JSONObject.parseArray(text);
    } catch (Exception e) {
      throw new RuntimeException("Json deserialization exception.", e);
    }
  }



  /**
   * json serializer
   */
  public static class JsonDataSerializer extends JsonSerializer<String> {

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      gen.writeRawValue(value);
    }

  }

  /**
   * json data deserializer
   */
  public static class JsonDataDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonNode node = p.getCodec().readTree(p);
      return node.toString();
    }

  }
}

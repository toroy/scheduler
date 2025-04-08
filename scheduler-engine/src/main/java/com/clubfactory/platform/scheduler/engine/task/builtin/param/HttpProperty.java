package com.clubfactory.platform.scheduler.engine.task.builtin.param;

import com.clubfactory.platform.scheduler.core.enums.HttpParametersType;

import java.util.Objects;

/**
 * @author xiejiajun
 */
public class HttpProperty {
  /**
   * key
   */
  private String prop;

  /**
   *  httpParametersType
   */
  private HttpParametersType httpParametersType;

  /**
   * value
   */
  private String value;

  public HttpProperty() {
  }

  public HttpProperty(String prop, HttpParametersType httpParametersType, String value) {
    this.prop = prop;
    this.httpParametersType = httpParametersType;
    this.value = value;
  }

  /**
   * getter method
   *
   * @return the prop
   * @see HttpProperty#prop
   */
  public String getProp() {
    return prop;
  }

  /**
   * setter method
   *
   * @param prop the prop to set
   * @see HttpProperty#prop
   */
  public void setProp(String prop) {
    this.prop = prop;
  }

  /**
   * getter method
   *
   * @return the value
   * @see HttpProperty#value
   */
  public String getValue() {
    return value;
  }

  /**
   * setter method
   *
   * @param value the value to set
   * @see HttpProperty#value
   */
  public void setValue(String value) {
    this.value = value;
  }

  public HttpParametersType getHttpParametersType() {
    return httpParametersType;
  }

  public void setHttpParametersType(HttpParametersType httpParametersType) {
    this.httpParametersType = httpParametersType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
        return true;
    }
    if (o == null || getClass() != o.getClass()) {
        return false;
    }
    HttpProperty property = (HttpProperty) o;
    return Objects.equals(prop, property.prop) &&
            Objects.equals(value, property.value);
  }


  @Override
  public int hashCode() {
    return Objects.hash(prop, value);
  }

  @Override
  public String toString() {
    return "HttpProperty{" +
            "prop='" + prop + '\'' +
            ", httpParametersType=" + httpParametersType +
            ", value='" + value + '\'' +
            '}';
  }


}

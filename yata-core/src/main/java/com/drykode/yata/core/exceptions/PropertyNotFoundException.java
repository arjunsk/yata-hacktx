package com.drykode.yata.core.exceptions;

public class PropertyNotFoundException extends RuntimeException {

  public PropertyNotFoundException(String propertyKey) {
    super("Property not found for " + propertyKey);
  }
}

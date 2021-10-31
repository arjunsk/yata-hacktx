package com.drykode.yata.core.support;

import com.drykode.yata.core.exceptions.PropertyNotFoundException;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.util.Properties;

/**
 * This is a similar utility to spring @Value for reading property.
 *
 * <p>This utility will read environment variable and application.properties. If env variable is
 * present then the value is prioritized.
 */
public class PropertyFetcher {

  private final Properties properties;

  /**
   * Read application.properties file.
   *
   * @param propertyPath Properties File path.
   */
  @SneakyThrows
  public PropertyFetcher(String propertyPath) {
    ClassPathResource resource = new ClassPathResource(propertyPath);
    properties = PropertiesLoaderUtils.loadProperties(resource);
  }

  /**
   * Fetch property value based on key. If value is present in environment variable, then it is
   * taken, else application.properties value is taken.
   *
   * @param key property key.
   * @return property value.
   */
  public String getPropertyValue(final String key) {
    String envValue = System.getProperty(key);
    String propertyValue = properties.getProperty(key);
    String value = (envValue != null) ? envValue : propertyValue;
    if (value == null) throw new PropertyNotFoundException(key);
    return value;
  }
}

package org.feeder.api.core.converter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.springframework.util.StringUtils;

@Converter
public class SetToStringConverter implements AttributeConverter<Set<String>, String> {

  @Override
  public String convertToDatabaseColumn(Set<String> attribute) {

    String dbData = null;

    if (Objects.nonNull(attribute)) {
      dbData = String.join(",", attribute);
    }

    return dbData;
  }

  @Override
  public Set<String> convertToEntityAttribute(String dbData) {

    Set<String> attribute = new HashSet<>();

    if (StringUtils.hasText(dbData)) {
      attribute = Arrays.stream(dbData.split(",")).collect(Collectors.toSet());
    }

    return attribute;
  }
}

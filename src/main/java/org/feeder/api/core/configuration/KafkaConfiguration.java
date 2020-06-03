package org.feeder.api.core.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;

@EnableKafka
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
@ConditionalOnProperty(value = "feeder.kafka.enabled", havingValue = "true")
public class KafkaConfiguration {

  @Autowired
  private KafkaProperties kafkaProperties;

  @Bean
  public KafkaTemplate<?, ?> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  @Bean
  public ProducerFactory<?, ?> producerFactory() {
    JsonSerializer<Object> jsonSerializer = new JsonSerializer<>(objectMapper());
    jsonSerializer.setAddTypeInfo(false);
    return new DefaultKafkaProducerFactory<>(
        kafkaProperties.buildProducerProperties(),
        null,
        jsonSerializer
    );
  }

  @Bean
  public ConsumerFactory<?, ?> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
      ConsumerFactory<Object, Object> consumerFactory,
      ConcurrentKafkaListenerContainerFactoryConfigurer configurer
  ) {

    ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    configurer.configure(factory, consumerFactory);
    factory.setMessageConverter( new StringJsonMessageConverter( objectMapper() ) );
    return factory;
  }

  private static ObjectMapper objectMapper() {
    return new Jackson2ObjectMapperBuilder()
        .modules(new JavaTimeModule(), new Jdk8Module())
        .serializationInclusion(JsonInclude.Include.NON_EMPTY)
        .featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .build();
  }
}

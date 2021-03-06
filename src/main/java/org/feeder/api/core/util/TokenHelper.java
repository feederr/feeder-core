package org.feeder.api.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TokenHelper {

  public static final String JTI_KEY = "jti";

  public static final String TOKEN_TYPE_KEY = "_t";

  public static final String USER_ID_KEY = "user_id";

  public static final String USER_NAME_KEY = "user_name";

  public static final String USER_ROLE_KEY = "role";

  public static final String CLIENT_ID_KEY = "client_id";

  private static final ObjectMapper mapper = new ObjectMapper();

  private static final TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
  };

  public enum TokenType {
    CLIENT, USER
  }

  public static Optional<String> extractClientId() {
    return getValue(CLIENT_ID_KEY)
        .map(Object::toString);
  }

  public static Optional<UUID> extractUserId() {
    return getValue(USER_ID_KEY)
        .map(Object::toString)
        .map(UUID::fromString);
  }

  public static boolean isClientToken() {
    return tokenOfType(TokenType.CLIENT);
  }

  public static boolean isUserToken() {
    return tokenOfType(TokenType.USER);
  }

  private static boolean tokenOfType(TokenType tokenType) {
    return getValue(TOKEN_TYPE_KEY)
        .map(Object::toString)
        .map(TokenType::valueOf)
        .filter(tokenType::equals)
        .isPresent();
  }

  public static String getClaim(Map<String, Object> map, String key) {
    return map.getOrDefault(key, "").toString();
  }

  public static Optional<Map<String, Object>> getClaims(String tokenValue) {
    try {
      Jwt token = JwtHelper.decode(tokenValue);
      return mapClaimsFromJwt(token);
    } catch (RuntimeException ex) {
      throw new InvalidTokenException("Token decoding failed", ex);
    }
  }

  @SuppressWarnings("ALL")
  private static Optional<Map<String, Object>> mapClaimsFromJwt(Jwt jwt) {

    Optional<Map<String, Object>> map = Optional.empty();

    String claims = jwt.getClaims();

    try {

      map = Optional.ofNullable(mapper.readValue(claims, typeRef));


    } catch (JsonProcessingException e) {
      log.warn("Invalid token claims");
    }

    return map;
  }

  @SuppressWarnings("ALL")
  private static Optional<Object> getValue(String name) {

    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (hasOAuth2Token(authentication)) {

      final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication
          .getDetails();

      return Optional.ofNullable((Map<String, Object>) details.getDecodedDetails())
          .map(decodedDetails -> decodedDetails.get(name));
    }

    return Optional.empty();
  }

  private static boolean hasOAuth2Token(Authentication authentication) {
    return authentication != null
        && authentication.getDetails() != null
        && authentication.getDetails() instanceof OAuth2AuthenticationDetails;
  }
}

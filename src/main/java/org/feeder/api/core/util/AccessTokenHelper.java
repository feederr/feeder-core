package org.feeder.api.core.util;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessTokenHelper {

  public static final String TOKEN_TYPE_KEY = "_t";

  public static final String USER_ID_KEY = "user_id";

  public static final String USER_NAME_KEY = "username";

  public enum AccessTokenType {
    CLIENT, USER
  }

  public static UUID extractUserId() {
    return getValue(USER_ID_KEY)
        .map(Object::toString)
        .map(UUID::fromString)
        .orElse(null);
  }

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

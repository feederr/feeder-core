package org.feeder.api.core.search;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("ALL")
public class GenericRsqlSpecification<T> implements Specification<T> {

  private String property;

  private ComparisonOperator operator;

  private List<String> arguments;

  @Override
  public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

    List<Object> args = castArguments(root);
    Object argument = args.get(0);
    query.distinct(true);

    switch (RsqlSearchOperation.getSimpleOperator(operator)) {

      case EQUAL: {
        if (argument instanceof String) {
          return builder
              .like(getPath(root).as(String.class), argument.toString().replace('*', '%'));
        } else if (argument == null) {
          return builder.isNull(getPath(root));
        } else {
          return builder.equal(getPath(root), argument);
        }
      }

      case NOT_EQUAL: {
        if (argument instanceof String) {
          return builder
              .notLike(getPath(root).as(String.class), argument.toString().replace('*', '%'));
        } else if (argument == null) {
          return builder.isNotNull(getPath(root));
        } else {
          return builder.notEqual(getPath(root), argument);
        }
      }

      case GREATER_THAN: {
        return builder.greaterThan(getPath(root).as(String.class), argument.toString());
      }

      case GREATER_THAN_OR_EQUAL: {
        return builder.greaterThanOrEqualTo(getPath(root), argument.toString());
      }

      case LESS_THAN: {
        return builder.lessThan(getPath(root), argument.toString());
      }

      case LESS_THAN_OR_EQUAL: {
        return builder.lessThanOrEqualTo(getPath(root), argument.toString());
      }

      case IN:
        return getPath(root).in(args);

      case NOT_IN:
        return builder.not(getPath(root).in(args));
    }

    return null;
  }

  private List<Object> castArguments(final Root<T> root) {

    Class<?> type = getPath(root).getJavaType();

    return arguments.stream().map(arg -> {

      if (type.equals(Integer.class)) {
        return Integer.parseInt(arg);
      } else if (type.equals(Long.class)) {
        return Long.parseLong(arg);
      } else if (type.equals(UUID.class)) {
        return UUID.fromString(arg);
      } else if (type.equals(LocalDateTime.class)) {
        return LocalDateTime.parse(arg);
      } else {
        return arg;
      }

    }).collect(Collectors.toList());
  }

  private Path getPath(final Root<T> root) {
    // next class citizen can be left-join to main class by "." symbol
    if (property.contains(".")) {
      String[] properties = property.split("\\.");
      From<?, ?> from = root;

      for (int i = 0; i < properties.length - 1; i++) {
        final String joinPropertyAlias = properties[i];

        boolean isPropertyJoined = from.getJoins().stream()
            .anyMatch(j -> j.getAttribute().getName().equals(joinPropertyAlias));

        if (!isPropertyJoined) {
          from = from.join(joinPropertyAlias, JoinType.LEFT);
        } else {
          Optional<? extends Join<?, ?>> joinFound = from.getJoins().stream()
              .filter(j -> j.getAttribute().getName().equals(joinPropertyAlias))
              .findFirst();

          if (joinFound.isPresent()) {
            from = joinFound.get();
          }
        }
      }
      return from.get(properties[properties.length - 1]);
    }

    return root.get(property);
  }
}

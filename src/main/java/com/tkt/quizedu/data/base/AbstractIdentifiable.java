package com.tkt.quizedu.data.base;

import java.io.Serial;
import java.util.Objects;

public abstract class AbstractIdentifiable<I> implements Identifiable<I> {
  @Serial private static final long serialVersionUID = -9068818681961708484L;

  @Override
  public final boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    var id = getId();

    if (id == null) {
      return false;
    }

    return o instanceof AbstractIdentifiable<?> other && Objects.equals(id, other.getId());
  }

  @Override
  public final int hashCode() {
    var id = getId();

    return id == null ? 0 : id.hashCode();
  }
}

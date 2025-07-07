package com.tkt.quizedu.data.base;

import java.io.Serializable;

public interface Identifiable<I> extends Serializable {

  I getId();
}

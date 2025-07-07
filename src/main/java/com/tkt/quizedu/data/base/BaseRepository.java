package com.tkt.quizedu.data.base;

import java.io.Serializable;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<E extends AbstractIdentifiable<I>, I extends Serializable>
    extends MongoRepository<E, I> {}

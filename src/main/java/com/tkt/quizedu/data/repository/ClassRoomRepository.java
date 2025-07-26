package com.tkt.quizedu.data.repository;

import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.ClassRoom;

import java.util.Optional;

@Repository
public interface ClassRoomRepository extends BaseRepository<ClassRoom, String> {

    Optional<ClassRoom> findByClassCode(String classCode);
    boolean existsByClassCode(String classCode);
}

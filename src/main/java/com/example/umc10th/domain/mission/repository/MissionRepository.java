package com.example.umc10th.domain.mission.repository;

import com.example.umc10th.domain.mission.entity.Mission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    /**
     * 도전 가능한 미션 목록 — id 내림차순, 단일 커서(lastMissionId).
     * Slice<>로 반환 → Spring Data가 size+1 가져와서 hasNext 자동 계산.
     */
    @Query("""
            SELECT m FROM Mission m
            JOIN FETCH m.store s
            WHERE s.dong.id = :dongId
              AND (:lastMissionId IS NULL OR m.id < :lastMissionId)
            ORDER BY m.id DESC
            """)
    Slice<Mission> findAvailableMissions(
            @Param("dongId") Long dongId,
            @Param("lastMissionId") Long lastMissionId,
            Pageable pageable
    );
}

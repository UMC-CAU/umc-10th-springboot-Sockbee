package com.example.umc10th.domain.mission.repository;

import com.example.umc10th.domain.mission.entity.Mission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    /**
     * 도전 가능한 미션 목록 조회 (커서 기반 페이징).
     * - 동(Dong) 기준으로 필터
     * - lastMissionId가 null이면 첫 페이지, 아니면 그보다 작은 id만 조회
     * - id 내림차순 정렬
     */
    @Query("""
            SELECT m FROM Mission m
            JOIN FETCH m.store s
            WHERE s.dong.id = :dongId
              AND (:lastMissionId IS NULL OR m.id < :lastMissionId)
            ORDER BY m.id DESC
            """)
    List<Mission> findAvailableMissions(
            @Param("dongId") Long dongId,
            @Param("lastMissionId") Long lastMissionId,
            Pageable pageable
    );
}

package com.example.umc10th.domain.user.repository;

import com.example.umc10th.domain.mission.enums.MissionStatus;
import com.example.umc10th.domain.user.entity.UserMission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

    /**
     * 해당 동에서 사용자가 "완료(COMPLETE)"한 미션 수.
     * 응답에는 (반환값 % 10) 적용해서 노출함.
     */
    @Query("""
            SELECT COUNT(um) FROM UserMission um
            WHERE um.user.id = :userId
              AND um.mission.store.dong.id = :dongId
              AND um.status = com.example.umc10th.domain.mission.enums.MissionStatus.COMPLETE
            """)
    int countCompletedByUserAndDong(@Param("userId") Long userId,
                                    @Param("dongId") Long dongId);

    /**
     * 내 미션 목록 조회 (커서 기반 페이징).
     * - userId, status 기준으로 필터
     * - createdAt + id 복합 커서 (createdAt 충돌 시 id로 tie-break)
     * - createdAt 내림차순, id 내림차순 정렬
     */
    @Query("""
            SELECT um FROM UserMission um
            JOIN FETCH um.mission m
            JOIN FETCH m.store s
            WHERE um.user.id = :userId
              AND um.status = :status
              AND (:lastCreatedAt IS NULL
                   OR um.createdAt < :lastCreatedAt
                   OR (um.createdAt = :lastCreatedAt AND um.id < :lastId))
            ORDER BY um.createdAt DESC, um.id DESC
            """)
    List<UserMission> findMyMissions(
            @Param("userId") Long userId,
            @Param("status") MissionStatus status,
            @Param("lastId") Long lastId,
            @Param("lastCreatedAt") LocalDateTime lastCreatedAt,
            Pageable pageable
    );
}

package ru.practicum.ewm.stat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.stat.model.Stat;
import ru.practicum.ewm.stat.model.StatClient;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<StatClient, Long> {

    @Query("SELECT new ru.practicum.ewm.stat.model.Stat(s.app, s.uri, COUNT (s.ip)) " +
            "FROM StatClient s WHERE s.timestamp > ?1 AND s.timestamp < ?2 " +
            "AND s.uri IN ?3 GROUP BY s.app, s.uri")
    List<Stat> getAllStat(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.ewm.stat.model.Stat(s.app, s.uri, COUNT (DISTINCT s.ip)) FROM " +
            "StatClient s WHERE s.timestamp > ?1 AND s.timestamp < ?2 " +
            "AND s.uri IN ?3 GROUP BY s.app, s.uri")
    List<Stat> getAllUniqueStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}

package com.vantage.bulls.dao;

import com.vantage.bulls.model.OptionChainRecordDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NiftyOptionChainRepository extends JpaRepository<OptionChainRecordDTO, Long> {

    // Retrieves the 94 records just inserted
    List<OptionChainRecordDTO> findByTimestamp(LocalDateTime timestamp);

    // Efficiently finds the previous batch's timestamp
    @Query("SELECT MAX(o.timestamp) FROM OptionChainRecordDTO o WHERE o.timestamp < :currentTs")
    Optional<LocalDateTime> findTopTimestampBefore(@Param("currentTs") LocalDateTime currentTs);
}

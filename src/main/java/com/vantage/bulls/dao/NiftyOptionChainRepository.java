package com.vantage.bulls.dao;

import com.vantage.bulls.model.OptionChainRecordDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NiftyOptionChainRepository extends JpaRepository<OptionChainRecordDTO, Long> {
}

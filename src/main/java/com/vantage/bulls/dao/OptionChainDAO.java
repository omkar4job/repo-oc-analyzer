package com.vantage.bulls.dao;

import com.vantage.bulls.dto.OptionChainResponse;

public interface OptionChainDAO {
    boolean saveOptionChain(OptionChainResponse optionChain);
}

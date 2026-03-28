package com.vantage.bulls.dao;

import java.time.LocalDateTime;

/**
 * A Java Record is a concise way to create data carriers.
 * It automatically generates constructor, getters, equals, and hashCode.
 */
public record OCBatchSavedEvent(LocalDateTime timestamp) {
}
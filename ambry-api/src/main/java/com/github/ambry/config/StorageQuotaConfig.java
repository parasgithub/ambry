/*
 * Copyright 2020 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.config;

import com.github.ambry.quota.QuotaMode;


/**
 * Config for Storage Quota service.
 */
public class StorageQuotaConfig {
  public static final String STORAGE_QUOTA_PREFIX = "storage.quota.";
  public static final String REFRESHER_POLLING_INTERVAL_MS = STORAGE_QUOTA_PREFIX + "refresher.polling.interval.ms";
  public static final String SOURCE_FACTORY = STORAGE_QUOTA_PREFIX + "source.factory";
  public static final String CONTAINER_STORAGE_QUOTA_IN_JSON = STORAGE_QUOTA_PREFIX + "container.storage.quota.in.json";
  public static final String SOURCE_POLLING_INTERVAL_MS = STORAGE_QUOTA_PREFIX + "source.polling.interval.ms";
  public static final String BACKUP_FILE_DIR = STORAGE_QUOTA_PREFIX + "backup.file.dir";
  public static final String MYSQL_MONTHLY_BASE_FETCH_OFFSET_SEC =
      STORAGE_QUOTA_PREFIX + "mysql.monthly.base.fetch.offset.sec";
  public static final String MYSQL_STORE_RETRY_BACKOFF_MS = STORAGE_QUOTA_PREFIX + "mysql.store.retry.backoff.ms";
  public static final String MYSQL_STORE_RETRY_MAX_COUNT = STORAGE_QUOTA_PREFIX + "mysql.store.retry.max.count";
  public static final String ENFORCER_MODE = STORAGE_QUOTA_PREFIX + "enforcer.mode";
  private static final String DEFAULT_VALUE_SOURCE_FACTORY =
      "com.github.ambry.quota.storage.JSONStringStorageQuotaSourceFactory";
  private static final String DEFAULT_VALUE_ENFORCE_MODE = QuotaMode.TRACKING.name();

  /**
   * The interval in milliseconds for refresher to refresh storage usage from its source.
   */
  @Config(REFRESHER_POLLING_INTERVAL_MS)
  @Default("30 * 60 * 1000") // 30 minutes
  public final int refresherPollingIntervalMs;

  @Config(SOURCE_FACTORY)
  @Default(DEFAULT_VALUE_SOURCE_FACTORY)
  public final String sourceFactory;

  //////////////// Config for JSONStringStorageQuotaSource ///////////////

  /**
   * A JSON string representing storage quota for all containers. eg:
   * {
   *   "101": {
   *     "1": 1024000000,
   *     "2": 258438456
   *   },
   *   "102": {
   *     "1": 10737418240
   *   }
   * }
   * The key of the top object is the acount id and the key of the inner object is the container id.
   * The value of the each container id is the storage quota in bytes for this container.
   *
   * If the targeted container doesn't have a storage quota in this JSON string, it's up to StorageQuotaEnforcer
   * to decide whether to allow uploads or not.
   */
  @Config(CONTAINER_STORAGE_QUOTA_IN_JSON)
  @Default("")
  public final String containerStorageQuotaInJson;

  /**
   * The interval in milliseconds for quota source to refresh each container's storage quota.
   */
  @Config(SOURCE_POLLING_INTERVAL_MS)
  @Default("30 * 60 * 1000")
  public final int sourcePollingIntervalMs;

  /**
   * The directory to store quota related backup files. If empty, then backup files will be disabled.
   */
  @Config(BACKUP_FILE_DIR)
  @Default("")
  public final String backupFileDir;

  /**
   * Duration in milliseconds to backoff if the mysql database query failed.
   */
  @Config(MYSQL_STORE_RETRY_BACKOFF_MS)
  @Default("10*60*1000")
  public final long mysqlStoreRetryBackoffMs;

  /**
   * Maximum retry times to execute a mysql database query.
   */
  @Config(MYSQL_STORE_RETRY_MAX_COUNT)
  @Default("1")
  public final int mysqlStoreRetryMaxCount;

  /**
   * Offset in seconds to fetch container usage monthly base.
   */
  @Config(MYSQL_MONTHLY_BASE_FETCH_OFFSET_SEC)
  @Default("60 * 60")
  public final long mysqlMonthlyBaseFetchOffsetSec;

  /**
   * The quota mode to set for enforcer. There are two values, "tracking" or "throttling"
   */
  @Config(ENFORCER_MODE)
  public final QuotaMode enforcerMode;

  /**
   * Constructor to create a {@link StorageQuotaConfig}.
   * @param verifiableProperties The {@link VerifiableProperties} that contains all the properties.
   */
  public StorageQuotaConfig(VerifiableProperties verifiableProperties) {
    refresherPollingIntervalMs =
        verifiableProperties.getIntInRange(REFRESHER_POLLING_INTERVAL_MS, 30 * 60 * 1000, 0, Integer.MAX_VALUE);
    sourceFactory = verifiableProperties.getString(SOURCE_FACTORY, DEFAULT_VALUE_SOURCE_FACTORY);
    containerStorageQuotaInJson = verifiableProperties.getString(CONTAINER_STORAGE_QUOTA_IN_JSON, "");
    sourcePollingIntervalMs =
        verifiableProperties.getIntInRange(SOURCE_POLLING_INTERVAL_MS, 30 * 60 * 1000, 0, Integer.MAX_VALUE);
    backupFileDir = verifiableProperties.getString(BACKUP_FILE_DIR, "");
    mysqlStoreRetryBackoffMs = verifiableProperties.getLong(MYSQL_STORE_RETRY_BACKOFF_MS, 10 * 60 * 1000);
    mysqlStoreRetryMaxCount = verifiableProperties.getInt(MYSQL_STORE_RETRY_MAX_COUNT, 1);
    mysqlMonthlyBaseFetchOffsetSec = verifiableProperties.getLong(MYSQL_MONTHLY_BASE_FETCH_OFFSET_SEC, 60 * 60);
    enforcerMode =
        QuotaMode.valueOf(verifiableProperties.getString(ENFORCER_MODE, DEFAULT_VALUE_ENFORCE_MODE).toUpperCase());
  }
}

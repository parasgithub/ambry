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
package com.github.ambry.quota.storage;

import com.github.ambry.account.Account;
import com.github.ambry.account.Container;
import com.github.ambry.quota.QuotaMode;
import com.github.ambry.rest.RestRequest;
import com.github.ambry.rest.RestUtils.InternalKeys;
import java.util.Map;


/**
 * {@link StorageQuotaEnforcer} enforces the traffic throttling based on the storage quota and the current storage usage.
 *
 * Each traffic that changes storage usage is targeted at a specific account and container. Enforcer enforces storage
 * quota on each container. Before evaluating any traffic, enforcer has to retrieve storage quota of each container from
 * {@link StorageQuotaSource} and current storage usage of each container from {@link StorageUsageRefresher}, by calling
 * {@link #initStorageQuota} and {@link #initStorageUsage} respectively.
 *
 * Container storage quota can be dynamic, it can be updated to increase or decrease the quota for specific containers.
 * To listen on these changes, Enforcer would return a {@link StorageQuotaSource.Listener}.
 *
 * Container storage usage is changing all the the time because of expired blobs and compacted deleted blobs. That's the
 * reason why relying on the incoming traffic won't give you a correct answer about the current storage usage. For instance,
 * if 1GB blob is uploaded to containerA and the TTL for this blob is one day. Then one day later, without any traffic
 * from client, the storage usage for containerA becomes 0. Since the storage usage from {@link StorageUsageRefresher} is
 * the source of the truth, enforcer has to listen on the changes for storage usage and replace the value in memory with
 * the value from {@link StorageUsageRefresher}.
 */
public interface StorageQuotaEnforcer {

  /**
   * Initialize the storage usage in {@link StorageQuotaEnforcer}.
   * @param usage The initial storage usage from {@link StorageUsageRefresher}.
   */
  void initStorageUsage(Map<String, Map<String, Long>> usage);

  /**
   * Initialize the storage quota in {@link StorageQuotaSource}.
   * @param quota The initial quota from {@link StorageQuotaSource}.
   */
  void initStorageQuota(Map<String, Map<String, Long>> quota);

  /**
   * Register listeners in {@link StorageQuotaSource} and {@link StorageUsageRefresher}.
   * @param storageQuotaSource The {@link StorageQuotaSource} to register listener.
   * @param storageUsageRefresher The {@link StorageUsageRefresher} to register listener.
   */
  void registerListeners(StorageQuotaSource storageQuotaSource, StorageUsageRefresher storageUsageRefresher);

  /**
   * Return true if the given {@link RestRequest} should be throttled. Since the {@link StorageQuotaEnforcer} decide to
   * throttle a request based on the account id and container id, the {@code restRequest} has to carry {@link Account}
   * and {@link Container} by header {@link InternalKeys#TARGET_ACCOUNT_KEY} and {@link InternalKeys#TARGET_CONTAINER_KEY}.
   * @param restRequest The {@link RestRequest} from client.
   * @return True if the given {@link RestRequest} should be throttled.
   */
  boolean shouldThrottle(RestRequest restRequest);

  /**
   * Change the {@link StorageQuotaEnforcer}'s mode to the given value. If the mode is {@link QuotaMode#TRACKING}, then {@link StorageQuotaEnforcer}
   * should never return true in {@link #shouldThrottle} method.
   * @param mode The new value for {@link QuotaMode}.
   */
  void setQuotaMode(QuotaMode mode);

  /**
   * Interface of callback method when the quota of certain account and container's quota is exceeded.
   */
  interface QuotaExceededCallback {

    /**
     * Method to call when the storage quota is exceeded.
     * @param quotaMode The current {@link QuotaMode} of Enforcer.
     * @param accountId The account id that exceeds the quota.
     * @param containerId The container id that exceeds the quota.
     * @param op The particular {@link QuotaOperation} that exceeds the quota.
     * @param quota The storage quota.
     * @param existingUsage The existing usage before the operation.
     * @param opSize The storage size of the operation.
     */
    void onQuotaExceeded(QuotaMode quotaMode, short accountId, short containerId, QuotaOperation op, long quota,
        long existingUsage, long opSize);
  }
}

/**
 * Copyright 2021 LinkedIn Corp. All rights reserved.
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
package com.github.ambry.quota;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Class that returns the overall throttling recommendation for all the quotas.
 */
public class ThrottlingRecommendation {
  private final boolean throttle;
  private final Map<QuotaName, Float> quotaUsagePercentage;
  private final int recommendedHttpStatus;
  private final Map<QuotaName, Double> requestCost;
  private final long retryAfterMs;

  /**
   * Constructor for {@link ThrottlingRecommendation}.
   * @param throttle flag indicating if request should be throttled.
   * @param quotaUsagePercentage A {@link Map} of {@link QuotaName} to usage percentage.
   * @param recommendedHttpStatus overall recommended http status.
   * @param requestCost A {@link Map} of cost for all {@link QuotaName}s for this request.
   * @param retryAfterMs time in ms after which request should be retried. -1 if request is not throttled.
   */
  public ThrottlingRecommendation(boolean throttle, Map<QuotaName, Float> quotaUsagePercentage,
      int recommendedHttpStatus, Map<QuotaName, Double> requestCost, long retryAfterMs) {
    this.throttle = throttle;
    this.quotaUsagePercentage = new HashMap<>(quotaUsagePercentage);
    this.recommendedHttpStatus = recommendedHttpStatus;
    this.requestCost = new HashMap<>(requestCost);
    this.retryAfterMs = retryAfterMs;
  }

  /**
   * @return true if recommendation is to throttle. false otherwise.
   */
  public boolean shouldThrottle() {
    return this.throttle;
  }

  /**
   * @return A {@link Map} of quota  name and estimation of percentage of quota in use when the recommendation was made.
   */
  public Map<QuotaName, Float> getQuotaUsagePercentage() {
    return Collections.unmodifiableMap(this.quotaUsagePercentage);
  }

  /**
   * @return http status recommended.
   */
  public int getRecommendedHttpStatus() {
    return this.recommendedHttpStatus;
  }

  /**
   * @return A {@link Map} of quota name and cost value for serving the request.
   */
  public Map<QuotaName, Double> getRequestCost() {
    return Collections.unmodifiableMap(this.requestCost);
  }

  /**
   * @return the time interval in milliseconds after the request can be retried.
   * If request is not throttled then returns 0.
   */
  public long getRetryAfterMs() {
    return this.retryAfterMs;
  }
}

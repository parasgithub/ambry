/*
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

import com.github.ambry.config.QuotaConfig;
import java.util.List;


/**
 * Factory to instantiate {@link AmbryQuotaManager} class.
 */
public class AmbryQuotaManagerFactory implements QuotaManagerFactory {
  private final QuotaManager quotaManager;

  /**
   * @param quotaConfig {@link QuotaConfig} object.
   * @param addedQuotaEnforcers {@link List} of {@link QuotaEnforcer}s to inject to {@link QuotaManager}. These will be
   *                                        those {@link QuotaEnforcer} classes that cannot be created by config.
   * @throws ReflectiveOperationException
   */
  public AmbryQuotaManagerFactory(QuotaConfig quotaConfig, List<QuotaEnforcer> addedQuotaEnforcers,
      ThrottlePolicy throttlePolicy) throws ReflectiveOperationException {
    quotaManager = new AmbryQuotaManager(quotaConfig, addedQuotaEnforcers, throttlePolicy);
  }

  @Override
  public QuotaManager getQuotaManager() {
    return quotaManager;
  }
}

/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.infinispan;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * @author Tobias Sarnowski
 */
final class Infinispan implements Initializable, Disposable {
    private static final Logger LOG = LoggerFactory.getLogger(Infinispan.class);

    private final Set<EmbeddedCacheManager> embeddedCacheManagers;

    @Inject
    public Infinispan(Set<EmbeddedCacheManager> embeddedCacheManagers) {
        this.embeddedCacheManagers = embeddedCacheManagers;
    }


    @Override
    public void initialize() throws LifecycleException {
        for (EmbeddedCacheManager embeddedCacheManager: embeddedCacheManagers) {
            LOG.info("Starting {}...", embeddedCacheManager);
            embeddedCacheManager.start();
        }
    }

    @Override
    public void dispose() throws LifecycleException {
        List<Exception> exceptions = Lists.newArrayList();
        for (EmbeddedCacheManager embeddedCacheManager: embeddedCacheManagers) {
            try {
                LOG.info("Stopping {}...", embeddedCacheManager);
                embeddedCacheManager.stop();
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        if (exceptions.size() > 0) {
            throw new LifecycleException(new InfinispanExceptions(exceptions));
        }
    }
}
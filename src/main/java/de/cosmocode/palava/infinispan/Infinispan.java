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

import org.infinispan.Cache;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * A service which manages lifecycles for multiple Infinispan {@link Cache}s
 * configured via {@link InfinispanModule}.
 * 
 * @author Tobias Sarnowski
 */
final class Infinispan implements Initializable, Disposable {
    
    private static final Logger LOG = LoggerFactory.getLogger(Infinispan.class);

    private final Set<EmbeddedCacheManager> managers;

    @Inject
    public Infinispan(Set<EmbeddedCacheManager> managers) {
        this.managers = managers;
    }

    @Override
    public void initialize() throws LifecycleException {
        for (EmbeddedCacheManager manager : managers) {
            LOG.info("Starting {}...", manager);
            manager.start();
        }
    }

    @Override
    public void dispose() throws LifecycleException {
        final List<Exception> exceptions = Lists.newArrayList();
        for (EmbeddedCacheManager manager : managers) {
            try {
                LOG.info("Stopping {}...", manager);
                manager.stop();
            /* CHECKSTYLE:OFF */
            } catch (RuntimeException e) {
            /* CHECKSTYLE:ON */
                exceptions.add(e);
            }
        }
        if (exceptions.size() > 0) {
            throw new LifecycleException(new InfinispanExceptions(exceptions));
        }
    }
        
}

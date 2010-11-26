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

import java.util.List;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;

/**
 * A service which manages lifecycles for multiple Infinispan {@link Cache}s
 * configured via {@link InfinispanModule}.
 * 
 * @author Tobias Sarnowski
 */
final class Infinispan implements Initializable, Disposable {
    
    private static final Logger LOG = LoggerFactory.getLogger(Infinispan.class);

    private final Set<CacheContainer> managers;

    @Inject
    Infinispan(Set<CacheContainer> managers) {
        this.managers = managers;
    }

    @Override
    public void initialize() throws LifecycleException {
        for (CacheContainer manager : managers) {
            LOG.info("Starting {}...", manager);
            manager.start();
        }
    }

    @Override
    public void dispose() throws LifecycleException {
        final List<Exception> exceptions = Lists.newArrayList();
        for (CacheContainer manager : managers) {
            try {
                LOG.info("Stopping {}...", manager);
                manager.stop();
            /* CHECKSTYLE:OFF */
            } catch (RuntimeException e) {
            /* CHECKSTYLE:ON */
                exceptions.add(e);
            }
        }
        if (!exceptions.isEmpty()) {
            throw new LifecycleException(new InfinispanExceptions(exceptions));
        }
    }
        
}

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

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import de.cosmocode.palava.core.lifecycle.LifecycleException;
import org.infinispan.Cache;
import org.infinispan.manager.CacheManager;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Tobias Sarnowski
 */
public final class InfinispanModule implements Module {
    private static final Logger LOG = LoggerFactory.getLogger(InfinispanModule.class);

    private final String infinispanConfigurationFile;


    public InfinispanModule(String infinispanConfigurationFile) {
        this.infinispanConfigurationFile = infinispanConfigurationFile;
    }


    @Override
    public void configure(Binder binder) {
        final EmbeddedCacheManager embeddedCacheManager;

        // load configuration
        try {
            embeddedCacheManager = new DefaultCacheManager(infinispanConfigurationFile);
        } catch (IOException e) {
            throw new LifecycleException(e);
        }

        // bind the cachemanager itself
        LOG.debug("Binding CacheManager '{}'", infinispanConfigurationFile);
        binder.bind(CacheManager.class).annotatedWith(Names.named(infinispanConfigurationFile)).toInstance(embeddedCacheManager);

        // bind the cachemanager for later retrieval
        Multibinder.newSetBinder(binder, EmbeddedCacheManager.class).addBinding().toInstance(embeddedCacheManager);


        for (String cacheName: embeddedCacheManager.getCacheNames()) {
            Cache cache = embeddedCacheManager.getCache(cacheName);

            // bind the cache
            LOG.debug("Binding named Cache '{}'", cacheName);
            binder.bind(Cache.class).annotatedWith(Names.named(cacheName)).toInstance(cache);
        }
    }
}
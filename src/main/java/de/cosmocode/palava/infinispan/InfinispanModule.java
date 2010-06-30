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

import com.google.common.base.Preconditions;
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
 * Binds {@link Cache}s dynamically by reading the given config file.
 * 
 * @author Tobias Sarnowski
 */
public final class InfinispanModule implements Module {
    
    private static final Logger LOG = LoggerFactory.getLogger(InfinispanModule.class);

    private final String configFile;

    public InfinispanModule(String configFile) {
        this.configFile = Preconditions.checkNotNull(configFile, "ConfigFile");
    }

    @Override
    public void configure(Binder binder) {
        final EmbeddedCacheManager manager;

        try {
            manager = new DefaultCacheManager(configFile);
        } catch (IOException e) {
            throw new LifecycleException(e);
        }

        // bind the cachemanager itself
        LOG.debug("Binding CacheManager '{}'", configFile);
        binder.bind(CacheManager.class).annotatedWith(Names.named(configFile)).toInstance(manager);

        // bind the cachemanager for later retrieval
        Multibinder.newSetBinder(binder, EmbeddedCacheManager.class).addBinding().toInstance(manager);

        for (String name : manager.getCacheNames()) {
            final Cache<?, ?> cache = manager.getCache(name);
            LOG.debug("Binding named Cache '{}'", name);
            binder.bind(Cache.class).annotatedWith(Names.named(name)).toInstance(cache);
        }
    }
    
}

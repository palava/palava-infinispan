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
import org.infinispan.Cache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.manager.CacheContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Binds {@link org.infinispan.Cache}s dynamically by reading the given config file.
 *
 * @author Tobias Sarnowski
 */
public final class InfinispanRemoteModule implements Module {

    private static final Logger LOG = LoggerFactory.getLogger(InfinispanRemoteModule.class);

    private final Properties properties;
    private final String[] cacheNames;

    /**
     * Atm no support for named/annotated CacheContainer bindings.
     *
     * For more informations about properties see:
     * http://docs.jboss.org/infinispan/4.1/apidocs/org/infinispan/client/hotrod/RemoteCacheManager.html
     *
     * @param configuration client configuration
     * @param cacheNames which named caches to bind
     */
    public InfinispanRemoteModule(Properties configuration, String[] cacheNames) {
        this.cacheNames = Preconditions.checkNotNull(cacheNames, "CacheNames");
        this.properties = Preconditions.checkNotNull(configuration, "Properties");
    }

    @Override
    public void configure(Binder binder) {
        final CacheContainer manager = new RemoteCacheManager(properties);

        // bind the cachemanager itself
        LOG.debug("Binding RemoteCacheManager {}", manager);
        binder.bind(CacheContainer.class).toInstance(manager);

        // bind the cachemanager for later retrieval
        Multibinder.newSetBinder(binder, CacheContainer.class).addBinding().toInstance(manager);

        for (String name : cacheNames) {
            final Cache<?, ?> cache = manager.getCache(name);
            LOG.debug("Binding named Cache '{}'", name);
            binder.bind(Cache.class).annotatedWith(Names.named(name)).toInstance(cache);
        }

        // lifecycle service
        binder.bind(Infinispan.class).asEagerSingleton();
    }
    
}

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

import java.io.File;
import java.io.IOException;

import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;
import org.infinispan.manager.DefaultCacheManager;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;

/**
 * Configurable {@link CacheContainer}.
 *
 * @since 1.1
 * @author Willi Schoenborn
 */
final class ConfigurableCacheContainer implements CacheContainer, Initializable, Disposable {

    private final CacheContainer container;
    
    @Inject
    public ConfigurableCacheContainer(@Named(InfinispanConfig.CONFIG) File config) throws IOException {
        this.container = new DefaultCacheManager(Preconditions.checkNotNull(config, "Config").getAbsolutePath());
    }

    @Override
    public void initialize() throws LifecycleException {
        start();
    }
    
    @Override
    public void start() {
        container.start();
    }

    @Override
    public void stop() {
        container.stop();
    }

    @Override
    public <K, V> Cache<K, V> getCache() {
        return container.getCache();
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        return container.getCache(cacheName);
    }
    
    @Override
    public void dispose() throws LifecycleException {
        stop();
    }

}

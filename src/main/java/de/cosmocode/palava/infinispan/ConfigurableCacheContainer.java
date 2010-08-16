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

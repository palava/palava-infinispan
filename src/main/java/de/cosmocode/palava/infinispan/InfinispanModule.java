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
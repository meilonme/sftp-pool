package me.meilon.jsftp.core;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.*;


/**
 * Provides the default implementation of {@link EvictionPolicy} used by the
 * pools. Objects will be evicted if the following conditions are met:
 * <ul>
 * <li>the object has been idle longer than
 *     {@link GenericObjectPool#getMinEvictableIdleTimeMillis()} /
 *     {@link GenericKeyedObjectPool#getMinEvictableIdleTimeMillis()}</li>
 * <li>there are more than {@link GenericObjectPool#getMinIdle()} /
 *     {@link GenericKeyedObjectPoolConfig#getMinIdlePerKey()} idle objects in
 *     the pool and the object has been idle for longer than
 *     {@link GenericObjectPool#getSoftMinEvictableIdleTimeMillis()} /
 *     {@link GenericKeyedObjectPool#getSoftMinEvictableIdleTimeMillis()}
 * </ul>
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 *
 * @since 2.0
 */
public class SftpEvictionPolicy implements EvictionPolicy<SftpConnect> {

    @Override
    public boolean evict(EvictionConfig config, PooledObject<SftpConnect> underTest, int idleCount) {

        return (config.getIdleSoftEvictTime() < underTest.getIdleTimeMillis() &&
                config.getMinIdle() < idleCount) ||
                config.getIdleEvictTime() < underTest.getIdleTimeMillis();
    }
}

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
 *     {@link GenericObjectPool#getSoftMinEvictableIdleDuration()} /
 *     {@link GenericKeyedObjectPool#getSoftMinEvictableIdleDuration()}
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

        return (config.getIdleSoftEvictDuration().compareTo(underTest.getIdleDuration()) < 0 &&
                config.getMinIdle() < idleCount) ||
                config.getIdleEvictDuration().compareTo(underTest.getIdleDuration()) < 0;
    }

}

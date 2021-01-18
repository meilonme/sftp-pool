package me.meilon.jsftp.core;

import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author meilon
 */
public class SftpPooledObject extends DefaultPooledObject<SftpConnect> {
    /**
     * Creates a new instance that wraps the provided object so that the pool can
     * track the state of the pooled object.
     *
     * @param object The object to wrap
     */
    public SftpPooledObject(SftpConnect object) {
        super(object);
    }

}

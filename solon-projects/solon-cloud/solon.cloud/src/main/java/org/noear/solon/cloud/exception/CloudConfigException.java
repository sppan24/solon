package org.noear.solon.cloud.exception;

/**
 * @author noear
 * @since 1.10
 */
public class CloudConfigException extends CloudException {
    public CloudConfigException(String message){
        super(message);
    }

    public CloudConfigException(Throwable cause){
        super(cause);
    }
}

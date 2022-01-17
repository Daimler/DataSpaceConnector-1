package org.eclipse.dataspaceconnector.transaction.tx;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
    String key();
}

package org.eclipse.dataspaceconnector.transaction.tx;

import org.junit.Test;

public class TxTest {

    @Test
    public void myTest() {
        TransactionManager transactionManager = null;

        TransactionContext context = transactionManager.beginTransaction();

        context.commit();
    }
}

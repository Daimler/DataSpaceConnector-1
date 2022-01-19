package org.eclipse.dataspaceconnector.api.data.client.exceptions;

public class UnsupportedCriterionOperatorException extends DataManagementApiClientException {
    private final String operator;

    public UnsupportedCriterionOperatorException(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }
}

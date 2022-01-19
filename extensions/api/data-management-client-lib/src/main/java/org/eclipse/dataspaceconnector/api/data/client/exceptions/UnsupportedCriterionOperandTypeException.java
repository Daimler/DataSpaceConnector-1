package org.eclipse.dataspaceconnector.api.data.client.exceptions;

public class UnsupportedCriterionOperandTypeException extends DataManagementApiClientException {
    private final String unsupportedType;

    public UnsupportedCriterionOperandTypeException(String unsupportedType) {
        this.unsupportedType = unsupportedType;
    }

    public String getUnsupportedType() {
        return unsupportedType;
    }
}

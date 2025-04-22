package ca.bc.gov.brmb.common.model;

public enum ValidationStatus {

	RED(3),YELLOW(2),GREEN(1);

    private final int intValue;
    
    private ValidationStatus(int intValue) {
        this.intValue = intValue;
    }

    public int intValue() {
        return intValue;
    }
}

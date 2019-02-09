package org.contractor.core.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Getter
@Setter
@ToString
public class ResultDataSet {

    private Map<Long, String> errorMap;

    private Set<AuditedObject> resultObjects;

    public ResultDataSet() {
        this.resultObjects = new HashSet<>();
        errorMap = new HashMap<>();

    }
    public void addError(Long errorCode, String errorMsg) {
        this.errorMap.put(errorCode, errorMsg);
    }
    public boolean hasErrors() {
        return !errorMap.isEmpty();
    }

    public Set<AuditedObject> getResults() {
        return resultObjects;
    }

    public void addAllErrors(Map<Long, String> errors) {
        this.getErrorMap().putAll(errors);
    }

    public void clearErrorMap() {
        this.errorMap.clear();
    }


}

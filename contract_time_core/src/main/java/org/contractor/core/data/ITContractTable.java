package org.contractor.core.data;


import lombok.*;

@Data
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper=false)
public class ITContractTable extends AuditedObject {
    private String contractTableHeader;
    private String contractMoneyMarkDownTable;
}

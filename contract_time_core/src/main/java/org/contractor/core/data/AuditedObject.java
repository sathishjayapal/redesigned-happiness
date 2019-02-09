package org.contractor.core.data;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
public class AuditedObject {
    private @Getter @Setter String createUserID;
    private @Getter @Setter Date createDate;

}

package spring.empty.dto;

import java.util.Date;
import java.util.UUID;

/**
 * @author Oleg Cherednik
 * @since 05.07.2016
 */
public class Scenario {
    private long id;
    private long projectId;
    private String name;
    private String notes;

    private Date createdOn;
    private User createdBy;
    private Date modifiedOn;
    private User modifiedBy;

    private String modelName;
    private String modelStepName;
    private UUID modelUUID;
    private UUID modelStepUUID;

    private Boolean disabled = false;
    private Boolean isDeleted = false;
    private Boolean isHidden = false;

    private int analysisCount;
}

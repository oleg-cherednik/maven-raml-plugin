package spring.empty.dto;

import java.util.Date;
import java.util.UUID;

/**
 * @author Oleg Cherednik
 * @since 05.07.2016
 */
public class Analysis {
    private long id;
    private long projectId;

    private Scenario scenario;

    private String name;
    private String notes;
    private String status;
    private Date createdOn;
    private User createdBy;
    private Date modifiedOn;
    private User modifiedBy;
    private Date startedOn;
    private User executor;
    private Date executedOn;

    private String strategyField;

    private String modelName;
    private String modelStepName;
    private UUID modelUUID;
    private UUID modelStepUUID;

    private String numberOfStrategies;
    private boolean disabled;
    private boolean isDeleted;
}

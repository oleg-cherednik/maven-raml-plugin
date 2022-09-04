package spring.empty.dto;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * @author Oleg Cherednik
 * @since 05.07.2016
 */
public class Project {
    private long id;
    private String name;
    private User createdBy;
    private User modifiedBy;
    private Date createdOn;
    private Date modifiedOn;
    private String notes;
    private String status;
    private UUID modelStepUUID;
    private Boolean isEditable;
    private Boolean isDeleted;
    private Set<User> owners;

    private String modelUUID;
    private String modelName;
    private String modelStepName;
    private Long scenarioCount;
    private Long analysesCount;
    private Integer completedScenarioCount;
    private Integer completedAnalysesCount;
}

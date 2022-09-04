package spring.general.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author Oleg Cherednik
 * @since 05.07.2016
 */
@JsonIgnoreProperties({ "executor", "executedOn" })
public class Analysis {
    /** {@type String} */
    private List<String> ids;

    private long id;
    /** {@link Project#id} */
    private long projectId;

    private Scenario scenario;

    /**
     * Name of the analysis
     * <p>
     * {@example The Analysis}
     */
    private String name;
    private String notes;
    private String status;
    private Date createdOn;
    @JsonIgnore(false)
    private User createdBy;
    @JsonIgnore(false)
    private Date modifiedOn;
    @JsonIgnore
    private User modifiedBy;
    @JsonIgnore
    private Date startedOn;
    // @JsonIgnore
    private User executor;
    // @JsonIgnore
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

package spring.general.dto;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * @author Oleg Cherednik
 * @since 05.07.2016
 */
public class Project {
    /**
     * Unique project id
     * {@name Unique project id}
     * {@example 666}
     */
    private long id;
    /**
     * Name of the project
     * <p>
     * {@example The Project}
     */
    private String name;
    private User createdBy;
    private Date createdOn;
    private String status;
    private UUID stepUuid;
    private Boolean isEditable;
    private Boolean isDeleted;
    private Set<User> owners;

    /** {@link Model#uuid} */
    private String modelUuid;
    private String modelName;
    private String modelStepName;

    private Long scenarioCount;
    private Long analysesCount;
    private Integer completedScenarioCount;
    private Integer completedAnalysesCount;
}

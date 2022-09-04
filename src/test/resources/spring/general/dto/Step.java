package spring.general.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Step {

    private User createdBy;
    private User modifiedBy;
    private Date createdOn;
    private Date modifiedOn;

    private String name;
    private UUID uuid;
    private String uri;
    private Map<String, String> metadata;

    private Model model;
    private List<StepParameter> parameters;
}

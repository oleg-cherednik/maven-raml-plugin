package spring.general.dto;

import java.util.Date;
import java.util.Map;

public class StepParameter {
    private Long id;
    private String createdBy;
    private String modifiedBy;
    private Date createdOn;
    private Date modifiedOn;

    private String name;

    private Boolean isInput;
    private Map<String, String> metadata;
}

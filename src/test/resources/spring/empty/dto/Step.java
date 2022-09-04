package spring.empty.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    private Map<String, String> metadata = new HashMap<>();

    private Model model;
    private List<StepParameter> parameters = new ArrayList<>();
}

package spring.general.dto;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author Oleg Cherednik
 * @since 05.07.2016
 */
public class Model {
    /**
     * Unique model uuid
     * {@example d3aa88e2-c754-41e0-8ba6-4198a34aa0a2}
     */
    private UUID uuid;
    /**
     * Name of the model
     * {@example The Grand Model}
     */
    private String name;
    /**
     * Unique model uri this model available on
     * {@example www.model.org/d3aa88e2-c754-41e0-8ba6-4198a34aa0a2}
     */
    private String uri;

    /** User this model was created by */
    private User createdBy;
    /** Date this model was created on */
    private Date createdOn;
    /** Model's metadata */
    private Map<String, String> metadata;
}

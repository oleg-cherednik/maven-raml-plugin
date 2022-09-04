package spring.empty.dto;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author Oleg Cherednik
 * @since 05.07.2016
 */
public class Model {
    private User createdBy;
    private User modifiedBy;
    private Date createdOn;
    private Date modifiedOn;

    private String name;
    private UUID uuid;
    private String uri;
    private Map<String, String> metadata;
}

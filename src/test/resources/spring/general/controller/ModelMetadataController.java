package spring.general.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * REST end point for model item metadata operations
 */
@RestController("model/{modelUuid}/metadata")
public class ModelMetadataController {
    /**
     * Retrieve metadata for given model {@code modelUuid}
     *
     * @param modelUuid model uuid
     * @return not {@code null} map with metadata
     */
    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public Map<String, String> getModelMetadata(@PathVariable String modelUuid) {
        return null;
    }

    /**
     * Add metadata for given model {@code modelUuid}. Optionally {@code comment} can be specified.
     *
     * @param modelUuid model uuid
     * @param metadata  metadata
     */
    @RequestMapping(method = POST)
    public void addModelMetadata(@PathVariable String modelUuid, @RequestBody Map<String, String> metadata) {
    }

    /**
     * Delete existed metadata specified in {@code metadata} for given model {@code modelUuid}. Optionally {@code
     * reason} can be specified.
     *
     * @param modelUuid model uuid
     * @param metadata  metadata
     * @param reason    optional reason
     */
    @RequestMapping(method = DELETE)
    public void deleteModelMetadata(@PathVariable String modelUuid, @RequestBody List<String> metadata,
            @RequestParam(required = false) String reason) {
    }

    /**
     * Update metadata for given model {@code modelUuid} with new {@code metadata} values data. Optionally {@code
     * comment} can be specified.
     *
     * @param modelUuid model uuid
     * @return current metadata for given {@code modelUuid}
     */
    @RequestMapping(method = PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, String> updateModelMetadata(@PathVariable String modelUuid, @RequestBody Map<String, String> metadata) {
        return null;
    }
}

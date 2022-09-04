package spring.general.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.general.exception.GeneralException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * REST end point for model step metadata operations
 */
@RestController("step/{stepUuid}/metadata")
public class StepMetadataController {
    /**
     * Retrieve metadata for given model step {@code stepUuid}
     *
     * @param stepUuid model step uuid
     * @return not {@code null} map with metadata
     */
    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public Map<String, String> getModelStepMetadata(@PathVariable UUID stepUuid) {
        return null;
    }

    /**
     * Add metadata for given model step {@code stepUuid}. Optionally {@code comment} can be specified.
     *
     * @param stepUuid model step uuid
     * @param metadata metadata
     * @throws GeneralException in case of any error
     */
    @RequestMapping(method = POST)
    public void addModelStepMetadata(@PathVariable UUID stepUuid, @RequestBody Map<String, String> metadata) throws GeneralException {
    }

    /**
     * Delete existed metadata specified in {@code metadata} for given model step {@code stepUuid}. Optionally {@code
     * reason} can be specified.
     *
     * @param stepUuid model step uuid
     * @param metadata metadata
     * @param reason   optional reason
     * @throws GeneralException in case of any error
     */
    @RequestMapping(method = DELETE)
    public void deleteModelStepMetadata(@PathVariable UUID stepUuid, @RequestBody List<String> metadata,
            @RequestParam(required = false) String reason) throws GeneralException {
    }

    /**
     * Update metadata for given model step {@code stepUuid} with new {@code metadata} values data. Optionally {@code
     * comment} can be specified.
     *
     * @param stepUuid model step uuid
     * @param metadata list of new data for model step parameters (if it's <b>empty</b>, then all metadata for given
     *                 model step will be removed)
     * @return current metadata for given {@code stepUuid}
     * @throws GeneralException in case of any error
     */
    @RequestMapping(method = PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, String> updateModelStepMetadata(@PathVariable UUID stepUuid, @RequestBody Map<String, String> metadata) throws
            GeneralException {
        return null;
    }
}

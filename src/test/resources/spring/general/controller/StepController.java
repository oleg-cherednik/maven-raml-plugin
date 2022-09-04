package spring.general.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import spring.general.dto.ModelStatus;
import spring.general.dto.StepParameter;
import spring.general.dto.Step;
import spring.general.exception.GeneralException;
import spring.general.exception.ModelItemNotFoundException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * REST end point for model step operations
 */
@RestController("step")
public class StepController {

    @RequestMapping(value = "find", method = POST, produces = APPLICATION_JSON_VALUE)
    public List<Step> getSteps(@RequestBody Map<String, Collection<String>> metadata) throws GeneralException {
        return null;
    }

    /**
     * Retrieve all model steps with {@link ModelStatus#PUBLISHED} status.
     *
     * @return not {@code null} list of available models {@link Step}
     * @throws GeneralException in case of any problem
     */
    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Step> getSteps() throws GeneralException {
        return null;
    }

    /**
     * Retrieve {@link Step} with given {@code stepUuid}
     *
     * @param stepUuid model step uuid
     * @return {@link Step} with given {@code stepUuid} or {@code null} if model step with given {@code stepUuid}
     * is not found
     * @throws GeneralException in case of any problem
     */
    @RequestMapping(value = "{stepUuid}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Step getStep(@PathVariable UUID stepUuid) throws GeneralException {
        return null;
    }

    /**
     * Create new {@link Step} with given {@code modelStep} data. Optionally {@code comment} can be specified.
     *
     * @param step model step data
     * @return created {@link Step}
     * @throws GeneralException in case of any problem
     */
    @RequestMapping(method = POST, produces = APPLICATION_JSON_VALUE)
    public Step createStep(@RequestBody Step step) throws GeneralException {
        return null;
    }

    /**
     * Delete existed {@link Step} with given {@code stepUuid}. Optionally {@code reason} can be specified.
     *
     * @param stepUuid model step uuid
     * @param reason   optional reason
     */
    @RequestMapping(value = "{stepUuid}", method = RequestMethod.DELETE)
    public void deleteStep(@PathVariable UUID stepUuid, @RequestParam(required = false) String reason) {
    }

    /**
     * Update {@link Step} with new {@code modelStep} data.
     *
     * @param stepUuid model step uuid (it must be the same with uuid in the body)
     * @param step     new data for model step
     * @return updated {@link Step}
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "{stepUuid}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public Step updateStep(@PathVariable UUID stepUuid, @RequestBody Step step)
            throws GeneralException {
        return null;
    }

    /**
     * Update {@link Step} status with new {@code modelStep} status data.
     *
     * @param stepUuid model step uuid (it must be the same with uuid in the body)
     * @param status   new status for model step
     * @return updated {@link Step}
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "{stepUuid}/status/{status}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public Step updateStepStatus(@PathVariable UUID stepUuid, @PathVariable String status)
            throws GeneralException {
        return null;
    }

    /**
     * Retrieves content of the given model step {@code stepUuid}.
     *
     * @param stepUuid model step uuid
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "{stepUuid}/readonly", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Step> getReadOnlyStep(@PathVariable UUID stepUuid)
            throws GeneralException {
        return null;
    }

    @ExceptionHandler(ModelItemNotFoundException.class)
    public ResponseEntity<ModelItemNotFoundException> handleModelItemNotFound(ModelItemNotFoundException e) {
        return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
    }

    /**
     * @return {@link ResponseEntity} object with created model step {@link Step} with metadata and without
     * parameters
     * <ul>
     * <li>{@link HttpStatus#CREATED} - new model step was successfuly created</li>
     * <li>{@link HttpStatus#NOT_FOUND} - if model step with given {@code stepUuid} is not found</li>
     * <li>{@link HttpStatus#BAD_REQUEST} - if given model step is not <t>Excel</t> type</li>
     * <li>{@link HttpStatus#INTERNAL_SERVER_ERROR} - in case of error in app part or problems in converter</li>
     * <li><i>header.location</i> - url for retrieve created model step</li>
     * </ul>
     */
    @RequestMapping(value = "{stepUuid}/convert/js", method = POST)
    public ResponseEntity<Step> convertStepExcelToJavaScript(@PathVariable UUID stepUuid)
            throws GeneralException, IOException, URISyntaxException {
        return null;
    }

    /**
     * Convert given Excel document to the list of {@link StepParameter} and optionally (if {@code save} is {@code true}) save it (given
     * parameters replace all existed parameters for given model step). Saving is possible only if parameters don't have any errors.
     *
     * @param file     Excel document
     * @param stepUuid model step uuid
     * @param save     if {@code true}, then save results to database (optionally, default is {@code false})
     * @return not {@code null} list of {@link StepParameter} objects
     * @throws GeneralException
     */
    @ResponseBody
    @RequestMapping(value = "{stepUuid}/upload", method = POST, produces = APPLICATION_JSON_VALUE)
    public List<StepParameter> createStepParametersFromExcel(@RequestBody MultipartFile file, @PathVariable UUID stepUuid,
            @RequestParam(required = false) boolean save) throws GeneralException {
        return null;
    }

    /**
     * Retrieves content of the given model step {@code stepUuid}.
     *
     * @param stepUuid model step uuid
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "{stepUuid}/inspect/sheets", method = GET)
    public Object inspectStepGetSheets(@PathVariable UUID stepUuid) throws GeneralException {
        return null;
    }

    /**
     * Retrieves content of the given model step {@code stepUuid}.
     *
     * @param stepUuid model step uuid
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "{stepUuid}/inspect/cells", method = POST)
    public Object inspectStepGetCells(@PathVariable UUID stepUuid, @RequestBody String name) throws GeneralException {
        return null;
    }
}

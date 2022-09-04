package spring.general.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.general.dto.Model;
import spring.general.dto.Step;
import spring.general.exception.GeneralException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * REST end point for model operations
 */
@RestController("model")
public class ModelController {
    /**
     * Retrieve all available models
     *
     * @return not {@code null} list of available models {@link Model}
     * @throws GeneralException in case of any problem
     */
    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Model> getModels() throws GeneralException {
        return null;
    }

    /**
     * Retrieve all available models
     *
     * @return not {@code null} list of available models {@link Model}
     * @throws GeneralException in case of any problem
     */
    @RequestMapping(value = "find", method = POST, produces = APPLICATION_JSON_VALUE)
    public List<Model> getModelsFilter(@RequestBody Map<String, Collection<String>> metadata) throws GeneralException {
        return null;
    }

    /**
     * Retrieve {@link Model} with given {@code modelUuid}
     *
     * @param modelUuid model uuid
     * @return {@link Model} with given {@code modelUuid} or {@code null} if model with given {@code modelUuid} is not
     * found
     * @throws GeneralException in case of any problem
     */
    @RequestMapping(value = "{modelUuid}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Model getModel(@PathVariable String modelUuid) throws GeneralException {
        return null;
    }

    /**
     * Create new {@link Model} with given {@code model} data. Optionally {@code comment} can be specified. This time
     * it uses only {@link Model#name} and {@link Model#metadata}. All other model's parts should be created using other
     * services.
     *
     * @param model model data
     * @return created {@link Model}
     * @throws GeneralException in case of any problem
     */
    @RequestMapping(method = POST, produces = APPLICATION_JSON_VALUE)
    public Model createModel(@RequestBody Model model, @RequestParam(required = false) String comment) {
        return null;
    }

    /**
     * Update {@link Model} with new {@code model} data. Optionally {@code comment} can be specified.
     *
     * @param model model with updated parameters
     * @return updated {@link Model}
     */
    @RequestMapping(value = "{modelUuid}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public Model updateModel(@RequestBody Model model) {
        return null;
    }

    /**
     * Delete existed {@link Model} with given {@code modelUuid}. Optionally {@code reason} can be specified.
     *
     * @param modelUuid model uuid
     * @param reason    optional reason
     */
    @RequestMapping(value = "{modelUuid}", method = DELETE)
    public void deleteModel(@PathVariable String modelUuid, @RequestParam(required = false) String reason) {
    }

    /**
     * Retrieve all available model steps for given {@code modelUuid}.
     *
     * @param modelUuid model uuid
     * @return not {@code null} list of available models {@link Step}
     * @throws GeneralException in case of any problem
     */
    @RequestMapping(value = "{modelUuid}/step", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Step> getModelSteps(@PathVariable String modelUuid) throws GeneralException {
        return null;
    }
}

package spring.empty.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.empty.dto.Model;
import spring.empty.dto.Step;
import spring.empty.exception.GeneralException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController("/model")
public class ModelController {
    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Model> getModels() throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/find", method = POST, produces = APPLICATION_JSON_VALUE)
    public List<Model> getModelsFilter(@RequestBody Map<String, Collection<String>> metadata) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{modelUuid}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Model getModel(@PathVariable String modelUuid) throws GeneralException {
        return null;
    }

    @RequestMapping(method = POST, produces = APPLICATION_JSON_VALUE)
    public Model createModel(@RequestBody Model model) {
        return null;
    }

    @RequestMapping(value = "/{modelUuid}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public Model updateModel(@RequestBody Model model) {
        return null;
    }

    @RequestMapping(value = "/{modelUuid}", method = DELETE)
    public void deleteModel(@PathVariable String modelUuid, @RequestParam(required = false) String reason) {
    }

    @RequestMapping(value = "/{modelUuid}/step", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Step> getModelSteps(@PathVariable String modelUuid) throws GeneralException {
        return null;
    }
}

package spring.empty.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.empty.exception.GeneralException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController("/step/{stepUuid}/metadata")
public class StepMetadataController {
    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public Map<String, String> getModelStepMetadata(@PathVariable UUID stepUuid) {
        return null;
    }

    @RequestMapping(method = POST)
    public void addModelStepMetadata(@PathVariable UUID stepUuid, @RequestBody Map<String, String> metadata) throws GeneralException {
    }

    @RequestMapping(method = DELETE)
    public void deleteModelStepMetadata(@PathVariable UUID stepUuid, @RequestBody List<String> metadata,
            @RequestParam(required = false) String reason) throws GeneralException {
    }

    @RequestMapping(method = PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, String> updateModelStepMetadata(@PathVariable UUID stepUuid, @RequestBody Map<String, String> metadata) throws
            GeneralException {
        return null;
    }
}

package spring.empty.controller;

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

@RestController("/model/{modelUuid}/metadata")
public class ModelMetadataController {
    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public Map<String, String> getModelMetadata(@PathVariable String modelUuid) {
        return null;
    }

    @RequestMapping(method = POST)
    public void addModelMetadata(@PathVariable String modelUuid, @RequestBody Map<String, String> metadata) {
    }

    @RequestMapping(method = DELETE)
    public void deleteModelMetadata(@PathVariable String modelUuid, @RequestBody List<String> metadata,
            @RequestParam(required = false) String reason) {
    }

    @RequestMapping(method = PUT, produces = APPLICATION_JSON_VALUE)
    public Map<String, String> updateModelMetadata(@PathVariable String modelUuid, @RequestBody Map<String, String> metadata) {
        return null;
    }
}

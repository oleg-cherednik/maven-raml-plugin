package spring.empty.controller;

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
import spring.empty.dto.Step;
import spring.empty.dto.StepParameter;
import spring.empty.exception.GeneralException;
import spring.empty.exception.ModelItemNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController("/step")
public class StepController {
    @RequestMapping(value = "/find", method = POST, produces = APPLICATION_JSON_VALUE)
    public List<Step> getSteps(@RequestBody Map<String, Collection<String>> metadata) throws GeneralException {
        return null;
    }

    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Step> getSteps() throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{stepUuid}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Step getStep(@PathVariable UUID stepUuid) throws GeneralException {
        return null;
    }

    @RequestMapping(method = POST, produces = APPLICATION_JSON_VALUE)
    public Step createlStep(@RequestBody Step step) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{stepUuid}", method = RequestMethod.DELETE)
    public void deleteStep(@PathVariable UUID stepUuid, @RequestParam(required = false) String reason) {
    }

    @RequestMapping(value = "/{stepUuid}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public Step updateStep(@PathVariable UUID stepUuid, @RequestBody Step step) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{stepUuid}/status/{status}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public Step updateStepStatus(@PathVariable UUID stepUuid, @PathVariable String status) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{stepUuid}/readonly", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Step> getReadOnlyStep(@PathVariable UUID stepUuid) throws GeneralException {
        return null;
    }

    @ExceptionHandler(ModelItemNotFoundException.class)
    public ResponseEntity<ModelItemNotFoundException> handleModelItemNotFound(ModelItemNotFoundException e) {
        return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/{stepUuid}/convert/js", method = POST)
    public ResponseEntity<Step> convertStepExcelToJavaScript(@PathVariable UUID stepUuid) throws GeneralException {
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/{stepUuid}/upload", method = POST, produces = APPLICATION_JSON_VALUE)
    public List<StepParameter> createStepParametersFromExcel(@RequestBody MultipartFile file, @PathVariable UUID stepUuid,
            @RequestParam(required = false) boolean save) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{stepUuid}/inspect/sheets", method = GET)
    public Object inspectStepGetSheets(@PathVariable UUID stepUuid) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{stepUuid}/inspect/cells", method = POST)
    public Object inspectStepGetCells(@PathVariable UUID stepUuid, @RequestBody String name) throws GeneralException {
        return null;
    }
}

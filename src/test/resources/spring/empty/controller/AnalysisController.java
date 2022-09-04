package spring.empty.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.empty.dto.Analysis;
import spring.empty.dto.TornadoPlot;
import spring.empty.exception.GeneralException;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController("/project/{projectId}/analysis")
public class AnalysisController {
    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Analysis> getAnalysesByProjectId(@PathVariable long projectId) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/status/{status}", method = RequestMethod.GET, produces = APPLICATION_JSON_VALUE)
    public List<Analysis> getAnalysesByProjectIdAndStatus(@PathVariable long projectId, @PathVariable String status) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/tag", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Analysis> getAnalysesByTagName(@PathVariable long projectId, @RequestParam String tagName) throws GeneralException {
        return null;
    }

    @RequestMapping(method = POST, produces = APPLICATION_JSON_VALUE)
    public Analysis createAnalysis(@PathVariable long projectId, @RequestBody Analysis analysisDTO) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/", method = POST, produces = APPLICATION_JSON_VALUE)
    public Analysis cloneAnalysis(@PathVariable long projectId, @RequestBody Analysis analysisDTO) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{analysisId}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public Analysis updateAnalysis(@PathVariable long projectId, @RequestBody Analysis analysisDTO) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{analysisId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Analysis getAnalysis(@PathVariable long projectId, @PathVariable long analysisId) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{analysisId}/tornado", method = GET, produces = APPLICATION_JSON_VALUE)
    public TornadoPlot generateTornadoPlot(@PathVariable long projectId, @PathVariable long analysisId,
            @RequestParam(required = false) String field) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{analysisId}", method = DELETE)
    public void analysisDelete(@PathVariable long projectId, @PathVariable long analysisId) throws GeneralException {
    }

    @RequestMapping(value = "/deleted", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Analysis> getDeletedAnalysis(@PathVariable long projectId) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{analysisId}/rename", method = PUT, produces = APPLICATION_JSON_VALUE)
    public void renameAnalysis(@PathVariable long projectId, @PathVariable long analysisId, @RequestBody String name)
            throws GeneralException {
    }
}

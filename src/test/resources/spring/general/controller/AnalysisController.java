package spring.general.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.general.dto.Analysis;
import spring.general.dto.Project;
import spring.general.dto.TornadoPlot;
import spring.general.exception.GeneralException;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * REST end point for analysis operations
 *
 * {@url {@link PathUtils#ANALYSIS}}
 * {@name Project Analysis}
 *
 * @author Oleg Cherednik
 * @since 17.12.2016
 */
@RestController("project/{projectId}/analysis")
public class AnalysisController {
    /**
     * Retrieve all analyses related to the given {@code projectId}.
     *
     * @param projectId {@link Project#id}
     * @return {@status 201} list of {@type arr}{@link Analysis} not {@code null} list of all analyses
     * @throws GeneralException in case of any error
     * @localRoot
     */
    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Analysis> getAnalysesByProjectId(@PathVariable long projectId) throws GeneralException {
        return null;
    }

    /**
     * Retrieve all analyses for given {@code projectId} with given {@code status}.
     *
     * @param projectId project id
     * @return list of {@link Analysis}
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "status/{status}", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Analysis> getAnalysesByProjectIdAndStatus(@PathVariable long projectId, @PathVariable String status) throws GeneralException {
        return null;
    }

    /**
     * Retrieve all analyses for given {@code projectId} with given {@code tagName}.
     *
     * @param projectId project id
     * @param tagName   tag name
     * @return list of {@link Analysis}
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "tag", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Analysis> getAnalysesByTagName(@PathVariable long projectId, @RequestParam String tagName) throws GeneralException {
        return null;
    }

    /**
     * Add analysis to project with given {@code projectId}.
     *
     * @param projectId   project id
     * @param analysisDTO analysis
     * @return {@link Analysis} object
     * @throws GeneralException in case of any error
     */
    @RequestMapping(method = POST, produces = APPLICATION_JSON_VALUE)
    public Analysis createAnalysis(@PathVariable long projectId, @RequestBody Analysis analysisDTO) throws GeneralException {
        return null;
    }

    /**
     * Clone analysis to the given project {@code projectId} based on {@code analysis} data.
     *
     * @param projectId   project id
     * @param analysisDTO analysis
     * @return {@link Analysis} object
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "copy", method = POST, produces = APPLICATION_JSON_VALUE)
    public Analysis cloneAnalysis(@PathVariable long projectId, @RequestBody Analysis analysisDTO) throws GeneralException {
        return null;
    }

    /**
     * Update analysis.
     *
     * @param analysisDTO analysis
     * @return {@link Analysis} object
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "{analysisId}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public Analysis updateAnalysis(@PathVariable long projectId, @RequestBody Analysis analysisDTO) throws GeneralException {
        return null;
    }

    /**
     * Get analysis with given {@code analysisId}.
     *
     * @param analysisId analysis id
     * @return {@link Analysis} object
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "{analysisId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Analysis getAnalysis(@PathVariable long projectId, @PathVariable long analysisId) throws GeneralException {
        return null;
    }

    /**
     * Returns tornado plot for given analysis {@code analysisId}. Optionally {@code field} can be specified.
     *
     * @param analysisId analysis id (required) {@name Analysis Identification} {@default 666} {@example 777}
     * @param field      field name (optional) {@default 666} {@example 777}
     * @return the analysis info
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "{analysisId}/tornado", method = GET, produces = APPLICATION_JSON_VALUE)
    public TornadoPlot generateTornadoPlot(@PathVariable long projectId, @PathVariable long analysisId,
            @RequestParam(required = false) String field) throws GeneralException {
        return null;
    }

    /**
     * Delete analysis with given {@code analysisId} for given {@code projectId}.
     *
     * @param projectId  project id
     * @param analysisId analysis id
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "{analysisId}", method = DELETE)
    public void analysisDelete(@PathVariable long projectId, @PathVariable long analysisId) throws GeneralException {
    }

    /**
     * Retrieves list of deleted analysis for given {@code projectId}.
     *
     * @param projectId project id
     * @return list of {@link Analysis} that was deleted
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "deleted", method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Analysis> getDeletedAnalysis(@PathVariable long projectId) throws GeneralException {
        return null;
    }

    /**
     * Rename given analysis {@code analysisId} for given {@code projectId}. {@code name} is specified in the body.
     *
     * @param projectId  project id
     * @param analysisId analysis id
     * @param name       new analysis name
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "{analysisId}/rename", method = PUT, produces = APPLICATION_JSON_VALUE)
    public void renameAnalysis(@PathVariable long projectId, @PathVariable long analysisId, @RequestBody String name)
            throws GeneralException {
    }
}

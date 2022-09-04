package spring.general.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.general.dto.Project;
import spring.general.exception.GeneralException;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_ATOM_XML_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * REST end point for project operations
 */
@RestController("project")
public class ProjectController {

    /**
     * Retrieve all available projects.
     *
     * @return {@status 200}{@type arr}{@link Project} not {@code null} list of available projects
     * @throws GeneralException in case of any error
     */
    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Project> getProjects() throws GeneralException {
        return null;
    }

    /**
     * Retrieve project with given {@code projectId}
     *
     * @param projectId project id
     * @return {@link Project} object
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "{projectId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Project getProject(@PathVariable long projectId) throws GeneralException {
        return null;
    }

    /**
     * Retrieve details for given {@code projectId}.
     *
     * @param projectId project id
     * @return {@link Project} object
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "{projectId}/details", method = GET, produces = APPLICATION_JSON_VALUE)
    public Project getProjectDetails(@PathVariable long projectId) throws GeneralException {
        return null;
    }

    /**
     * Update project with given {@code projectId} with new given {@code project}.
     *
     * @param projectId project id
     * @param project   new project
     * @return updated project in {@link Project}
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "{projectId}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public Project updateProject(@PathVariable long projectId, @RequestBody Project project) throws GeneralException {
        return null;
    }

    /**
     * Create project using given {@code project}.
     *
     * @param project project data
     * @return created {@link Project}
     * @throws GeneralException in case of any error
     */
    @RequestMapping(method = POST, consumes = APPLICATION_ATOM_XML_VALUE, produces = APPLICATION_XML_VALUE)
    public Project createProject(@RequestBody Project project) throws GeneralException {
        return null;
    }

    /**
     * Copy project {@code project}.
     *
     * @param project project
     * @return {@link Project}
     * @throws GeneralException
     * @throws CloneNotSupportedException
     */
    @RequestMapping(value = "copy", method = POST, produces = APPLICATION_JSON_VALUE)
    public Project copyProject(@RequestBody Project project) throws GeneralException, CloneNotSupportedException {
        return null;
    }

    /**
     * Delete project with given {@code projectId}.
     *
     * @param projectId project id
     */
    @RequestMapping(value = "{projectId}", method = DELETE, produces = APPLICATION_JSON_VALUE)
    public void deleteProject(@PathVariable long projectId) {
    }

    /**
     * Retrieve deleted projects
     *
     * @return list of {@link Project}
     * @throws GeneralException in case of any error
     */
    @RequestMapping(value = "deleted", method = GET, consumes = APPLICATION_XML_VALUE, produces = APPLICATION_JSON_VALUE)
    public List<Project> getDeletedProjects() throws GeneralException {
        return null;
    }

    /**
     * Change status to {@code status} for given project {@code projectId}.
     *
     * @param projectId project id
     * @param status    new status
     * @return updated project in {@link Project}
     * @throws GeneralException the exception
     */
    @RequestMapping(value = "{projectId}/status/{status}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public Project changeProjectStatus(@PathVariable long projectId, @PathVariable String status) throws GeneralException {
        return null;
    }
}

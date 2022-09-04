package spring.empty.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.empty.dto.Project;
import spring.empty.exception.GeneralException;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_ATOM_XML_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController("/project")
public class ProjectController {
    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public List<Project> getProjects() throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{projectId}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Project getProject(@PathVariable long projectId) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{projectId}/details", method = GET, produces = APPLICATION_JSON_VALUE)
    public Project getProjectDetails(@PathVariable long projectId) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{projectId}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public Project updateProject(@PathVariable long projectId, @RequestBody Project project) throws GeneralException {
        return null;
    }

    @RequestMapping(method = POST, consumes = APPLICATION_ATOM_XML_VALUE, produces = APPLICATION_XML_VALUE)
    public Project createProject(@RequestBody Project project) throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/copy", method = POST, produces = APPLICATION_JSON_VALUE)
    public Project copyProject(@RequestBody Project project) throws GeneralException, CloneNotSupportedException {
        return null;
    }

    @RequestMapping(value = "/{projectId}", method = DELETE, produces = APPLICATION_JSON_VALUE)
    public void deleteProject(@PathVariable long projectId) {
    }

    @RequestMapping(value = "/deleted", method = GET, consumes = APPLICATION_XML_VALUE, produces = APPLICATION_JSON_VALUE)
    public List<Project> getDeletedProjects() throws GeneralException {
        return null;
    }

    @RequestMapping(value = "/{projectId}/status/{status}", method = PUT, produces = APPLICATION_JSON_VALUE)
    public Project changeProjectStatus(@PathVariable long projectId, @PathVariable String status) throws GeneralException {
        return null;
    }
}

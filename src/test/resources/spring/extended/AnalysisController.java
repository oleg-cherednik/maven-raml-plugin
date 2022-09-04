package spring.empty.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController("/analysis")
public class AnalysisController {

    @RequestMapping(value = "/standard", method = GET)
    public void requestMappingGet() {
    }

    @RequestMapping(value = "/standard", method = POST)
    public void requestMappingPost() {
    }

    @RequestMapping(value = "/standard", method = PUT)
    public void requestMappingPut() {
    }

    @RequestMapping(value = "/standard", method = DELETE)
    public void requestMappingDelete() {
    }

    @RequestMapping(value = "/standard", method = PATCH)
    public void requestMappingPatch() {
    }

    @GetMapping(value = "/extended")
    public void getMapping() {
    }

    @PostMapping(value = "/extended")
    public void postMapping() {
    }

    @PutMapping(value = "/standard")
    public void putMapping() {
    }

    @DeleteMapping(value = "/standard")
    public void deleteMapping() {
    }

    @PatchMapping(value = "/standard")
    public void patchMapping() {
    }
}

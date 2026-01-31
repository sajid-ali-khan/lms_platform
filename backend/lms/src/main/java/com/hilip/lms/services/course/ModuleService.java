package com.hilip.lms.services.course;

import com.hilip.lms.exceptions.ResourceNotFoundException;
import com.hilip.lms.models.Course;
import com.hilip.lms.models.Module;
import com.hilip.lms.repositories.CourseRepository;
import com.hilip.lms.repositories.ModuleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class ModuleService {
    private final CourseRepository courseRepository;
    private final ModuleRepository moduleRepository;

    public void addModuleToCourse(String courseId) {
        Course course = courseRepository.findById(UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        var module = new Module();
        // Use count query instead of loading all modules to get size
        int sequenceOrder = moduleRepository.countByCourseId(UUID.fromString(courseId)) + 1;
        module.setTitle("Module " + sequenceOrder);
        module.setSequenceOrder(sequenceOrder);
        module.setIsPublished(true);
        module.setCourse(course);

        moduleRepository.save(module);
        log.debug("Module added to course: {}", courseId);
    }

    public Map<String, String> getModulesByCourseId(String courseId) {
        // Use direct query instead of loading course and navigating to modules
        var modules = moduleRepository.findAllByCourseIdOrderBySequenceOrder(UUID.fromString(courseId));

        var response = new HashMap<String, String>();
        for (var module : modules) {
            response.put(module.getId().toString(), module.getTitle());
        }
        return response;
    }

    public Module getModuleById(String moduleId) {
        return moduleRepository.findById(UUID.fromString(moduleId))
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));
    }
}

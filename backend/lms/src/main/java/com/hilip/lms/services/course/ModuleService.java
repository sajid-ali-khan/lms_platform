package com.hilip.lms.services.course;

import com.hilip.lms.dtos.course.modules.ModuleResponse;
import com.hilip.lms.exceptions.ResourceNotFoundException;
import com.hilip.lms.models.Course;
import com.hilip.lms.models.Module;
import com.hilip.lms.repositories.CourseRepository;
import com.hilip.lms.repositories.ModuleRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

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
        int sequenceOrder = moduleRepository.countByCourseId(UUID.fromString(courseId)) + 1;
        module.setTitle("Module " + sequenceOrder);
        module.setSequenceOrder(sequenceOrder);
        module.setIsPublished(true);
        module.setCourse(course);

        moduleRepository.save(module);
        log.debug("Module added to course: {}", courseId);
    }

    public List<ModuleResponse> getModulesByCourseId(String courseId) {
        var modules = moduleRepository.findAllByCourseIdOrderBySequenceOrder(UUID.fromString(courseId));

        List<ModuleResponse> moduleResponses = new ArrayList<>();
        for (Module module : modules) {
            Map<String, String> lessonsMap = new HashMap<>();
            module.getLessons().forEach(lesson ->
                    lessonsMap.put(lesson.getId().toString(), lesson.getTitle())
            );
            ModuleResponse moduleResponse = new ModuleResponse(
                    module.getId().toString(),
                    module.getTitle(),
                    lessonsMap
            );
            moduleResponses.add(moduleResponse);
        }
        return moduleResponses;
    }

}

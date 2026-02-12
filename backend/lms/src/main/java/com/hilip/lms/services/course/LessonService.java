package com.hilip.lms.services.course;

import com.hilip.lms.dtos.course.lessons.LessonResponse;
import com.hilip.lms.dtos.course.lessons.UpdateLessonRequest;
import com.hilip.lms.exceptions.ResourceNotFoundException;
import com.hilip.lms.helper.AutoMapper;
import com.hilip.lms.models.Lesson;
import com.hilip.lms.models.Module;
import com.hilip.lms.repositories.LessonRepository;
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
public class LessonService {
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final AutoMapper autoMapper;

    public void addLessonToModule(String moduleId) {
        Module module = moduleRepository.findById(UUID.fromString(moduleId))
                .orElseThrow(() -> new ResourceNotFoundException("Module not found"));

        var lesson = new Lesson();
        int sequenceOrder = lessonRepository.countByModuleId(UUID.fromString(moduleId)) + 1;
        lesson.setTitle("Lesson " + sequenceOrder);
        lesson.setSequenceOrder(sequenceOrder);
        lesson.setIsPublished(true);
        lesson.setModule(module);

        lessonRepository.save(lesson);
        log.debug("Lesson added to module: {}", moduleId);
    }

    public void updateLesson(String lessonId, UpdateLessonRequest request) {
        Lesson lesson = lessonRepository.findById(UUID.fromString(lessonId))
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));

        lesson.setTitle(request.title());
        lesson.setContent(request.content());
        lesson.setType(request.type());
        if (request.resourceUrl() != null) lesson.setResourceUrl(request.resourceUrl());
        if (request.isPublished() != null) lesson.setIsPublished(request.isPublished());
        lessonRepository.save(lesson);
        log.debug("Lesson updated: {}", lessonId);
    }

    public Map<String, LessonResponse> getLessonsByModuleId(String moduleId) {
        var lessons = lessonRepository.findAllByModuleIdOrderBySequenceOrder(UUID.fromString(moduleId));

        var response = new HashMap<String, LessonResponse>();
        for (var lesson : lessons) {
            var lessonResponse = autoMapper.mapLessonToLessonResponse(lesson);
            response.put(lesson.getId().toString(), lessonResponse);
        }
        return response;
    }

    public Lesson getLessonById(String lessonId) {
        return lessonRepository.findById(UUID.fromString(lessonId))
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
    }
}

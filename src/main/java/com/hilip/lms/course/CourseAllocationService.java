package com.hilip.lms.course;

import com.hilip.lms.course.dto.AllocateCourseRequest;
import com.hilip.lms.course.dto.CourseAllocationResponse;
import com.hilip.lms.organization.orgunit.OrgUnit;
import com.hilip.lms.organization.orgunit.OrgUnitRepository;
import com.hilip.lms.shared.exceptions.DataAlreadyExistsException;
import com.hilip.lms.shared.exceptions.ResourceNotFoundException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class CourseAllocationService {
    private final CourseAllocationRepository courseAllocationRepository;
    private final CourseRepository courseRepository;
    private final OrgUnitRepository orgUnitRepository;

    public CourseAllocationResponse allocateCourse(String courseId, AllocateCourseRequest request) {
        Course course = courseRepository.findById(UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        OrgUnit orgUnit = orgUnitRepository.findById(UUID.fromString(request.orgUnitId()))
                .orElseThrow(() -> new ResourceNotFoundException("Org unit not found with id: " + request.orgUnitId()));

        if (!course.getTenant().getId().equals(orgUnit.getTenant().getId())) {
            throw new IllegalArgumentException("Course and org unit belong to different tenants");
        }

        if (courseAllocationRepository.existsByCourseIdAndOrgUnitId(course.getId(), orgUnit.getId())) {
            throw new DataAlreadyExistsException("Course is already allocated to this org unit");
        }

        CourseAllocation allocation = new CourseAllocation();
        allocation.setCourse(course);
        allocation.setOrgUnit(orgUnit);
        allocation.setMandatory(request.isMandatory());

        allocation = courseAllocationRepository.save(allocation);
        log.info("Allocated course {} to org unit {}", courseId, request.orgUnitId());

        return mapToResponse(allocation);
    }

    public List<CourseAllocationResponse> getCourseAllocations(String courseId) {
        courseRepository.findById(UUID.fromString(courseId))
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        return courseAllocationRepository.findAllByCourseId(UUID.fromString(courseId))
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void removeAllocation(String courseId, String orgUnitId) {
        UUID cid = UUID.fromString(courseId);
        UUID ouid = UUID.fromString(orgUnitId);

        if (!courseAllocationRepository.existsByCourseIdAndOrgUnitId(cid, ouid)) {
            throw new ResourceNotFoundException("Course is not allocated to this org unit");
        }

        courseAllocationRepository.deleteByCourseIdAndOrgUnitId(cid, ouid);
        log.info("Removed course {} allocation from org unit {}", courseId, orgUnitId);
    }

    private CourseAllocationResponse mapToResponse(CourseAllocation ca) {
        return new CourseAllocationResponse(
                ca.getId().toString(),
                ca.getOrgUnit().getId().toString(),
                ca.getOrgUnit().getName(),
                buildOrgUnitPath(ca.getOrgUnit()),
                ca.isMandatory()
        );
    }

    private String buildOrgUnitPath(OrgUnit orgUnit) {
        List<String> parts = new ArrayList<>();
        OrgUnit current = orgUnit;
        while (current != null) {
            parts.add(current.getName());
            current = current.getParentUnit();
        }
        Collections.reverse(parts);
        return String.join(" / ", parts);
    }
}

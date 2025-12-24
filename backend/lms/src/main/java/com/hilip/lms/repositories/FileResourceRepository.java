package com.hilip.lms.repositories;

import com.hilip.lms.models.FileResource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileResourceRepository extends JpaRepository<FileResource, UUID> {
}

package com.hilip.lms.shared.fileresource;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileResourceRepository extends JpaRepository<FileResource, UUID> {
}

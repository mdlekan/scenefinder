package com.mikelekan.scenefinder.repository;

import com.mikelekan.scenefinder.model.LocationNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LocationNoteRepository extends JpaRepository<LocationNote, Long>
{
    List<LocationNote> findByLocationIdAndIsCurrentTrue(Long locationId);
}

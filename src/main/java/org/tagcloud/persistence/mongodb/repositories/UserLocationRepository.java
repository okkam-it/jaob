package org.tagcloud.persistence.mongodb.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.tagcloud.persistence.mongodb.model.Location;
import org.tagcloud.persistence.mongodb.model.UserLocation;

public interface UserLocationRepository extends PagingAndSortingRepository<UserLocation, Long> {

}

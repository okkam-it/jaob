package org.tagcloud.persistence.mongodb.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.tagcloud.persistence.mongodb.model.UserDevice;

public interface UserDeviceRepository extends PagingAndSortingRepository<UserDevice, Long> {
	
}

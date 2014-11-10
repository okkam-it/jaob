package org.tagcloud.persistence.mongodb.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.tagcloud.persistence.mongodb.model.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

}

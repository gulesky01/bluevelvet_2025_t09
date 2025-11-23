package com.musicstore.bluevelvet.infrastructure.repository;

import com.musicstore.bluevelvet.infrastructure.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

}
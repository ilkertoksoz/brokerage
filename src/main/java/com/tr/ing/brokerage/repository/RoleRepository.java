package com.tr.ing.brokerage.repository;

import com.tr.ing.brokerage.entity.Role;
import com.tr.ing.brokerage.enums.RoleUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByRoleUser(RoleUser roleUserName);
}

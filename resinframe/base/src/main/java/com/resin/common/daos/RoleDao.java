package com.resin.common.daos;

import com.resin.common.rbac.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 *  角色 DAO
 * 
 */
public interface RoleDao extends PagingAndSortingRepository<Role, Long> {

}
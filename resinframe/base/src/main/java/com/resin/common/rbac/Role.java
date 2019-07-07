package com.resin.common.rbac;

import com.resin.beans.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 *  角色
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    private String name;

    /**
     *  角色描述
     */
    private String comment;

}

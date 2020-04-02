package com.foxconn.iisd.rcadsvc.domain.auth;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/*
 *
 * @author JasonLai
 * @date 2019/3/12 上午10:16
 */
@Entity
@Table(name="permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 32, unique = true, nullable = false)
    private String code;

    @Column(length = 32, unique = true, nullable = false)
    private String name;

    @Column(length = 32)
    private String parentId;

    private int func_id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getFunc_id() {
        return func_id;
    }

    public void setFunc_id(int func_id) {
        this.func_id = func_id;
    }
}

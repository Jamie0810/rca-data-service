package com.foxconn.iisd.rcadsvc.domain;


import javax.persistence.*;
import java.util.Date;

/*
 *
 * @author Keeny
 * @date 2019/5/24 上午10:15
 */
@Entity
@Table(name="user_page_log")
public class PageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "func_name")
    private String funcName;


    @Column(name = "create_user")
    private String createUser;

    @Column(name = "create_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }


    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}

package com.foxconn.iisd.rcadsvc.util.menu;


/*
 *
 * @author Keeny
 * @date 2019/5/24 上午10:15
 */

import java.util.List;

public class MenuAuth {

    private Integer id;

    private String key;


    private Integer authority;

    private String authBit ;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getAuthority() {
        return authority;
    }

    public void setAuthority(Integer authority) {
        this.authority = authority;
    }

    public String getAuthBit() {
        return authBit;
    }

    public void setAuthBit(String authBit) {
        this.authBit = authBit;
    }
}

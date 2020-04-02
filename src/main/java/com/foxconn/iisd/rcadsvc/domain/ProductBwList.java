package com.foxconn.iisd.rcadsvc.domain;

import com.foxconn.iisd.rcadsvc.msg.ProductBWViewMsg;

import javax.persistence.*;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Entity
@Table(name="`product_bw_list`")
public class ProductBwList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "`product`")
    private String product;

	@Column(name = "`list_type`")
	private String listType;

	@Column(name = "`black_white`")
	private String blackWhite;

	@Column(name = "`black_list`")
	private String blackList;

	@Column(name = "`white_list`")
	private String whiteList;

	@Column(name = "`setting_json`")
	private String settingJson;

	@Column(name = "`create_user`")
    private String createUser;

    @Column(name = "`create_time`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @Column(name = "`modify_user`")
    private String modifyUser;
    
    @Column(name = "`modify_time`")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifyTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getListType() {
		return listType;
	}

	public void setListType(String listType) {
		this.listType = listType;
	}

	public String getBlackWhite() {
		return blackWhite;
	}

	public void setBlackWhite(String blackWhite) {
		this.blackWhite = blackWhite;
	}

	public String getBlackList() {
		return blackList;
	}

	public void setBlackList(String blackList) {
		this.blackList = blackList;
	}

	public String getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(String whiteList) {
		this.whiteList = whiteList;
	}

	public String getSettingJson() {
		return settingJson;
	}

	public void setSettingJson(String settingJson) {
		this.settingJson = settingJson;
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

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	public ProductBWViewMsg toMsg(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


		ProductBWViewMsg productMsg = new ProductBWViewMsg();
		productMsg.setId(this.getId());
		productMsg.setProduct(this.getProduct());
		productMsg.setListType(this.getListType());
		if(this.getBlackList() != null) {
			String[] bArray = this.getBlackList().split(",");
			productMsg.setBlackList(Arrays.asList(bArray));
		}
		if(this.getWhiteList() != null) {
			String[] wArray = this.getWhiteList().split(",");
			productMsg.setWhiteList(Arrays.asList(wArray));
		}




		return productMsg;
	}
}

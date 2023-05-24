package com.youkeda.application.art.member.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;
import com.youkeda.model.Base;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 用户模型
 */
@Document(collection = "users", collation = "{locale: 'zh'}")
public class User extends Base<User> {

    private static final long serialVersionUID = 7748234134327237924L;
    /**
     * 命名空间，默认存短ID，用户可以自定义
     */
    private String namespace;
    /**
     * 用户个人签名
     */
    private String description;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户名
     */
    @NotNull
    private String userName;

    /**
     * 用户名称
     */
    private String name;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 头像
     */
    private String avatarUrl;
    /**
     * 性别
     */
    private Gender gender;

    /**
     * 用户状态
     */
    private UserStatus status;

    /**
     * 出生年月日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    @JsonSerialize(using = NullSerializer.class)
    @NotNull
    private String pwd;

    // 密码盐值
    @JsonIgnore
    private String slot = "ykd";

    /**
     * 用户协议版本
     */
    private String agreementVersion;

    public static User create() {
        return new User();
    }

    public String getDescription() {
        return description;
    }

    public User setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getNamespace() {
        return namespace;
    }

    public User setNamespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public User setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getNickName() {
        return nickName;
    }

    public User setNickName(String nickName) {
        this.nickName = nickName;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public User setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public UserStatus getStatus() {
        return status;
    }

    public User setStatus(UserStatus status) {
        this.status = status;
        return this;
    }

    public Date getBirthday() {
        return birthday;
    }

    public User setBirthday(Date birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getAvatarUrl() {

        if (StringUtils.startsWith(avatarUrl, "http://")) {
            return avatarUrl.replace("http://", "//");
        }

        return avatarUrl;
    }

    public User setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public User setPwd(String pwd) {
        this.pwd = pwd;
        return this;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getAgreementVersion() {
        return agreementVersion;
    }

    public User setAgreementVersion(String agreementVersion) {
        this.agreementVersion = agreementVersion;
        return this;
    }

}


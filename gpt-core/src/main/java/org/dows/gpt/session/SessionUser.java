package org.dows.gpt.session;

import lombok.Data;

@Data
public class SessionUser/* implements UserDetails, Serializable*/ {

    /**
     * id
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    private String password;

    /**
     * 组织机构ID
     */
    private String organizationId;

    /**
     * saas租户
     */
    private String tenantId;

    /**
     * 状态
     */
    private Integer status = 0;

    public SessionUser() {
    }

    public SessionUser(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Collections.emptyList();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return false;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return false;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return false;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return false;
//    }
}

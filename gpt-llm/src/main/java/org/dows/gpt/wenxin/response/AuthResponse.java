package org.dows.gpt.wenxin.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文心一言token信息
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    String refresh_token;
    Long expires_in;
    String session_key;
    String access_token;
    String scope;
    String session_secret;

}

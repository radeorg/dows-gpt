package org.dows.gpt.internlm;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模型
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Model {

    private String id;
    private String object;
    private Long created;
    @SerializedName("owner_by")
    private String ownedBy;

}

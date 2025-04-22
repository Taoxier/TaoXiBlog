package generator;

import java.io.Serializable;
import lombok.Data;

/**
 * site_setting
 * @author 
 */
@Data
public class SiteSetting implements Serializable {
    private Long id;

    private String nameEn;

    private String nameZh;

    private String value;

    /**
     * 1基础设置，2页脚徽标，3资料卡，4友链信息
     */
    private Integer type;

    private static final long serialVersionUID = 1L;
}
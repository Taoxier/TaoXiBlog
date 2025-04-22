package generator;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * login_log
 * @author 
 */
@Data
public class LoginLog implements Serializable {
    private Long id;

    /**
     * 用户名称
     */
    private String username;

    /**
     * ip
     */
    private String ip;

    /**
     * ip来源
     */
    private String ipSource;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 登录状态
     */
    private Boolean status;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 登录时间
     */
    private Date createTime;

    /**
     * user-agent用户代理
     */
    private String userAgent;

    private static final long serialVersionUID = 1L;
}
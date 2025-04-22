package generator;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * visitor
 * @author 
 */
@Data
public class Visitor implements Serializable {
    private Long id;

    /**
     * 访客标识码
     */
    private String uuid;

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
     * 首次访问时间
     */
    private Date createTime;

    /**
     * 最后访问时间
     */
    private Date lastTime;

    /**
     * 访问页数统计
     */
    private Integer pv;

    /**
     * user-agent用户代理
     */
    private String userAgent;

    private static final long serialVersionUID = 1L;
}
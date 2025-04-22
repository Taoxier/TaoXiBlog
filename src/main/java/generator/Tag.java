package generator;

import java.io.Serializable;
import lombok.Data;

/**
 * tag
 * @author 
 */
@Data
public class Tag implements Serializable {
    private Long id;

    private String tagName;

    /**
     * 标签颜色(可选)
     */
    private String color;

    private static final long serialVersionUID = 1L;
}
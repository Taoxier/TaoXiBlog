package generator;

import java.io.Serializable;
import lombok.Data;

/**
 * blog_tag
 * @author 
 */
@Data
public class BlogTag implements Serializable {
    private Long blogId;

    private Long tagId;

    private static final long serialVersionUID = 1L;
}
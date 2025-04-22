package generator;

import java.io.Serializable;
import lombok.Data;

/**
 * category
 * @author 
 */
@Data
public class Category implements Serializable {
    private Long id;

    private String categoryName;

    private static final long serialVersionUID = 1L;
}
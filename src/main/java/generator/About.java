package generator;

import java.io.Serializable;
import lombok.Data;

/**
 * about
 * @author 
 */
@Data
public class About implements Serializable {
    private Long id;

    private String nameEn;

    private String nameZh;

    private String value;

    private static final long serialVersionUID = 1L;
}
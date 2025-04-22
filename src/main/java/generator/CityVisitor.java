package generator;

import java.io.Serializable;
import lombok.Data;

/**
 * city_visitor
 * @author 
 */
@Data
public class CityVisitor implements Serializable {
    /**
     * 城市名称
     */
    private String city;

    /**
     * 独立访客数量
     */
    private Integer uv;

    private static final long serialVersionUID = 1L;
}
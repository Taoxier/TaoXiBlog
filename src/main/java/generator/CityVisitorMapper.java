package generator;

import generator.CityVisitor;

public interface CityVisitorMapper {
    int deleteByPrimaryKey(String city);

    int insert(CityVisitor record);

    int insertSelective(CityVisitor record);

    CityVisitor selectByPrimaryKey(String city);

    int updateByPrimaryKeySelective(CityVisitor record);

    int updateByPrimaryKey(CityVisitor record);
}
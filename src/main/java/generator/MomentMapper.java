package generator;

import generator.Moment;

public interface MomentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Moment record);

    int insertSelective(Moment record);

    Moment selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Moment record);

    int updateByPrimaryKey(Moment record);
}
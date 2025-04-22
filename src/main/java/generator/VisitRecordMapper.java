package generator;

import generator.VisitRecord;

public interface VisitRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(VisitRecord record);

    int insertSelective(VisitRecord record);

    VisitRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(VisitRecord record);

    int updateByPrimaryKey(VisitRecord record);
}
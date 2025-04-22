package generator;

import generator.SiteSetting;

public interface SiteSettingMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SiteSetting record);

    int insertSelective(SiteSetting record);

    SiteSetting selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SiteSetting record);

    int updateByPrimaryKey(SiteSetting record);
}
package generator;

import generator.BlogTag;

public interface BlogTagMapper {
    int insert(BlogTag record);

    int insertSelective(BlogTag record);
}
package com.taoxier.taoxiblog.util.markdown.ext.heimu;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;

/**
 * @Description ：定义并提供一种使用 "@@" 作为起始和结束分隔符的机制
 * @Author taoxier
 * @Date 2025/4/22
 */
public class Heimu extends CustomNode implements Delimited {
    private static final String DELIMITER = "@@";

    @Override
    public String getOpeningDelimiter() {
        return DELIMITER;
    }

    @Override
    public String getClosingDelimiter() {
        return DELIMITER;
    }
}

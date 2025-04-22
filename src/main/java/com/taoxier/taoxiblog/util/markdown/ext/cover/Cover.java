package com.taoxier.taoxiblog.util.markdown.ext.cover;

import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;

/**
 * @Description ：定义并提供一种使用 "%%" 作为起始和结束分隔符的机制
 * @Author taoxier
 * @Date 2025/4/22
 */
public class Cover extends CustomNode implements Delimited {
    /*
    分隔符
     */
    private static final String DELIMITER="%%";

    //起始分隔符
    @Override
    public String getOpeningDelimiter() {
        return DELIMITER;
    }

    //结束分隔符
    @Override
    public String getClosingDelimiter() {
        return DELIMITER;
    }
}

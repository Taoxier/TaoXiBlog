package com.taoxier.taoxiblog.util.markdown.ext.cover.internal;

import com.taoxier.taoxiblog.util.markdown.ext.cover.Cover;
import org.commonmark.node.*;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;

/**
 * @Description ：定界
 * @Author taoxier
 * @Date 2025/4/22
 */
public class CoverDelimiterProcessor implements DelimiterProcessor {

    @Override
    public char getOpeningCharacter() {
        return '%';
    }

    @Override
    public char getClosingCharacter() {
        return '%';
    }

    @Override
    public int getMinLength() {
        return 2;
    }

    /**
     * @param openingRun
     * @param closingRun
     * @Description 将起始和结束分隔符之间的所有节点包装在 Cover 节点中，并更新源跨度信息，最后将 Cover 节点插入到合适的位置
     * @Author: taoxier
     * @Date: 2025/4/22
     * @Return: int
     */
    @Override
    public int process(DelimiterRun openingRun, DelimiterRun closingRun) {
        if (openingRun.length() >= 2 && closingRun.length() >= 2) {
            Text opener = openingRun.getOpener();

            Node cover = new Cover();

            //记录节点的源跨度信息
            SourceSpans sourceSpans = new SourceSpans();

            //从起始分隔符序列中获取前两个分隔符的源跨度信息，并添加到 sourceSpans 中。
            sourceSpans.addAllFrom(openingRun.getOpeners(2));
            for (Node node : Nodes.between(opener, closingRun.getCloser())) {
                //将每个节点添加到 cover 节点的子节点列表中
                cover.appendChild(node);
                //将每个节点的源跨度信息添加到 sourceSpans 中
                sourceSpans.addAll(node.getSourceSpans());
            }
            //从结束分隔符序列中获取前两个分隔符的源跨度信息，并添加到 sourceSpans 中
            sourceSpans.addAllFrom(closingRun.getClosers(2));

            //将 sourceSpans 中的源跨度信息设置到 cover 节点上
            cover.setSourceSpans(sourceSpans.getSourceSpans());

            //将 cover 节点插入到起始分隔符节点 opener 的后面
            opener.insertAfter(cover);
            return 2;
        }
        return 0;
    }
}

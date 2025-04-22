package com.taoxier.taoxiblog.util.markdown.ext.cover.internal;

import org.commonmark.node.Node;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description ：将 Cover 类型的节点渲染为 HTML 内容
 * @Author taoxier
 * @Date 2025/4/22
 */
public class CoverHtmlNodeRenderer extends AbstractCoverNodeRenderer{
    private final HtmlNodeRendererContext context;
    private final HtmlWriter html;

    public CoverHtmlNodeRenderer(HtmlNodeRendererContext context) {
        this.context = context;
        this.html = context.getWriter();
    }

    @Override
    public void render(Node node) {
        Map<String, String> attributes = new HashMap<>(2);
        //添加一个键值对 "class": "m-text-cover"，表示要为 HTML 元素添加 m-text-cover 类名
        attributes.put("class", "m-text-cover");
        //渲染一个 <span> 标签，并将 attributes 中的属性添加到该标签中
        html.tag("span", context.extendAttributes(node, "span", attributes));
        //递归渲染当前节点的所有子节点
        renderChildren(node);
        //结束 <span> 标签的渲染
        html.tag("/span");
    }

    //递归渲染子节点
    private void renderChildren(Node parent) {
        Node node = parent.getFirstChild();
        while (node != null) {
            Node next = node.getNext();
            context.render(node);
            node = next;
        }
    }
}

package com.taoxier.taoxiblog.util.markdown.ext.cover.internal;

import org.commonmark.node.Node;
import org.commonmark.renderer.text.TextContentNodeRendererContext;
import org.commonmark.renderer.text.TextContentWriter;

/**
 * @Description ：将 Cover 类型的节点渲染为文本内容
 * @Author taoxier
 * @Date 2025/4/22
 */
public class CoverTextContentNodeRenderer extends AbstractCoverNodeRenderer{
    private final TextContentNodeRendererContext context;
    private final TextContentWriter textContent;

    public CoverTextContentNodeRenderer(TextContentNodeRendererContext context) {
        this.context = context;
        this.textContent = context.getWriter();
    }

    @Override
    public void render(Node node) {
        //向文本内容写入器输出一个斜杠 /
        textContent.write('/');
        //递归地渲染当前节点的所有子节点
        renderChildren(node);
        //输出另一个斜杠 /
        textContent.write('/');
    }

    //递归渲染父节点的所有子节点
    private void renderChildren(Node parent) {
        Node node = parent.getFirstChild();
        while (node != null) {
            Node next = node.getNext();
            context.render(node);
            node = next;
        }
    }
}

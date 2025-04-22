package com.taoxier.taoxiblog.util.markdown.ext.heimu.internal;

import org.commonmark.node.Node;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description ：将Heimu 类型的节点渲染为 HTML 内容
 * @Author taoxier
 * @Date 2025/4/22
 */
public class HeimuHtmlNodeRenderer extends AbstractHeimuNodeRenderer {
	private final HtmlNodeRendererContext context;
	private final HtmlWriter html;

	public HeimuHtmlNodeRenderer(HtmlNodeRendererContext context) {
		this.context = context;
		this.html = context.getWriter();
	}

	@Override
	public void render(Node node) {
		Map<String, String> attributes = new HashMap<>(4);
		attributes.put("class", "m-text-heimu");
		//设置元素的提示信息
		attributes.put("title", "你知道的太多了");
		html.tag("span", context.extendAttributes(node, "span", attributes));
		renderChildren(node);
		html.tag("/span");
	}

	private void renderChildren(Node parent) {
		Node node = parent.getFirstChild();
		while (node != null) {
			Node next = node.getNext();
			context.render(node);
			node = next;
		}
	}
}

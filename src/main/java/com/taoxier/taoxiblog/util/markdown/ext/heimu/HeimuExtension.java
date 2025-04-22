package com.taoxier.taoxiblog.util.markdown.ext.heimu;

import com.taoxier.taoxiblog.util.markdown.ext.heimu.internal.HeimuDelimiterProcessor;
import com.taoxier.taoxiblog.util.markdown.ext.heimu.internal.HeimuHtmlNodeRenderer;
import com.taoxier.taoxiblog.util.markdown.ext.heimu.internal.HeimuTextContentNodeRenderer;
import org.commonmark.Extension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.text.TextContentNodeRendererContext;
import org.commonmark.renderer.text.TextContentNodeRendererFactory;
import org.commonmark.renderer.text.TextContentRenderer;


/**
 * @Description ：将自定义的分隔符处理逻辑和节点渲染逻辑集成到解析器和渲染器中，使得系统能够正确解析和渲染 Heimu 类型的节点
 * @Author taoxier
 * @Date 2025/4/22
 */
public class HeimuExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension, TextContentRenderer.TextContentRendererExtension {
	private HeimuExtension() {
	}

	public static Extension create() {
		return new HeimuExtension();
	}

	@Override
	public void extend(Parser.Builder parserBuilder) {
		parserBuilder.customDelimiterProcessor(new HeimuDelimiterProcessor());
	}

	@Override
	public void extend(HtmlRenderer.Builder rendererBuilder) {
		rendererBuilder.nodeRendererFactory(new HtmlNodeRendererFactory() {
			@Override
			public NodeRenderer create(HtmlNodeRendererContext context) {
				return new HeimuHtmlNodeRenderer(context);
			}
		});
	}

	@Override
	public void extend(TextContentRenderer.Builder rendererBuilder) {
		rendererBuilder.nodeRendererFactory(new TextContentNodeRendererFactory() {
			@Override
			public NodeRenderer create(TextContentNodeRendererContext context) {
				return new HeimuTextContentNodeRenderer(context);
			}
		});
	}
}

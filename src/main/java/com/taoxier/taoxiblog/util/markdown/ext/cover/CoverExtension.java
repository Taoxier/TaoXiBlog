package com.taoxier.taoxiblog.util.markdown.ext.cover;

import com.taoxier.taoxiblog.util.markdown.ext.cover.internal.CoverDelimiterProcessor;
import com.taoxier.taoxiblog.util.markdown.ext.cover.internal.CoverHtmlNodeRenderer;
import com.taoxier.taoxiblog.util.markdown.ext.cover.internal.CoverTextContentNodeRenderer;
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
 * @Description ：将自定义的分隔符处理逻辑和节点渲染逻辑集成到解析器和渲染器中，使得系统能够正确解析和渲染 Cover 类型的节点
 * @Author taoxier
 * @Date 2025/4/22
 */
public class CoverExtension implements Parser.ParserExtension, HtmlRenderer.HtmlRendererExtension, TextContentRenderer.TextContentRendererExtension {

    public CoverExtension() {
    }

    public static Extension create() {
        return new CoverExtension();
    }

    //扩展解析器
    @Override
    public void extend(Parser.Builder builder) {
        builder.customDelimiterProcessor(new CoverDelimiterProcessor());
    }

    //扩展 HTML 渲染器
    @Override
    public void extend(HtmlRenderer.Builder builder) {
        builder.nodeRendererFactory(new HtmlNodeRendererFactory() {
            @Override
            public NodeRenderer create(HtmlNodeRendererContext htmlNodeRendererContext) {
                return new CoverHtmlNodeRenderer(htmlNodeRendererContext);
            }
        });
    }

    //扩展文本内容渲染器
    @Override
    public void extend(TextContentRenderer.Builder builder) {
        builder.nodeRendererFactory(new TextContentNodeRendererFactory() {
            @Override
            public NodeRenderer create(TextContentNodeRendererContext textContentNodeRendererContext) {
                return new CoverTextContentNodeRenderer(textContentNodeRendererContext);
            }
        });
    }
}

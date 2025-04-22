package com.taoxier.taoxiblog.util.markdown.ext.cover.internal;

import com.taoxier.taoxiblog.util.markdown.ext.cover.Cover;
import org.commonmark.node.Node;
import org.commonmark.renderer.NodeRenderer;

import java.util.Collections;
import java.util.Set;

/**
 * @Description ：渲染Cover类型的节点
 * @Author taoxier
 * @Date 2025/4/22
 */
abstract class AbstractCoverNodeRenderer implements NodeRenderer {
    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        return Collections.<Class<? extends Node>>singleton(Cover.class);
    }

    @Override
    public void render(Node node) {

    }
}

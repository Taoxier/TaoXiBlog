package com.taoxier.taoxiblog.util.markdown.ext.heimu.internal;

import com.taoxier.taoxiblog.util.markdown.ext.heimu.Heimu;
import org.commonmark.node.Node;
import org.commonmark.node.Nodes;
import org.commonmark.node.SourceSpans;
import org.commonmark.node.Text;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;


/**
 * @Description ：渲染Heimu类型的节点
 * @Author taoxier
 * @Date 2025/4/22
 */
public class HeimuDelimiterProcessor implements DelimiterProcessor {
    @Override
    public char getOpeningCharacter() {
        return '@';
    }

    @Override
    public char getClosingCharacter() {
        return '@';
    }

    @Override
    public int getMinLength() {
        return 2;
    }

	@Override
	public int process(DelimiterRun openingRun, DelimiterRun closingRun) {
		if (openingRun.length() >= 2 && closingRun.length() >= 2) {

			Text opener = openingRun.getOpener();

			Node heimu = new Heimu();

			SourceSpans sourceSpans = new SourceSpans();

			sourceSpans.addAllFrom(openingRun.getOpeners(2));
			for (Node node : Nodes.between(opener, closingRun.getCloser())) {
				heimu.appendChild(node);
				sourceSpans.addAll(node.getSourceSpans());
			}
			sourceSpans.addAllFrom(closingRun.getClosers(2));

			heimu.setSourceSpans(sourceSpans.getSourceSpans());

			opener.insertAfter(heimu);
			return 2;
		}
		return 0;
	}
}

package markup;

import java.util.List;

abstract class AbstractMarkable implements InParagraph {
    private List<InParagraph> elements;

    public AbstractMarkable(List<InParagraph> elements) {
        this.elements = elements;
    }

    protected void toMarkdown(StringBuilder token, String border) {
        token.append(border);
        for (InParagraph cur : elements) {
            cur.toMarkdown(token);
        }
        token.append(border);
    }

    protected void toHtml(StringBuilder token, String border) {
        token.append("<").append(border).append(">");
        for (InParagraph cur : elements) {
            cur.toHtml(token);
        }
        token.append("</").append(border).append(">");
    }
}

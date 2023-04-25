package markup;

import java.util.List;

abstract class AbstractHtmlList implements InList {
    private List<ListItem> elements;

    public AbstractHtmlList(List<ListItem> elements) {
        this.elements = elements;
    }

    protected void toHtml(StringBuilder token, String border) {
        token.append("<").append(border).append(">");
        for (ListItem cur : elements) {
            cur.toHtml(token);
        }
        token.append("</").append(border).append(">");
    }
}

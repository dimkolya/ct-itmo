package markup;

import java.util.List;

public class ListItem {
    private List<InList> elements;

    public ListItem(List<InList> elements) {
        this.elements = elements;
    }

    public void toHtml(StringBuilder token) {
        token.append("<li>");
        for (InList element : elements) {
            element.toHtml(token);
        }
        token.append("</li>");
    }
}

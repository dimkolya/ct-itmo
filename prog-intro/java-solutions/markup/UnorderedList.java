package markup;

import java.util.List;

public class UnorderedList extends AbstractHtmlList implements InList {
    public UnorderedList(List<ListItem> elements) {
        super(elements);
    }

    @Override
    public void toHtml(StringBuilder token) {
        toHtml(token, "ul");
    }
}

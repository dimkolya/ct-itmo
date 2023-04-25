package markup;

import java.util.List;

public class OrderedList extends AbstractHtmlList implements InList {
    public OrderedList(List<ListItem> elements) {
        super(elements);
    }

    @Override
    public void toHtml(StringBuilder token) {
        toHtml(token, "ol");
    }
}

package markup;

import java.util.List;

public class Strong extends AbstractMarkable {
    public Strong(List<InParagraph> elements) {
        super(elements);
    }

    @Override
    public void toMarkdown(StringBuilder token) {
        toMarkdown(token, "__");
    }

    @Override
    public void toHtml(StringBuilder token) {
        toHtml(token, "strong");
    }
}

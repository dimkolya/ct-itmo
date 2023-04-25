package markup;

import java.util.List;

public class Emphasis extends AbstractMarkable {
    public Emphasis(List<InParagraph> elements) {
        super(elements);
    }

    @Override
    public void toMarkdown(StringBuilder token) {
        toMarkdown(token, "*");
    }

    @Override
    public void toHtml(StringBuilder token) {
        toHtml(token, "em");
    }
}

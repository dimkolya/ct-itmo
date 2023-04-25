package markup;

import java.util.List;

public class Paragraph implements InList {
    private List<InParagraph> elements;

    public Paragraph(List<InParagraph> elements) {
        this.elements = elements;
    }

    public void toMarkdown(StringBuilder token) {
        for (InParagraph element : elements) {
            element.toMarkdown(token);
        }
    }

    public void toHtml(StringBuilder token) {
        for (InParagraph element : elements) {
            element.toHtml(token);
        }
    }
}

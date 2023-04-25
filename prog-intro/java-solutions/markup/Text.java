package markup;

public class Text implements InParagraph {
    private String text;

    public Text(String text) {
        this.text = text;
    }

    public void toMarkdown(StringBuilder token) {
        token.append(text);
    }

    public void toHtml(StringBuilder token) {
        token.append(text);
    }
}

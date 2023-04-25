package markup;

interface InParagraph extends Htmlable {
    void toMarkdown(StringBuilder token);
}

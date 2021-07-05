package nudge.util.text;

/**
 * @author Frederik Dahl
 * XX/06/2021
 */

public class CharEntry {

    private CharEntry next;
    private CharEntry prev;
    private byte charCode;

    protected CharEntry(byte charCode) {
        this(null,charCode,null);
    }

    protected CharEntry(CharEntry prev, byte charCode, CharEntry next) {
        this.prev = prev;
        this.next = next;
        this.charCode = charCode;
    }

    protected CharEntry next() { return next; }

    protected CharEntry prev() { return prev; }

    protected void setNext (CharEntry entry) { next = entry; }

    protected void setPrev(CharEntry entry) { prev = entry; }

    public byte code() { return charCode; }

    public char character() { return (char) charCode; }

    protected void setCode(byte charCode) { this.charCode = charCode; }

    protected boolean hasNext() { return next != null; }

    protected boolean hasPrev() { return prev != null; }
}

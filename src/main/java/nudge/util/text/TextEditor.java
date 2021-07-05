package nudge.util.text;

import java.util.Iterator;
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * @author Frederik Dahl
 * XX/06/2021
 */

public class TextEditor implements Iterable<CharEntry> {

    private final CharEntry sentinel;
    private CharEntry firstEntry;
    private CharEntry pointer;
    private int count;

    public TextEditor() {
        sentinel = new CharEntry((byte)0x20);
        pointer = sentinel;
        count = 0;
    }

    public void type(byte charCode) {

        CharEntry newNode;

        if (isEmpty()) {

            newNode = new CharEntry(sentinel,charCode,null);

            firstEntry = newNode;

            sentinel.setNext(firstEntry);
        }
        else if (onSentinel()) {

            newNode = new CharEntry(pointer,charCode,pointer.next());

            pointer.setNext(newNode);

            firstEntry = newNode;
        }
        else {

            newNode = new CharEntry(pointer,charCode,pointer.next());

            pointer.setNext(newNode);

        }
        pointer = newNode;

        count++;
    }

    public boolean type(char c) {

        if (c <= Byte.MAX_VALUE) {

            type((byte) c);

            return true;
        }
        return false;
    }

    public void insert(byte[] us_ascii) {

        for (byte b : us_ascii) type(b);

    }

    public boolean insert(CharSequence text) {

        byte[] us_ascii = text.toString().getBytes(US_ASCII);

        if (us_ascii.length == text.length()) {

            insert(us_ascii);

            return true;
        }
        return false;
    }

    public void clear() {

        if (!isEmpty()) {

            sentinel.setNext(null);

            pointer = firstEntry = null;

            count = 0;
        }
    }

    public void space() {

        type((byte)0x20);
    }

    public void newLine() {

        type((byte)0x0A);
    }

    public void tab() {

        type((byte)0x09);
    }

    public void delete() {

        if (isEmpty() || onSentinel()) return;

        if (pointer.hasNext()) {

            pointer.next().setPrev(pointer.prev());
        }
        pointer.prev().setNext(pointer.next());

        pointer = pointer.prev();

        if (onFirstEntry()) {

            firstEntry = pointer.next();
        }
        count--;
    }

    public void moveForward() {

        if (pointer.hasNext()) {

            pointer = pointer.next();
        }
    }

    public void moveBackward() {

        if (pointer.hasPrev()) {

            pointer = pointer.prev();
        }
    }

    public void moveToStart() {

        pointer = sentinel;
    }

    public void moveToEnd() {

        while (pointer.hasNext()) {

            pointer = pointer.next();
        }
    }

    public byte[] getBytes() {

        if (isEmpty()) return null;

        byte[] bytes = new byte[count];

        CharEntry entry = firstEntry;

        int i = 0;

        do {
            bytes[i++] = entry.code();
            entry = entry.next();
        }
        while (entry != null);

        return bytes;
    }

    public boolean isEmpty() {

        return count == 0;
    }

    public int length() {

        return count;
    }

    public boolean isPointer(CharEntry entry) {

        return pointer.equals(entry);
    }

    private boolean onSentinel() {

        return !pointer.hasPrev();
    }

    private boolean onFirstEntry() {

        return pointer.equals(firstEntry);
    }

    private boolean atEnd() {

        return !pointer.hasNext();
    }

    @Override
    public String toString() {

        return isEmpty() ? "" : new String(getBytes(),US_ASCII);
    }

    @Override
    public Iterator<CharEntry> iterator() {

        return new Iterator<>() {

            private CharEntry entry = TextEditor.this.sentinel;

            @Override
            public boolean hasNext() {

                return entry.hasNext();
            }

            @Override
            public CharEntry next() {

                return entry = entry.next();
            }

            @Override
            public void remove() {

                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public void forEach(Consumer<? super CharEntry> action) {

        Iterable.super.forEach(action);
    }
}

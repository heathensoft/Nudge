package nudge.core.input;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

public interface MouseListener {

    // dragging and highlighting is in world space


    void l_highlightBox(Rectanglef box);
    void r_highlightBox(Rectanglef box);
    void m_highlightBox(Rectanglef box);
    void l_highlightBoxReleased(Rectanglef box);
    void r_highlightBoxReleased(Rectanglef box);
    void m_highlightBoxReleased(Rectanglef box);

    void l_dragReleased(Vector2f vec);
    void r_dragReleased(Vector2f vec);
    void m_dragReleased(Vector2f vec);
    void l_drag(Vector2f vec);
    void r_drag(Vector2f vec);
    void m_drag(Vector2f vec);

    void rightClick_View(Vector2f pos);
    void rightClick_World(Vector2f pos);
    void leftClick_View(Vector2f pos);
    void leftClick_World(Vector2f pos);
    void wheelClick_View(Vector2f pos);
    void wheelClick_World(Vector2f pos);

    void hover_View(Vector2f pos);
    void hover_World(Vector2f pos);

    void scrollDown();
    void scrollUp();

    boolean isActiveMouseListener();
    boolean ignoreDragAndHighlight();

}

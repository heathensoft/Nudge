package nudge.core.view;

import org.joml.Vector2f;
import org.joml.primitives.Rectanglef;

public interface ViewListener {

    void onCameraZoom(Rectanglef worldView, float zoom);

    void onCameraTranslation(Rectanglef worldView, Vector2f position);

}

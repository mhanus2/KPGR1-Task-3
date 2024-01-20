package solid;

import transforms.Point3D;

import java.awt.*;

public class Axes extends Solid {
    public Axes() {
        // vb
        vb.add(new Point3D(0, 0, 0)); // v0
        vb.add(new Point3D(0.25, 0, 0)); // v1
        vb.add(new Point3D(0, 0.25, 0)); // v2
        vb.add(new Point3D(0, 0, 0.25)); // v3

        // ib
        addIndices(
                0, 1,
                0, 2,
                0, 3
        );

        colors = new Color[3];
        colors[0] = Color.RED;
        colors[1] = Color.GREEN;
        colors[2] = Color.BLUE;
    }
}

package solid;

import transforms.Cubic;
import transforms.Mat4;
import transforms.Point3D;

import java.awt.*;

public class Curve extends Solid {
    public Curve(Mat4 baseMat, Point3D pointA, Point3D pointB, Point3D pointC, Point3D pointD, Color curveColor) {
        Cubic cubic = new Cubic(
                baseMat,
                pointA,pointB,pointC,pointD
        );

        color = curveColor;

        int index = 0;

        for (int i=0;i<=10;i++) {
            float n = (float) i / 10;

            vb.add(cubic.compute(n));

            if (n != 1.0) {
                ib.add(index);
                ib.add(index+1);
                index++;
            }
        }
    }
}

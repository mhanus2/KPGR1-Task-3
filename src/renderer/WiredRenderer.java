package renderer;

import rasterize.LineRasterizer;
import solid.Solid;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec3D;

public class WiredRenderer {
    private final LineRasterizer lineRasterizer;
    private Mat4 view, proj;
    private final Vec3D windowTransformA = new Vec3D(1, -1, 1);
    private final Vec3D windowTransformB = new Vec3D(1, 1, 0);
    private final Vec3D windowTransformC;

    public WiredRenderer(LineRasterizer lineRasterizer, int width, int height) {
        this.lineRasterizer = lineRasterizer;
        this.windowTransformC = new Vec3D((double) (width - 1) / 2, (double) (height - 1) / 2, 1);
    }

    public void render(Solid solid) {
        for (int i = 0; i < solid.getIb().size(); i += 2) {
            int indexA = solid.getIb().get(i);
            int indexB = solid.getIb().get(i + 1);

            Point3D a = solid.getVb().get(indexA);
            Point3D b = solid.getVb().get(indexB);

            // Modelovací tranformace
            a = a.mul(solid.getModel());
            b = b.mul(solid.getModel());

            // Pohledová tranformace
            a = a.mul(view);
            b = b.mul(view);

            // Projekční tranformace
            a = a.mul(proj);
            b = b.mul(proj);

            // Ořezání
            if (isWithinBounds(a)) {
                if (isWithinBounds(b)) {
                    // Dehomogenizace
                    Vec3D v = new Vec3D();
                    Vec3D w = new Vec3D();

                    if (a.dehomog().isPresent())
                        v = a.dehomog().get();
                    if (b.dehomog().isPresent())
                        w = b.dehomog().get();

                    // Tranformace do okna obrazovky
                    Vec3D aTransformed = v.mul(windowTransformA);
                    aTransformed = aTransformed.add(windowTransformB);
                    aTransformed = aTransformed.mul(windowTransformC);

                    Vec3D bTransformed = w.mul(windowTransformA);
                    bTransformed = bTransformed.add(windowTransformB);
                    bTransformed = bTransformed.mul(windowTransformC);

                    // Rasterizace
                    lineRasterizer.rasterize(
                            (int) Math.round(aTransformed.getX()), (int) Math.round(aTransformed.getY()),
                            (int) Math.round(bTransformed.getX()), (int) Math.round(bTransformed.getY()),
                            solid.getColor()
                    );
                }
            }
        }
    }

    public void setView(Mat4 view) {
        this.view = view;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }

    private boolean isWithinBounds(Point3D point) {
        return (-point.getW() <= point.getX() && point.getX() <= point.getW()) &&
                (-point.getW() <= point.getY() && point.getY() <= point.getW()) &&
                (0 <= point.getZ() && point.getZ() <= point.getW());
    }
}


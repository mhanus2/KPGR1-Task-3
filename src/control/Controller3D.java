package control;

import rasterize.LineRasterizer;
import rasterize.LineRasterizerGraphics;
import rasterize.LineRasterizerTrivial;
import rasterize.Raster;
import renderer.WiredRenderer;
import solid.Cube;
import solid.Pyramid;
import solid.Solid;
import transforms.*;
import view.Panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Controller3D implements Controller {
    private final Panel panel;

    private LineRasterizer rasterizer;
    private WiredRenderer renderer;
    int firstX;
    int firstY;
    private double azimuth = 90;
    private double zenith = 0;
    private Camera camera;
    private Mat4 proj;

    public Controller3D(Panel panel) {
        this.panel = panel;
        initObjects(panel.getRaster());
        initListeners(panel);

        update();
    }

    public void initObjects(Raster raster) {
        rasterizer = new LineRasterizerGraphics(raster);
        renderer = new WiredRenderer(rasterizer, raster.getWidth(), raster.getHeight());

        camera = new Camera(
          new Vec3D(0, -1, 0.3),
          Math.toRadians(azimuth),
          Math.toRadians(zenith),
          1,
          true
        );

        proj = new Mat4PerspRH(
                Math.PI / 4,
                raster.getHeight() / (double)raster.getWidth(),
                0.1,
                20
        );
     }

    @Override
    public void initListeners(Panel panel) {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    firstX = e.getX();
                    firstY = e.getY();
                }
                update();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                panel.clear();

                int dy = e.getY() - firstY;
                zenith -= (double) (180 * dy) / panel.getHeight();

                if (zenith > 90) zenith = 90;
                if (zenith < -90) zenith = -90;

                int dx = e.getX() - firstX;

                azimuth -= (double) (180 * dx) / panel.getWidth();
                azimuth = azimuth % 360;

                firstX = e.getX();
                firstY = e.getY();

                if (e.isControlDown()) return;
                update();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // na klávesu C vymazat plátno
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    //TODO
                }
            }
        });

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.resize();
                initObjects(panel.getRaster());
            }
        });
    }

    private void update() {
        panel.clear();

        camera = camera.withAzimuth(Math.toRadians(azimuth)).withZenith(Math.toRadians(zenith));

        renderer.setProj(proj);
        renderer.setView(camera.getViewMatrix());

        Solid cube = new Cube();
        Solid pyramid = new Pyramid();

        Mat4 cubeModelMat = new Mat4RotY(-0.5).mul(new Mat4Scale(0.5)).mul(new Mat4Transl(0.75, 1, 0));
        Mat4 pyramidModelMat = new Mat4RotZ(0.5).mul(new Mat4Scale(1.5)).mul(new Mat4Transl(-1.5, 1, 0));

        cube.setModel(cubeModelMat);
        pyramid.setModel(pyramidModelMat);

        renderer.render(cube);
        renderer.render(pyramid);

        panel.repaint();
    }

    private void hardClear() {
        panel.clear();
    }

}

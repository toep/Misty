package examples;

import com.misty.engine.Game;
import com.misty.engine.graphics.Color;
import com.misty.engine.graphics.Renderer;
import com.misty.listeners.Keys;
import com.misty.utils.Util;
import com.misty.utils.Vector3;

/**
 * Created by Thomas on 5/9/2019.
 */
public class RayTracer extends Game {
    public RayTracer(String name, int width, int height, int scale) {
        super(name, width, height, scale);
    }
    Vector3 pos = new Vector3(0, 0, 0);
    public static void main(String[] args) {
        RayTracer rt = new RayTracer("Ray Tracer", 500, 260, 3);

        rt.start();
    }

    @Override
    public void setup() {

    }

    @Override
    public void draw(Renderer g) {
        float invW = 1/(float)g.getWidth();
        float invH = 1/(float)g.getHeight();
        float fov = 80;
        float aspect = width/(float)height;
        float angle = (float)Math.tan(Math.PI/180.0*fov/2.0);
        Vector3 mid = new Vector3(0, 0, -10).normalize();
        for(int j = 0; j < g.getHeight(); j++) {
            for(int i = 0; i < g.getWidth(); i++) {
                float x = (2 * ((i + 0.5f) * invW) - 1) * angle * aspect;
                float y = (1 - 2 * ((j + 0.5f) * invH)) * angle;
                //System.out.println(x + " " + y);
                Vector3 ray = new Vector3(x, y, -1).normalize();
               // ray = ray.cross(mid);
                ray = ray.mul(1/(float)Math.cos(ray.angleBetween(mid)));
                g.drawPixel(i, j, trace(pos, ray));

            }
        }
    }

    private Color trace(Vector3 pos, Vector3 ray) {
        float tnear = Float.MAX_VALUE;
        float t0 = Float.MAX_VALUE, t1 = Float.MAX_VALUE;
        if (intersect(new Vector3(0, 0, -10), pos, ray, t0, t1)) {
            return Color.GREEN;
           // if (t0 < 0) t0 = t1;
            //if (t0 < tnear) {
            //    tnear = t0;
           // }
        }
        return Color.BLUE;
    }

    boolean intersect(Vector3 center, Vector3 rayorig, Vector3 raydir, float t0, float t1)
    {
        Vector3 l = center.sub(rayorig);
        float tca = l.dot(raydir);
        if (tca < 0) return false;
        float d2 = l.dot(l) - tca * tca;
        if (d2 > 1) return false;
        float thc = (float)Math.sqrt(1 - d2);
        t0 = tca - thc;
        t1 = tca + thc;

        return true;
    }

    @Override
    public void update() {

        if(this.isKeyDown(Keys.A)) {
            pos.x -=.1f;
        }
        if(this.isKeyDown(Keys.D)) {
            pos.x +=.1f;
        }
    }
}

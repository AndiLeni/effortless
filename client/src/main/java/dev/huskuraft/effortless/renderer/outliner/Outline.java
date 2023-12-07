package dev.huskuraft.effortless.renderer.outliner;

import dev.huskuraft.effortless.core.Axis;
import dev.huskuraft.effortless.core.Orientation;
import dev.huskuraft.effortless.core.Resource;
import dev.huskuraft.effortless.math.MathUtils;
import dev.huskuraft.effortless.math.Vector3d;
import dev.huskuraft.effortless.renderer.Renderer;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Optional;

public abstract class Outline {

    protected OutlineParams params;

    protected Outline() {
        params = new OutlineParams();
    }

    private static Vector3d rotate(Vector3d vector, double degree, Axis axis) {
        if (degree == 0)
            return vector;
        if (vector == Vector3d.ZERO)
            return vector;

        float angle = (float) (degree / 180f * MathUtils.PI);
        double sin = MathUtils.sin(angle);
        double cos = MathUtils.cos(angle);
        double x = vector.getX();
        double y = vector.getY();
        double z = vector.getZ();

        if (axis == Axis.X)
            return new Vector3d(x, y * cos - z * sin, z * cos + y * sin);
        if (axis == Axis.Y)
            return new Vector3d(x * cos + z * sin, y, z * cos - x * sin);
        if (axis == Axis.Z)
            return new Vector3d(x * cos - y * sin, y * cos + x * sin, z);
        return vector;
    }

    public static Vector3d calculateAxisAlignedPlane(Vector3d vector) {
        vector = vector.normalize();
        return new Vector3d(1, 1, 1).subtract(MathUtils.abs(vector.getX()), MathUtils.abs(vector.getY()), MathUtils.abs(vector.getZ()));
    }

    public abstract void render(Renderer renderer, float deltaTick);

    public void tick() {
    }

    public OutlineParams getParams() {
        return params;
    }

    public void renderCuboidLine(Renderer renderer, Vector3d start, Vector3d end) {
        var diff = end.subtract(start);
//        var hAngle = MathUtils.deg(MathUtils.atan2(diff.getX(), diff.getZ()));
        var hDistance = (float) diff.multiply(1, 0, 1).length();
//        var vAngle = MathUtils.deg(MathUtils.atan2(hDistance, diff.getY())) - 90;
        renderer.pushPose();
        // TODO: 27/1/23
        renderer.translate(start.getX(), start.getY(), start.getZ());
//			.rotateY(hAngle).rotateX(vAngle);
        renderAACuboidLine(renderer, Vector3d.ZERO, new Vector3d(0, 0, diff.length()));
        renderer.popPose();
    }

    public void renderAACuboidLine(Renderer renderer, Vector3d start, Vector3d end) {
        var camera = renderer.getCameraPosition();
        start = start.subtract(camera);
        end = end.subtract(camera);
        var lineWidth = params.getLineWidth();
        if (lineWidth == 0)
            return;

        var renderType = renderer.getStyleProvider().outlineSolid(true);

        var diff = end.subtract(start);
        if (diff.getX() + diff.getY() + diff.getZ() < 0) {
            var temp = start;
            start = end;
            end = temp;
            diff = diff.scale(-1);
        }

        var extension = diff.normalize().scale(lineWidth / 2);
        var plane = calculateAxisAlignedPlane(diff);
        var face = Orientation.getNearest(diff.getX(), diff.getY(), diff.getZ());
        var axis = face.getAxis();

        start = start.subtract(extension);
        end = end.add(extension);
        plane = plane.scale(lineWidth / 2);

        var a1 = plane.add(start);
        var b1 = plane.add(end);
        plane = rotate(plane, -90, axis);
        var a2 = plane.add(start);
        var b2 = plane.add(end);
        plane = rotate(plane, -90, axis);
        var a3 = plane.add(start);
        var b3 = plane.add(end);
        plane = rotate(plane, -90, axis);
        var a4 = plane.add(start);
        var b4 = plane.add(end);

        if (params.disableNormals) {
            face = Orientation.UP;
            renderer.drawQuad(renderType, b4, b3, b2, b1, params.lightMap, params.getColor().getRGB(), face);
            renderer.drawQuad(renderType, a1, a2, a3, a4, params.lightMap, params.getColor().getRGB(), face);
            renderer.drawQuad(renderType, a1, b1, b2, a2, params.lightMap, params.getColor().getRGB(), face);
            renderer.drawQuad(renderType, a2, b2, b3, a3, params.lightMap, params.getColor().getRGB(), face);
            renderer.drawQuad(renderType, a3, b3, b4, a4, params.lightMap, params.getColor().getRGB(), face);
            renderer.drawQuad(renderType, a4, b4, b1, a1, params.lightMap, params.getColor().getRGB(), face);
            return;
        }

        renderer.drawQuad(renderType, b4, b3, b2, b1, params.lightMap, params.getColor().getRGB(), face);
        renderer.drawQuad(renderType, a1, a2, a3, a4, params.lightMap, params.getColor().getRGB(), face.getOpposite());
        var vec = a1.subtract(a4);
        face = Orientation.getNearest(vec.getX(), vec.getY(), vec.getZ());
        renderer.drawQuad(renderType, a1, b1, b2, a2, params.lightMap, params.getColor().getRGB(), face);
        vec = rotate(vec, -90, axis);
        face = Orientation.getNearest(vec.getX(), vec.getY(), vec.getZ());
        renderer.drawQuad(renderType, a2, b2, b3, a3, params.lightMap, params.getColor().getRGB(), face);
        vec = rotate(vec, -90, axis);
        face = Orientation.getNearest(vec.getX(), vec.getY(), vec.getZ());
        renderer.drawQuad(renderType, a3, b3, b4, a4, params.lightMap, params.getColor().getRGB(), face);
        vec = rotate(vec, -90, axis);
        face = Orientation.getNearest(vec.getX(), vec.getY(), vec.getZ());
        renderer.drawQuad(renderType, a4, b4, b1, a1, params.lightMap, params.getColor().getRGB(), face);
    }

    public static class OutlineParams {
        protected Optional<Resource> faceTexture;
        protected Optional<Resource> hightlightedFaceTexture;
        protected Orientation highlightedFace;
        protected boolean fadeLineWidth;
        protected boolean disableCull;
        protected boolean disableNormals;
        protected float alpha;
        protected int lightMap;
        protected Color rgb;
        private float lineWidth;

        public OutlineParams() {
            faceTexture = hightlightedFaceTexture = Optional.empty();
            alpha = 1;
            lineWidth = 1 / 32f;
            fadeLineWidth = true;
            rgb = Color.WHITE;
            lightMap = LightTexture.FULL_BRIGHT;
        }

        // builder

        public OutlineParams colored(float r, float g, float b, float a) {
            rgb = new Color(r, g, b, a);
            return this;
        }

        public OutlineParams colored(int red, int green, int blue, int alpha) {
            rgb = new Color(red, green, blue, alpha);
            return this;
        }

        public OutlineParams colored(int color) {
            rgb = new Color(color, false);
            return this;
        }

        public OutlineParams colored(Color c) {
            rgb = c;
            return this;
        }

        public OutlineParams lightMap(int light) {
            lightMap = light;
            return this;
        }

        public OutlineParams stroke(float width) {
            this.lineWidth = width;
            return this;
        }

        public OutlineParams texture(Resource resource) {
            this.faceTexture = Optional.ofNullable(resource);
            return this;
        }

        public OutlineParams clearTextures() {
            return this.textures(null, null);
        }

        public OutlineParams textures(Resource texture, Resource highlightTexture) {
            this.faceTexture = Optional.ofNullable(texture);
            this.hightlightedFaceTexture = Optional.ofNullable(highlightTexture);
            return this;
        }

        public OutlineParams highlightFace(@Nullable Orientation face) {
            highlightedFace = face;
            return this;
        }

        public OutlineParams disableNormals() {
            disableNormals = true;
            return this;
        }

        public OutlineParams disableCull() {
            disableCull = true;
            return this;
        }

        // getter

        public float getLineWidth() {
            return fadeLineWidth ? alpha * lineWidth : lineWidth;
        }

        public Orientation getHighlightedFace() {
            return highlightedFace;
        }

        public Color getColor() {
            return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), (int) (rgb.getAlpha() * alpha));
        }

    }

    public static class LightTexture {
        public static final int FULL_BRIGHT = 15728880;
        public static final int FULL_SKY = 15728640;
        public static final int FULL_BLOCK = 240;
    }

}
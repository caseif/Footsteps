package net.amigocraft.footsteps.util;

import static org.lwjgl.opengl.GL11.*;

public class GluEmulation {
    public static void gluLookAt(double eyeX, double eyeY, double eyeZ,
            double centerX, double centerY, double centerZ,
            double upX, double upY, double upZ) {
        var f = new double[] { centerX - eyeX, centerY - eyeY, centerZ - eyeZ };
        var fNorm = normalize3(f);

        var up = new double[] { upX, upY, upZ };
        var upNorm = normalize3(up);

        var s = cross3(fNorm, upNorm);
        var sNorm = normalize3(s);

        var u = cross3(sNorm, fNorm);

        var m = new double[] {
            s[0], s[1], s[2], 0,
            u[0], u[1], u[2], 0,
            -fNorm[0], -fNorm[1], -fNorm[2], 0,
            0, 0, 0, 1
        };

        glMultMatrixf(toFloatArray(transpose4x4(m)));
        glTranslated(-eyeX, -eyeY, -eyeZ);
    }

    public static void gluPerspective(double fovy, double aspect, double zNear, double zFar) {
        var f = Math.atan(fovy / 2.0);

        var m = new double[] {
            f / aspect, 0, 0, 0,
            0, f, 0, 0,
            0, 0, (zFar + zNear) / (zNear - zFar), (2 * zFar * zNear) / (zNear - zFar),
            0, 0, -1, 0
        };

        glMultMatrixf(toFloatArray(transpose4x4(m)));
    }

    private static double[] cross3(double[] a, double[] b) {
        if (a.length != 3 || b.length != 3) {
            throw new IllegalArgumentException("Input arrays must contain three values each");
        }

        return new double[] {
                a[1] * b[2] - a[2] * b[1],
                a[2] * b[0] - a[0] * b[2],
                a[0] * b[1] - a[1] * b[0]
        };
    }

    private static double[] normalize3(double[] vec) {
        if (vec.length != 3) {
            throw new IllegalArgumentException("Input array must contain three values");
        }

        var mag = Math.sqrt(Math.pow(vec[0], 2) + Math.pow(vec[1], 2) + Math.pow(vec[2], 2));
        return new double[] { vec[0] / mag, vec[1] / mag, vec[2] / mag };
    }

    private static float[] toFloatArray(double[] arr) {
        var arrF = new float[arr.length];
        for (var i = 0; i < arr.length; i++) {
            arrF[i] = (float) arr[i];
        }

        return arrF;
    }

    public static double[] transpose4x4(double[] mat) {
        if (mat.length != 16) {
            throw new IllegalArgumentException("Input array must contain 16 values");
        }

        return new double[] {
            mat[0], mat[4], mat[8], mat[12],
            mat[1], mat[5], mat[9], mat[13],
            mat[2], mat[6], mat[10], mat[14],
            mat[3], mat[7], mat[11], mat[15],
        };
    }
}

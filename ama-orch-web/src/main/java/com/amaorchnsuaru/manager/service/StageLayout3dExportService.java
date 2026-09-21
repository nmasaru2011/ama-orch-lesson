package com.amaorchnsuaru.manager.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;

import com.amaorchnsuaru.manager.entity.StageLayout;
import com.amaorchnsuaru.manager.entity.StageLayoutSeat;

/**
 * 舞台配置を 3MF (.3mf) に書き出す。ペイント3D・3Dビューアー・Blender などで開ける。
 *
 * <p>単位は cm（論理座標 1 = 1cm。舞台 1200x800 = 12m x 8m）。3D 空間は Z が上、
 * 舞台奥が +Y、上手が +X。DB の座標（y は客席側ほど大きい）とは Y を反転して対応させる。</p>
 *
 * <p>奏者は椅子に座った簡易モデル、指揮者は指揮台の上に立つ簡易モデル。
 * 席ごとに別オブジェクト（名前は氏名/パート名）として出力するので、
 * ペイント3Dで 1 人ずつ選んで動かしたり差し替えたりできる。</p>
 */
@Service
public class StageLayout3dExportService {

    private static final String NS_CORE = "http://schemas.microsoft.com/3dmanufacturing/core/2015/02";

    /** 床の厚み */
    private static final double FLOOR_T = 3;

    public void write3mf(StageLayout layout, List<StageLayoutSeat> seats, OutputStream out)
            throws IOException {
        int stageW = layout.getStageWidth() == null ? 1200 : layout.getStageWidth();
        int stageD = layout.getStageDepth() == null ? 800 : layout.getStageDepth();

        List<NamedMesh> objects = new ArrayList<>();
        objects.add(new NamedMesh("舞台", 1,
                new Mesh().box(0, 0, -FLOOR_T, stageW, stageD, 0)));

        for (StageLayoutSeat s : seats) {
            double x = s.getPosX() == null ? 0 : s.getPosX();
            double y = stageD - (s.getPosY() == null ? 0 : s.getPosY());
            int rot  = s.getRotation() == null ? 0 : s.getRotation();
            Mesh m = figureFor(s).transformed(rot, x, y);
            objects.add(new NamedMesh(seatName(s), 2, m));
        }

        try (ZipOutputStream zip = new ZipOutputStream(out, StandardCharsets.UTF_8)) {
            put(zip, "[Content_Types].xml",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                  + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                  + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                  + "<Default Extension=\"model\" ContentType=\"application/vnd.ms-package.3dmanufacturing-3dmodel+xml\"/>"
                  + "</Types>");
            put(zip, "_rels/.rels",
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                  + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
                  + "<Relationship Target=\"/3D/3dmodel.model\" Id=\"rel0\" "
                  + "Type=\"http://schemas.microsoft.com/3dmanufacturing/2013/01/3dmodel\"/>"
                  + "</Relationships>");
            put(zip, "3D/3dmodel.model", modelXml(objects));
        }
    }

    // =========================================================
    // モデル（ここを差し替えれば、人ごとにモデルを変えられる）
    // =========================================================

    /**
     * 席に置くモデルを返す。原点は席の中心の床面、正面は +Y。
     * 将来「人ごとにモデルを変える」ときは、s.getPersonId() などで分岐する。
     */
    private Mesh figureFor(StageLayoutSeat s) {
        if (StageLayoutSeat.ROLE_CONDUCTOR.equals(s.getRoleType())) {
            return standingFigure();
        }
        return seatedFigure();
    }

    /** 椅子に座った人。椅子の座面 44cm、座面高 44cm、頭頂 約 123cm */
    private Mesh seatedFigure() {
        Mesh m = new Mesh();
        // 椅子: 座面 + 背もたれ(奏者の後ろ = -Y) + 脚
        m.box(-22, -22, 40, 22, 22, 44);
        m.box(-22, -22, 44, 22, -19, 80);
        for (int sx : new int[] {-1, 1}) {
            for (int sy : new int[] {-1, 1}) {
                m.box(sx * 19 - 1.5, sy * 19 - 1.5, 0, sx * 19 + 1.5, sy * 19 + 1.5, 40);
            }
        }
        // 太もも・すね・足
        for (int sx : new int[] {-1, 1}) {
            double x0 = sx > 0 ? 3 : -15;
            double x1 = sx > 0 ? 15 : -3;
            m.box(x0, -8, 44, x1, 30, 55);
            m.box(x0 + 1, 26, 8, x1 - 1, 36, 44);
            m.box(x0, 26, 0, x1, 42, 8);
        }
        // 胴・腕・頭
        m.box(-18, -15, 55, 18, 5, 98);
        m.box(-25, -8, 78, -18, 28, 90);
        m.box(18, -8, 78, 25, 28, 90);
        m.sphere(0, -5, 110, 11);
        return m;
    }

    /** 指揮台の上に立つ人。台 15cm + 身長 約 170cm */
    private Mesh standingFigure() {
        Mesh m = new Mesh();
        m.box(-45, -45, 0, 45, 45, 15);
        for (int sx : new int[] {-1, 1}) {
            double x0 = sx > 0 ? 2 : -14;
            double x1 = sx > 0 ? 14 : -2;
            m.box(x0, -7, 15, x1, 7, 95);
        }
        m.box(-19, -10, 95, 19, 10, 145);
        m.box(-26, -8, 120, -19, 24, 132);
        m.box(19, -8, 120, 26, 24, 132);
        m.sphere(0, 0, 158, 12);
        return m;
    }

    private static String seatName(StageLayoutSeat s) {
        String person = blankToNull(s.getPersonName());
        String part = blankToNull(s.getPartName());
        if (part == null) {
            part = blankToNull(s.getPartCode());
        }
        if (StageLayoutSeat.ROLE_CONDUCTOR.equals(s.getRoleType())) {
            return person != null ? "指揮 " + person : "指揮者";
        }
        if (person != null && part != null) {
            return part + " " + person;
        }
        return person != null ? person : part != null ? part : "席" + s.getSeatNo();
    }

    // =========================================================
    // 3MF (XML)
    // =========================================================

    private record NamedMesh(String name, int materialIndex, Mesh mesh) {}

    private String modelXml(List<NamedMesh> objects) {
        StringBuilder sb = new StringBuilder(256 * 1024);
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<model unit=\"centimeter\" xml:lang=\"ja-JP\" xmlns=\"").append(NS_CORE).append("\">\n");
        sb.append("<resources>\n");
        sb.append("<basematerials id=\"1\">"
                + "<base name=\"舞台\" displaycolor=\"#E8DCCB\"/>"
                + "<base name=\"人物\" displaycolor=\"#8A8F98\"/>"
                + "</basematerials>\n");

        int id = 2;
        StringBuilder build = new StringBuilder();
        for (NamedMesh o : objects) {
            sb.append("<object id=\"").append(id).append("\" type=\"model\" name=\"")
              .append(esc(o.name())).append("\" pid=\"1\" pindex=\"")
              .append(o.materialIndex() - 1).append("\">\n<mesh>\n<vertices>\n");
            for (double[] v : o.mesh().vertices) {
                sb.append("<vertex x=\"").append(f(v[0])).append("\" y=\"").append(f(v[1]))
                  .append("\" z=\"").append(f(v[2])).append("\"/>\n");
            }
            sb.append("</vertices>\n<triangles>\n");
            for (int[] t : o.mesh().triangles) {
                sb.append("<triangle v1=\"").append(t[0]).append("\" v2=\"").append(t[1])
                  .append("\" v3=\"").append(t[2]).append("\"/>\n");
            }
            sb.append("</triangles>\n</mesh>\n</object>\n");
            build.append("<item objectid=\"").append(id).append("\"/>\n");
            id++;
        }
        sb.append("</resources>\n<build>\n").append(build).append("</build>\n</model>\n");
        return sb.toString();
    }

    private static void put(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    // =========================================================
    // 三角形メッシュ
    // =========================================================

    private static final class Mesh {
        final List<double[]> vertices = new ArrayList<>();
        final List<int[]> triangles = new ArrayList<>();

        /** 直方体（外向き法線 = 反時計回り） */
        Mesh box(double x0, double y0, double z0, double x1, double y1, double z1) {
            int b = vertices.size();
            double[][] p = {
                {x0, y0, z0}, {x1, y0, z0}, {x1, y1, z0}, {x0, y1, z0},
                {x0, y0, z1}, {x1, y0, z1}, {x1, y1, z1}, {x0, y1, z1}};
            vertices.addAll(List.of(p));
            int[][] t = {
                {0, 2, 1}, {0, 3, 2},   // 下
                {4, 5, 6}, {4, 6, 7},   // 上
                {0, 1, 5}, {0, 5, 4},   // 手前 (-Y)
                {1, 2, 6}, {1, 6, 5},   // 右 (+X)
                {2, 3, 7}, {2, 7, 6},   // 奥 (+Y)
                {3, 0, 4}, {3, 4, 7}};  // 左 (-X)
            for (int[] tri : t) {
                triangles.add(new int[] {b + tri[0], b + tri[1], b + tri[2]});
            }
            return this;
        }

        /** 低ポリの球（頭用） */
        Mesh sphere(double cx, double cy, double cz, double r) {
            final int seg = 12;
            final int ring = 8;
            int b = vertices.size();
            vertices.add(new double[] {cx, cy, cz + r});                       // 頂点
            for (int i = 1; i < ring; i++) {
                double phi = Math.PI * i / ring;
                for (int j = 0; j < seg; j++) {
                    double th = 2 * Math.PI * j / seg;
                    vertices.add(new double[] {
                        cx + r * Math.sin(phi) * Math.cos(th),
                        cy + r * Math.sin(phi) * Math.sin(th),
                        cz + r * Math.cos(phi)});
                }
            }
            vertices.add(new double[] {cx, cy, cz - r});                       // 底
            int bottom = vertices.size() - 1;
            for (int j = 0; j < seg; j++) {
                int n = (j + 1) % seg;
                triangles.add(new int[] {b, b + 1 + j, b + 1 + n});
            }
            for (int i = 0; i < ring - 2; i++) {
                int r0 = b + 1 + i * seg;
                int r1 = r0 + seg;
                for (int j = 0; j < seg; j++) {
                    int n = (j + 1) % seg;
                    triangles.add(new int[] {r0 + j, r1 + j, r1 + n});
                    triangles.add(new int[] {r0 + j, r1 + n, r0 + n});
                }
            }
            int last = b + 1 + (ring - 2) * seg;
            for (int j = 0; j < seg; j++) {
                int n = (j + 1) % seg;
                triangles.add(new int[] {last + j, bottom, last + n});
            }
            return this;
        }

        /**
         * 向き rotationDeg（0 = +Y 向き、上から見て時計回り）に回して (tx, ty) へ移動した
         * 新しいメッシュを返す。
         */
        Mesh transformed(int rotationDeg, double tx, double ty) {
            double a = Math.toRadians(rotationDeg);
            double cos = Math.cos(a);
            double sin = Math.sin(a);
            Mesh m = new Mesh();
            for (double[] v : vertices) {
                m.vertices.add(new double[] {
                    v[0] * cos + v[1] * sin + tx,
                    -v[0] * sin + v[1] * cos + ty,
                    v[2]});
            }
            m.triangles.addAll(triangles);
            return m;
        }
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private static String f(double v) {
        return String.format(java.util.Locale.ROOT, "%.2f", v);
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}

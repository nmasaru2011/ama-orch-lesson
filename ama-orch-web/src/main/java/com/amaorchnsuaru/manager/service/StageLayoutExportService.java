package com.amaorchnsuaru.manager.service;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFAutoShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextBox;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.springframework.stereotype.Service;

import com.amaorchnsuaru.manager.entity.StageLayout;
import com.amaorchnsuaru.manager.entity.StageLayoutSeat;

/**
 * 舞台配置を PowerPoint (.pptx) と SVG に書き出す。
 *
 * <p>pptx は画像ではなく「楕円＋三角形＋テキストボックス」のネイティブ図形として出力するので、
 * PowerPoint 上で個々の席をそのまま動かしたり、色や文字を直したりできる。</p>
 */
@Service
public class StageLayoutExportService {

    /** スライドサイズ（ポイント）。13.333in x 7.5in = 16:9 ワイド */
    private static final double SLIDE_W = 960;
    private static final double SLIDE_H = 540;

    /** 舞台を描く領域 */
    private static final double AREA_X = 30;
    private static final double AREA_Y = 44;
    private static final double AREA_W = 900;
    private static final double AREA_H = 476;

    /** 論理座標での座席の直径（画面エディタの .seat と同じ） */
    private static final double SEAT_D = 54;

    private static final String FONT = "Meiryo";

    private static final String STAGE_FILL = "fbf7f0";
    private static final String STAGE_LINE = "c9b8a8";

    /**
     * パートごとの配色。画面側 layout/edit.html の PART_STYLE と同じ値を使う
     * （出力と画面で見た目を揃えるため。片方を変えたらもう片方も直すこと）。
     */
    private record Palette(String line, String fill) {}

    private static final Map<String, Palette> PART_STYLE = Map.ofEntries(
            Map.entry("Cond", new Palette("dc3545", "fdeef0")),
            Map.entry("Vn1",  new Palette("6f42c1", "f3eeff")),
            Map.entry("Vn2",  new Palette("6f42c1", "f3eeff")),
            Map.entry("Va",   new Palette("5936a8", "f3eeff")),
            Map.entry("Vc",   new Palette("5936a8", "f3eeff")),
            Map.entry("Cb",   new Palette("45298a", "f3eeff")),
            Map.entry("Fl",   new Palette("198754", "e9f7f0")),
            Map.entry("Ob",   new Palette("198754", "e9f7f0")),
            Map.entry("Cl",   new Palette("198754", "e9f7f0")),
            Map.entry("Fg",   new Palette("198754", "e9f7f0")),
            Map.entry("Hr",   new Palette("fd7e14", "fff3e8")),
            Map.entry("Tp",   new Palette("fd7e14", "fff3e8")),
            Map.entry("Tb",   new Palette("e8590c", "fff3e8")),
            Map.entry("Tu",   new Palette("e8590c", "fff3e8")),
            Map.entry("Timp", new Palette("6c757d", "f1f3f5")),
            Map.entry("Perc", new Palette("6c757d", "f1f3f5")),
            Map.entry("Hp",   new Palette("0d6efd", "e7f1ff")),
            Map.entry("Pf",   new Palette("0d6efd", "e7f1ff")));

    private static final Palette DEFAULT_STYLE = new Palette("495057", "f8f9fa");

    // =========================================================
    // PowerPoint
    // =========================================================

    public void writePptx(StageLayout layout, List<StageLayoutSeat> seats, OutputStream out)
            throws IOException {
        try (XMLSlideShow ppt = new XMLSlideShow()) {
            ppt.setPageSize(new Dimension((int) SLIDE_W, (int) SLIDE_H));
            XSLFSlide slide = ppt.createSlide();

            int stageW = nz(layout.getStageWidth(), 1200);
            int stageD = nz(layout.getStageDepth(), 800);
            Transform t = new Transform(stageW, stageD);

            XSLFTextBox title = slide.createTextBox();
            title.setAnchor(new Rectangle2D.Double(AREA_X, 8, AREA_W, 30));
            oneLine(title, layout.getLayoutName(), 16, true, color("333333"),
                    TextParagraph.TextAlign.LEFT);

            XSLFAutoShape stage = slide.createAutoShape();
            stage.setShapeType(ShapeType.ROUND_RECT);
            stage.setAnchor(new Rectangle2D.Double(t.originX, t.originY, t.drawW, t.drawH));
            stage.setFillColor(color(STAGE_FILL));
            stage.setLineColor(color(STAGE_LINE));
            stage.setLineWidth(1.5);

            addNote(slide, "舞台奥", t.originX, t.originY + 4, t.drawW);
            addNote(slide, "客席",   t.originX, t.originY + t.drawH - 18, t.drawW);

            for (StageLayoutSeat s : seats) {
                drawSeat(slide, t, s);
            }
            ppt.write(out);
        }
    }

    private void drawSeat(XSLFSlide slide, Transform t, StageLayoutSeat s) {
        Palette pal = paletteOf(s);
        double cx = t.x(nz(s.getPosX(), 0));
        double cy = t.y(nz(s.getPosY(), 0));
        double d  = Math.max(26, SEAT_D * t.scale);
        double r  = d / 2;
        int rot   = nz(s.getRotation(), 0);

        // 向きを示す三角形。ShapeType.TRIANGLE は上向きなので rotation をそのまま渡せる
        double aw  = d * 0.42;
        double ah  = d * 0.34;
        double rad = Math.toRadians(rot);
        double ax  = cx + Math.sin(rad) * (r + ah * 0.55);
        double ay  = cy - Math.cos(rad) * (r + ah * 0.55);
        XSLFAutoShape arrow = slide.createAutoShape();
        arrow.setShapeType(ShapeType.TRIANGLE);
        arrow.setAnchor(new Rectangle2D.Double(ax - aw / 2, ay - ah / 2, aw, ah));
        arrow.setFillColor(color(pal.line()));
        arrow.setLineColor(color(pal.line()));
        arrow.setRotation(rot);

        XSLFAutoShape circle = slide.createAutoShape();
        circle.setShapeType(ShapeType.ELLIPSE);
        circle.setAnchor(new Rectangle2D.Double(cx - r, cy - r, d, d));
        circle.setFillColor(color(pal.fill()));
        circle.setLineColor(color(pal.line()));
        circle.setLineWidth(1.25);

        // 画面と同じルール: 人がいれば丸の中は氏名、いなければパートコード
        String person = trimToNull(s.getPersonName());
        List<Line> inside = new ArrayList<>();
        if (person != null) {
            inside.add(new Line(person, nameFontSize(person, d), true, color("222222")));
        } else {
            inside.add(new Line(circleCode(s), d * 0.27, true, color("333333")));
        }
        String pult = pultLabel(s);
        if (!pult.isEmpty()) {
            inside.add(new Line(pult, d * 0.20, false, color("666666")));
        }
        multiLine(circle, inside);

        // 人がいる席だけ、丸の下にパート名
        if (person != null) {
            String part = partLabel(s);
            if (!part.isEmpty()) {
                XSLFTextBox label = slide.createTextBox();
                double lw = Math.max(d * 2.4, 58);
                label.setAnchor(new Rectangle2D.Double(cx - lw / 2, cy + r + 1, lw, 13));
                oneLine(label, part, 6.5, false, color("333333"), TextParagraph.TextAlign.CENTER);
            }
        }
    }

    private void addNote(XSLFSlide slide, String text, double x, double y, double w) {
        XSLFTextBox box = slide.createTextBox();
        box.setAnchor(new Rectangle2D.Double(x, y, w, 14));
        oneLine(box, text, 8, false, color("a08e7c"), TextParagraph.TextAlign.CENTER);
    }

    private record Line(String text, double size, boolean bold, Color color) {}

    private void oneLine(XSLFTextShape shape, String text, double size, boolean bold,
                         Color c, TextParagraph.TextAlign align) {
        prepare(shape);
        XSLFTextParagraph p = shape.addNewTextParagraph();
        p.setTextAlign(align);
        XSLFTextRun run = p.addNewTextRun();
        run.setText(text == null ? "" : text);
        run.setFontSize(size);
        run.setBold(bold);
        run.setFontColor(c);
        run.setFontFamily(FONT);
    }

    private void multiLine(XSLFTextShape shape, List<Line> items) {
        prepare(shape);
        for (Line it : items) {
            XSLFTextParagraph p = shape.addNewTextParagraph();
            p.setTextAlign(TextParagraph.TextAlign.CENTER);
            XSLFTextRun run = p.addNewTextRun();
            run.setText(it.text());
            run.setFontSize(it.size());
            run.setBold(it.bold());
            run.setFontColor(it.color());
            run.setFontFamily(FONT);
        }
    }

    /** 既定の空段落を消し、余白ゼロ・上下中央にそろえる */
    private void prepare(XSLFTextShape shape) {
        shape.clearText();
        shape.setLeftInset(0);
        shape.setRightInset(0);
        shape.setTopInset(0);
        shape.setBottomInset(0);
        shape.setWordWrap(true);
        shape.setVerticalAlignment(VerticalAlignment.MIDDLE);
    }

    // =========================================================
    // SVG（Illustrator / Google スライド / Keynote などに読ませる用）
    // =========================================================

    public String toSvg(StageLayout layout, List<StageLayoutSeat> seats) {
        int w = nz(layout.getStageWidth(), 1200);
        int d = nz(layout.getStageDepth(), 800);
        int headH = 46;

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"").append(w)
          .append("\" height=\"").append(d + headH)
          .append("\" viewBox=\"0 0 ").append(w).append(' ').append(d + headH).append("\">\n");
        sb.append("<style>text{font-family:'Meiryo','Yu Gothic',sans-serif}</style>\n");
        sb.append("<rect width=\"100%\" height=\"100%\" fill=\"#ffffff\"/>\n");
        sb.append("<text x=\"8\" y=\"26\" font-size=\"20\" font-weight=\"bold\" fill=\"#333\">")
          .append(esc(layout.getLayoutName())).append("</text>\n");

        sb.append("<g transform=\"translate(0,").append(headH).append(")\">\n");
        sb.append("<rect x=\"1\" y=\"1\" width=\"").append(w - 2).append("\" height=\"").append(d - 2)
          .append("\" rx=\"8\" fill=\"#").append(STAGE_FILL).append("\" stroke=\"#").append(STAGE_LINE)
          .append("\" stroke-width=\"2\"/>\n");
        sb.append(svgText(w / 2.0, 18, "舞台奥", 12, false, "#a08e7c"));
        sb.append(svgText(w / 2.0, d - 8, "客席", 12, false, "#a08e7c"));

        for (StageLayoutSeat s : seats) {
            Palette pal = paletteOf(s);
            double cx = nz(s.getPosX(), 0);
            double cy = nz(s.getPosY(), 0);
            double r  = SEAT_D / 2;
            int rot   = nz(s.getRotation(), 0);

            // 向きの三角形（0度=上向き、時計回り）
            sb.append("<polygon points=\"0,-").append(fmt(r + 11))
              .append(" -7,-").append(fmt(r + 1)).append(" 7,-").append(fmt(r + 1))
              .append("\" fill=\"#").append(pal.line())
              .append("\" transform=\"translate(").append(fmt(cx)).append(',').append(fmt(cy))
              .append(") rotate(").append(rot).append(")\"/>\n");

            sb.append("<circle cx=\"").append(fmt(cx)).append("\" cy=\"").append(fmt(cy))
              .append("\" r=\"").append(fmt(r)).append("\" fill=\"#").append(pal.fill())
              .append("\" stroke=\"#").append(pal.line()).append("\" stroke-width=\"2\"/>\n");

            String person = trimToNull(s.getPersonName());
            String pult   = pultLabel(s);
            String head   = person != null ? person : circleCode(s);
            double size   = person != null ? svgNameSize(person) : 13;
            if (pult.isEmpty()) {
                sb.append(svgText(cx, cy + size * 0.36, head, size, true, "#222"));
            } else {
                sb.append(svgText(cx, cy - 1, head, size, true, "#222"));
                sb.append(svgText(cx, cy + 11, pult, 9, false, "#666"));
            }
            if (person != null) {
                String part = partLabel(s);
                if (!part.isEmpty()) {
                    sb.append(svgText(cx, cy + r + 13, part, 10, false, "#333"));
                }
            }
        }
        sb.append("</g>\n</svg>\n");
        return sb.toString();
    }

    private String svgText(double x, double y, String text, double size, boolean bold, String fill) {
        return "<text x=\"" + fmt(x) + "\" y=\"" + fmt(y) + "\" font-size=\"" + fmt(size)
                + "\" text-anchor=\"middle\"" + (bold ? " font-weight=\"bold\"" : "")
                + " fill=\"" + fill + "\">" + esc(text) + "</text>\n";
    }

    // =========================================================
    // 共通ロジック（画面側 layout/edit.html と同じ表示ルール）
    // =========================================================

    private Palette paletteOf(StageLayoutSeat s) {
        if (StageLayoutSeat.ROLE_CONDUCTOR.equals(s.getRoleType())) {
            return PART_STYLE.get("Cond");
        }
        return PART_STYLE.getOrDefault(s.getPartCode() == null ? "" : s.getPartCode(), DEFAULT_STYLE);
    }

    private String circleCode(StageLayoutSeat s) {
        if (StageLayoutSeat.ROLE_CONDUCTOR.equals(s.getRoleType())) {
            return "指揮";
        }
        String code = trimToNull(s.getPartCode());
        return code != null ? code : "—";
    }

    private String partLabel(StageLayoutSeat s) {
        String name = trimToNull(s.getPartName());
        if (StageLayoutSeat.ROLE_CONDUCTOR.equals(s.getRoleType())) {
            return name != null ? name : "指揮者";
        }
        if (name != null) {
            return name;
        }
        String code = trimToNull(s.getPartCode());
        return code != null ? code : "";
    }

    private String pultLabel(StageLayoutSeat s) {
        String side = StageLayoutSeat.SIDE_OUT.equals(s.getSeatSide()) ? "表"
                    : StageLayoutSeat.SIDE_IN.equals(s.getSeatSide())  ? "裏" : "";
        if (s.getPultNo() != null && s.getPultNo() > 0) {
            return s.getPultNo() + "pult" + side;
        }
        return side;
    }

    /** 氏名の長さに応じて丸の中の文字サイズを決める（画面側 nameFontSize と同じ段階） */
    private double nameFontSize(String name, double diameter) {
        int len = name.length();
        double ratio = len <= 4 ? 0.21 : len <= 6 ? 0.17 : 0.15;
        return Math.max(4.5, diameter * ratio);
    }

    private double svgNameSize(String name) {
        int len = name.length();
        return len <= 4 ? 11 : len <= 6 ? 9 : 8;
    }

    /** 論理座標 → スライド上のポイント座標 */
    private static final class Transform {
        final double scale;
        final double drawW;
        final double drawH;
        final double originX;
        final double originY;

        Transform(int stageW, int stageD) {
            this.scale   = Math.min(AREA_W / stageW, AREA_H / stageD);
            this.drawW   = stageW * scale;
            this.drawH   = stageD * scale;
            this.originX = AREA_X + (AREA_W - drawW) / 2;
            this.originY = AREA_Y + (AREA_H - drawH) / 2;
        }

        double x(int logicalX) { return originX + logicalX * scale; }
        double y(int logicalY) { return originY + logicalY * scale; }
    }

    private static Color color(String hex) {
        return new Color(Integer.parseInt(hex, 16));
    }

    private static int nz(Integer v, int fallback) {
        return v == null ? fallback : v;
    }

    private static String trimToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private static String fmt(double v) {
        return (Math.rint(v) == v) ? String.valueOf((long) v) : String.format("%.1f", v);
    }

    private static String esc(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}

package com.amaorchnsuaru.manager.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amaorchnsuaru.manager.entity.ConcertProgram;
import com.amaorchnsuaru.manager.entity.OrchData;
import com.amaorchnsuaru.manager.entity.StageLayout;
import com.amaorchnsuaru.manager.entity.StageLayoutSeat;
import com.amaorchnsuaru.manager.repository.ConcertDataRepository;
import com.amaorchnsuaru.manager.repository.ConcertProgramRepository;
import com.amaorchnsuaru.manager.repository.OrchDataRepository;
import com.amaorchnsuaru.manager.repository.StageLayoutRepository;
import com.amaorchnsuaru.manager.repository.StageLayoutSeatRepository;

/**
 * 舞台配置（stage_layout / stage_layout_seat）の生成・保存を担当する。
 *
 * <p>座標系は舞台を真上から見た図。原点は左上、x は下手→上手、y は舞台奥→客席側。
 * 指揮者は客席寄り（y が大きい側）の中央に立ち、向き（rotation）は
 * 0 = 画面上（舞台奥）向きで時計回りの度数。</p>
 */
@Service
public class StageLayoutService {

    /** 既定の舞台サイズ（論理単位。画面では縮尺して描画する） */
    public static final int STAGE_WIDTH = 1200;
    public static final int STAGE_DEPTH = 800;

    private static final int CONDUCTOR_X = 600;
    private static final int CONDUCTOR_Y = 700;

    /** プルトの表／裏を振り分けるときの、正面方向に対する左右の振り幅 */
    private static final int PULT_SPREAD = 34;

    private final StageLayoutRepository     layoutRepo;
    private final StageLayoutSeatRepository seatRepo;
    private final ConcertProgramRepository  programRepo;

    private final ConcertDataRepository     concertRepo;
    private final OrchDataRepository        orchRepo;

    public StageLayoutService(StageLayoutRepository layoutRepo,
                              StageLayoutSeatRepository seatRepo,
                              ConcertProgramRepository programRepo,
                              ConcertDataRepository concertRepo,
                              OrchDataRepository orchRepo) {
        this.layoutRepo  = layoutRepo;
        this.seatRepo    = seatRepo;
        this.programRepo = programRepo;
        this.concertRepo = concertRepo;
        this.orchRepo    = orchRepo;
    }

    /** 演奏会の団体名（団体マスタ）を返す。見つからなければ null */
    public String orchNameOfConcert(String concertId) {
        return concertRepo.findById(concertId)
                .flatMap(c -> orchRepo.findById(c.getOrchId()))
                .map(OrchData::getOrchName)
                .orElse(null);
    }

    /** この配置を使っている最初の曲の演奏会の団体名。使われていなければ null */
    public String orchNameFromUsage(Long layoutId) {
        return programRepo.findByLayoutId(layoutId).stream()
                .sorted(Comparator.comparing(ConcertProgram::getConcertId)
                        .thenComparing(ConcertProgram::getProgramNo))
                .map(p -> orchNameOfConcert(p.getConcertId()))
                .filter(n -> n != null && !n.isBlank())
                .findFirst()
                .orElse(null);
    }

    /** 団体名が未入力なら、演奏会の団体名を登録する（呼び出し側のトランザクション内で使う） */
    public void fillOrchNameIfBlank(StageLayout layout, String concertId) {
        if (layout.getOrchName() == null || layout.getOrchName().isBlank()) {
            layout.setOrchName(trimOrNull(orchNameOfConcert(concertId), 64));
            layoutRepo.save(layout);
        }
    }

    // =========================================================
    // 標準配置テンプレート
    // =========================================================

    /** 弦楽セクション。プルトごとの基準座標を持ち、表／裏はそこから左右に振り分ける */
    private record StringSection(String code, String name, int[][] pults) {}

    /** 1人1席のパート */
    private record SoloSeat(String code, String name, int x, int y) {}

    private static final List<StringSection> STRING_SECTIONS = List.of(
            new StringSection("Vn1", "1stヴァイオリン",
                    new int[][] {{370, 660}, {340, 600}, {312, 540}, {286, 480}}),
            new StringSection("Vn2", "2ndヴァイオリン",
                    new int[][] {{495, 625}, {470, 565}, {447, 505}, {425, 445}}),
            new StringSection("Va", "ヴィオラ",
                    new int[][] {{705, 625}, {730, 565}, {755, 505}}),
            new StringSection("Vc", "チェロ",
                    new int[][] {{830, 655}, {855, 595}, {880, 535}}));

    private static final List<SoloSeat> SOLO_SEATS = List.of(
            // コントラバス（チェロの後方）
            new SoloSeat("Cb", "コントラバス", 960, 560),
            new SoloSeat("Cb", "コントラバス", 985, 500),
            new SoloSeat("Cb", "コントラバス", 1010, 440),
            // 木管 前列
            new SoloSeat("Fl", "フルート", 505, 400),
            new SoloSeat("Fl", "フルート", 575, 400),
            new SoloSeat("Ob", "オーボエ", 645, 400),
            new SoloSeat("Ob", "オーボエ", 715, 400),
            // 木管 後列
            new SoloSeat("Cl", "クラリネット", 505, 330),
            new SoloSeat("Cl", "クラリネット", 575, 330),
            new SoloSeat("Fg", "ファゴット", 645, 330),
            new SoloSeat("Fg", "ファゴット", 715, 330),
            // 金管 前列
            new SoloSeat("Hr", "ホルン", 395, 255),
            new SoloSeat("Hr", "ホルン", 465, 255),
            new SoloSeat("Hr", "ホルン", 535, 255),
            new SoloSeat("Hr", "ホルン", 605, 255),
            new SoloSeat("Tp", "トランペット", 700, 255),
            new SoloSeat("Tp", "トランペット", 770, 255),
            new SoloSeat("Tp", "トランペット", 840, 255),
            // 金管 後列
            new SoloSeat("Tb", "トロンボーン", 700, 185),
            new SoloSeat("Tb", "トロンボーン", 770, 185),
            new SoloSeat("Tb", "トロンボーン", 840, 185),
            new SoloSeat("Tu", "チューバ", 910, 185),
            // 打楽器
            new SoloSeat("Timp", "ティンパニ", 560, 110),
            new SoloSeat("Perc", "打楽器", 700, 110),
            new SoloSeat("Perc", "打楽器", 770, 110));

    /**
     * 標準的なオーケストラ配置の座席一覧を組み立てる。
     * 人物は未割り当て（person_id / person_name とも null）で返す。
     */
    public List<StageLayoutSeat> buildStandardSeats(Long layoutId) {
        List<StageLayoutSeat> seats = new ArrayList<>();
        int no = 1;

        seats.add(newSeat(layoutId, no++, StageLayoutSeat.ROLE_CONDUCTOR,
                "Cond", "指揮者", null, null, CONDUCTOR_X, CONDUCTOR_Y, 0));

        for (StringSection sec : STRING_SECTIONS) {
            for (int i = 0; i < sec.pults().length; i++) {
                int[] p = sec.pults()[i];
                int pultNo = i + 1;
                double[] perp = audienceSidePerpendicular(p[0], p[1]);
                int outX = (int) Math.round(p[0] + PULT_SPREAD * perp[0]);
                int outY = (int) Math.round(p[1] + PULT_SPREAD * perp[1]);
                int inX  = (int) Math.round(p[0] - PULT_SPREAD * perp[0]);
                int inY  = (int) Math.round(p[1] - PULT_SPREAD * perp[1]);

                seats.add(newSeat(layoutId, no++, StageLayoutSeat.ROLE_PLAYER,
                        sec.code(), sec.name(), pultNo, StageLayoutSeat.SIDE_OUT,
                        outX, outY, facingConductor(outX, outY)));
                seats.add(newSeat(layoutId, no++, StageLayoutSeat.ROLE_PLAYER,
                        sec.code(), sec.name(), pultNo, StageLayoutSeat.SIDE_IN,
                        inX, inY, facingConductor(inX, inY)));
            }
        }

        for (SoloSeat s : SOLO_SEATS) {
            seats.add(newSeat(layoutId, no++, StageLayoutSeat.ROLE_PLAYER,
                    s.code(), s.name(), null, null,
                    s.x(), s.y(), facingConductor(s.x(), s.y())));
        }
        return seats;
    }

    private StageLayoutSeat newSeat(Long layoutId, int seatNo, String roleType,
                                    String partCode, String partName,
                                    Integer pultNo, String seatSide,
                                    int x, int y, int rotation) {
        StageLayoutSeat seat = new StageLayoutSeat();
        seat.setLayoutId(layoutId);
        seat.setSeatNo(seatNo);
        seat.setRoleType(roleType);
        seat.setPartCode(partCode);
        seat.setPartName(partName);
        seat.setPultNo(pultNo);
        seat.setSeatSide(seatSide);
        seat.setPosX(x);
        seat.setPosY(y);
        seat.setRotation(rotation);
        return seat;
    }

    /** 指揮者のほうを向く角度（0 = 舞台奥向き、時計回りの度数）を返す */
    public static int facingConductor(int x, int y) {
        double dx = CONDUCTOR_X - x;
        double dy = CONDUCTOR_Y - y;
        if (dx == 0 && dy == 0) {
            return 0;
        }
        double deg = Math.toDegrees(Math.atan2(dx, -dy));
        return (int) Math.round((deg % 360 + 360) % 360);
    }

    /**
     * 指揮者を向いたときの真横方向のうち、客席側（y が増える側）を指す単位ベクトル。
     * プルトの「表」をこちら側、「裏」を反対側に置く。
     */
    private static double[] audienceSidePerpendicular(int x, int y) {
        double dx = CONDUCTOR_X - x;
        double dy = CONDUCTOR_Y - y;
        double len = Math.hypot(dx, dy);
        if (len == 0) {
            return new double[] {0, 1};
        }
        double px = -dy / len;
        double py = dx / len;
        if (py < 0) {
            px = -px;
            py = -py;
        }
        return new double[] {px, py};
    }

    // =========================================================
    // CRUD
    // =========================================================

    /** 配置を新規作成する。withTemplate が true なら標準配置の座席を流し込む */
    @Transactional
    public StageLayout create(String layoutName, boolean withTemplate) {
        StageLayout layout = new StageLayout();
        layout.setLayoutName(trim(layoutName, 64, "新しい配置"));
        layout.setStageWidth(STAGE_WIDTH);
        layout.setStageDepth(STAGE_DEPTH);
        layoutRepo.save(layout);
        if (withTemplate) {
            seatRepo.saveAll(buildStandardSeats(layout.getLayoutId()));
        }
        return layout;
    }

    /** 既存の座席をすべて捨てて標準配置で置き換える */
    @Transactional
    public void resetToStandard(Long layoutId) {
        StageLayout layout = require(layoutId);
        seatRepo.deleteByLayoutId(layoutId);
        seatRepo.flush();
        seatRepo.saveAll(buildStandardSeats(layoutId));
        layout.setStageWidth(STAGE_WIDTH);
        layout.setStageDepth(STAGE_DEPTH);
        layoutRepo.save(layout);
    }

    /** 配置を座席ごと複製する */
    @Transactional
    public StageLayout duplicate(Long layoutId) {
        StageLayout src = require(layoutId);
        StageLayout copy = new StageLayout();
        copy.setLayoutName(trim(src.getLayoutName() + " のコピー", 64, "配置のコピー"));
        copy.setOrchName(src.getOrchName());
        copy.setStageWidth(src.getStageWidth());
        copy.setStageDepth(src.getStageDepth());
        copy.setMemo(src.getMemo());
        layoutRepo.save(copy);

        List<StageLayoutSeat> copied = seatRepo.findByLayoutIdOrderBySeatNoAsc(layoutId).stream()
                .map(s -> {
                    StageLayoutSeat t = new StageLayoutSeat();
                    t.setLayoutId(copy.getLayoutId());
                    t.setSeatNo(s.getSeatNo());
                    t.setRoleType(s.getRoleType());
                    t.setPartCode(s.getPartCode());
                    t.setPartName(s.getPartName());
                    t.setPultNo(s.getPultNo());
                    t.setSeatSide(s.getSeatSide());
                    t.setPersonId(s.getPersonId());
                    t.setPersonName(s.getPersonName());
                    t.setPosX(s.getPosX());
                    t.setPosY(s.getPosY());
                    t.setRotation(s.getRotation());
                    t.setMemo(s.getMemo());
                    return t;
                })
                .toList();
        seatRepo.saveAll(copied);
        return copy;
    }

    /** 配置を削除し、参照していた曲目の layout_id を外す */
    @Transactional
    public void delete(Long layoutId) {
        for (ConcertProgram prog : programRepo.findByLayoutId(layoutId)) {
            prog.setLayoutId(null);
            programRepo.save(prog);
        }
        seatRepo.deleteByLayoutId(layoutId);
        layoutRepo.deleteById(layoutId);
    }

    // =========================================================
    // 一括保存
    // =========================================================

    /** 画面から送られてくる 1 座席分のデータ */
    public record SeatForm(Long seatId, String roleType, String partCode, String partName,
                           Integer pultNo, String seatSide, Long personId, String personName,
                           Integer posX, Integer posY, Integer rotation, String memo) {}

    /** 画面から送られてくる配置全体のデータ */
    public record LayoutForm(String layoutName, String orchName, Integer stageWidth, Integer stageDepth,
                             String memo, List<SeatForm> seats) {}

    /**
     * 配置と座席をまとめて保存する。
     * seatId を持つ座席は更新、持たない座席は追加、送られてこなかった座席は削除する。
     */
    @Transactional
    public StageLayout save(Long layoutId, LayoutForm form) {
        StageLayout layout = require(layoutId);
        if (form.layoutName() != null && !form.layoutName().isBlank()) {
            layout.setLayoutName(trim(form.layoutName(), 64, layout.getLayoutName()));
        }
        if (form.stageWidth() != null) {
            layout.setStageWidth(clamp(form.stageWidth(), 400, 4000));
        }
        if (form.stageDepth() != null) {
            layout.setStageDepth(clamp(form.stageDepth(), 300, 4000));
        }
        // 未入力なら紐づく演奏会の団体名を登録する
        String orchName = trimOrNull(form.orchName(), 64);
        layout.setOrchName(orchName != null ? orchName : trimOrNull(orchNameFromUsage(layoutId), 64));
        layout.setMemo(blankToNull(form.memo()));
        layoutRepo.save(layout);

        Map<Long, StageLayoutSeat> obsolete = seatRepo.findByLayoutIdOrderBySeatNoAsc(layoutId).stream()
                .collect(Collectors.toMap(StageLayoutSeat::getSeatId, Function.identity(),
                        (a, b) -> a, LinkedHashMap::new));

        List<StageLayoutSeat> saved = new ArrayList<>();
        int seatNo = 1;
        for (SeatForm f : form.seats() == null ? List.<SeatForm>of() : form.seats()) {
            // 他の配置の seatId が送られてきた場合は対象外なので、新規座席として扱う
            StageLayoutSeat seat = f.seatId() != null ? obsolete.remove(f.seatId()) : null;
            if (seat == null) {
                seat = new StageLayoutSeat();
                seat.setLayoutId(layoutId);
            }
            apply(seat, f, seatNo++, layout);
            saved.add(seat);
        }
        seatRepo.saveAll(saved);

        Collection<StageLayoutSeat> removed = obsolete.values();
        if (!removed.isEmpty()) {
            seatRepo.deleteAll(removed);
        }
        return layout;
    }

    private void apply(StageLayoutSeat seat, SeatForm f, int seatNo, StageLayout layout) {
        seat.setSeatNo(seatNo);
        seat.setRoleType(normalizeRole(f.roleType()));
        seat.setPartCode(trimOrNull(f.partCode(), 16));
        seat.setPartName(trimOrNull(f.partName(), 32));
        seat.setPultNo(f.pultNo() != null && f.pultNo() > 0 ? f.pultNo() : null);
        seat.setSeatSide(normalizeSide(f.seatSide()));
        seat.setPersonId(f.personId());
        seat.setPersonName(trimOrNull(f.personName(), 32));
        seat.setPosX(clamp(f.posX() == null ? 0 : f.posX(), 0, layout.getStageWidth()));
        seat.setPosY(clamp(f.posY() == null ? 0 : f.posY(), 0, layout.getStageDepth()));
        int rot = f.rotation() == null ? 0 : f.rotation();
        seat.setRotation(((rot % 360) + 360) % 360);
        seat.setMemo(trimOrNull(f.memo(), 128));
    }

    private static String normalizeRole(String role) {
        if (role == null) {
            return StageLayoutSeat.ROLE_PLAYER;
        }
        return switch (role) {
            case StageLayoutSeat.ROLE_CONDUCTOR, StageLayoutSeat.ROLE_SOLOIST,
                 StageLayoutSeat.ROLE_OTHER, StageLayoutSeat.ROLE_PLAYER -> role;
            default -> StageLayoutSeat.ROLE_PLAYER;
        };
    }

    private static String normalizeSide(String side) {
        if (StageLayoutSeat.SIDE_OUT.equals(side) || StageLayoutSeat.SIDE_IN.equals(side)) {
            return side;
        }
        return null;
    }

    private StageLayout require(Long layoutId) {
        return layoutRepo.findById(layoutId)
                .orElseThrow(() -> new IllegalArgumentException("不正な配置ID: " + layoutId));
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private static String trimOrNull(String s, int max) {
        if (s == null || s.isBlank()) {
            return null;
        }
        String t = s.trim();
        return t.length() > max ? t.substring(0, max) : t;
    }

    private static String trim(String s, int max, String fallback) {
        String t = trimOrNull(s, max);
        return t != null ? t : fallback;
    }
}

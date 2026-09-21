package com.amaorchnsuaru.manager.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * 全テーブル共通の監査項目。
 * delete_datetime が null のレコードのみを有効データとして扱う（各エンティティの @SQLRestriction）。
 * 論理削除の機能は無いため、delete_datetime は登録処理では設定しない。
 *
 * <p>画面（Thymeleaf の JS 埋め込みなど）へ JSON 化する際は、日時型を扱えず
 * 失敗するうえ画面でも使わないため、監査項目は出力しない。</p>
 */
@JsonIgnoreProperties({"createDatetime", "updateDatetime", "deleteDatetime", "updateBy"})
@MappedSuperclass
public abstract class AuditableEntity {

    private static final String SYSTEM_USER = "system";

    /** レコードを作成した日時 */
    @Column(name = "create_datetime", updatable = false)
    private LocalDateTime createDatetime;

    /** レコードを更新した日時 */
    @Column(name = "update_datetime")
    private LocalDateTime updateDatetime;

    /** レコードを削除した日時（null = 有効） */
    @Column(name = "delete_datetime")
    private LocalDateTime deleteDatetime;

    /** レコードを作成・更新・削除した人 */
    @Column(name = "update_by", length = 64)
    private String updateBy;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createDatetime == null) {
            createDatetime = now;
        }
        updateDatetime = now;
        updateBy = currentUser();
    }

    @PreUpdate
    void onUpdate() {
        updateDatetime = LocalDateTime.now();
        updateBy = currentUser();
    }

    private static String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return SYSTEM_USER;
        }
        return auth.getName();
    }

    public LocalDateTime getCreateDatetime() { return createDatetime; }
    public void setCreateDatetime(LocalDateTime createDatetime) { this.createDatetime = createDatetime; }

    public LocalDateTime getUpdateDatetime() { return updateDatetime; }
    public void setUpdateDatetime(LocalDateTime updateDatetime) { this.updateDatetime = updateDatetime; }

    public LocalDateTime getDeleteDatetime() { return deleteDatetime; }
    public void setDeleteDatetime(LocalDateTime deleteDatetime) { this.deleteDatetime = deleteDatetime; }

    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
}

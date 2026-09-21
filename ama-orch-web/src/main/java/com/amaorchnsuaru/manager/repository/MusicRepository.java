package com.amaorchnsuaru.manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amaorchnsuaru.manager.entity.Music;

public interface MusicRepository extends JpaRepository<Music, String> {

    List<Music> findAllByOrderByComposerNameAscMusicTitleFormalJpAsc();
}

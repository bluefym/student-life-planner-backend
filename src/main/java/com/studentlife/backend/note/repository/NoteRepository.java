package com.studentlife.backend.note.repository;

import com.studentlife.backend.note.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByUser_IdOrderByUpdatedAtDesc(Long userId);

    Optional<Note> findByIdAndUser_Id(Long id, Long userId);
}
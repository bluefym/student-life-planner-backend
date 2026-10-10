package com.studentlife.backend.note.service;

import com.studentlife.backend.note.dto.NoteRequest;
import com.studentlife.backend.note.dto.NoteResponse;
import com.studentlife.backend.note.entity.Note;
import com.studentlife.backend.note.repository.NoteRepository;
import com.studentlife.backend.user.User;
import com.studentlife.backend.user.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(
            NoteRepository noteRepository,
            UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
    }

    private Note getOwnedNote(Long noteId, Long userId) {
        return noteRepository.findByIdAndUser_Id(noteId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Note not found"));
    }

    @Transactional
    public NoteResponse create(String email, NoteRequest request) {
        User user = getUser(email);

        Note note = new Note();
        note.setTitle(request.title().trim());
        note.setContent(request.content());
        note.setUser(user);

        return NoteResponse.from(noteRepository.save(note));
    }

    public List<NoteResponse> getAll(String email) {
        User user = getUser(email);

        return noteRepository
                .findAllByUser_IdOrderByUpdatedAtDesc(user.getId())
                .stream()
                .map(NoteResponse::from)
                .toList();
    }

    public NoteResponse getById(String email, Long noteId) {
        User user = getUser(email);
        Note note = getOwnedNote(noteId, user.getId());

        return NoteResponse.from(note);
    }

    @Transactional
    public NoteResponse update(
            String email, Long noteId, NoteRequest request) {
        User user = getUser(email);
        Note note = getOwnedNote(noteId, user.getId());

        note.setTitle(request.title().trim());
        note.setContent(request.content());

        return NoteResponse.from(noteRepository.save(note));
    }

    @Transactional
    public void delete(String email, Long noteId) {
        User user = getUser(email);
        Note note = getOwnedNote(noteId, user.getId());

        noteRepository.delete(note);
    }
}   
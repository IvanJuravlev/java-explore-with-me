package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CreateCommentDto;
import ru.practicum.dto.comment.ShortCommentDto;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.User;
import ru.practicum.model.event.Event;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CommentDto createComment(Long userId, Long eventId, CreateCommentDto createCommentDto) {
        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        Comment comment = CommentMapper.COMMENT_MAPPER.createCommentDtoToComment(createCommentDto);
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());
        log.info("comment with id {} created", comment.getId());
        return CommentMapper.COMMENT_MAPPER.commentToCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateCommentByUser(Long userId, Long commentId, CreateCommentDto commentDto) {
        checkUser(userId);
        Comment comment = checkComment(commentId);

        if (comment.getAuthor().getId() != userId) {
            throw new ForbiddenException("Only author can edit comment");
        }

        comment.setText(commentDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());
        return CommentMapper.COMMENT_MAPPER.commentToCommentDto(comment);
    }

    @Transactional
    public CommentDto updateCommentByAdmin(Long commentId, CreateCommentDto commentDto) {
        Comment comment = checkComment(commentId);
        comment.setText(commentDto.getText());
        comment.setUpdatedOn(LocalDateTime.now());

        return CommentMapper.COMMENT_MAPPER.commentToCommentDto(comment);
    }

    @Transactional
    public void deleteCommentByUser(Long userId, Long commentId) {
        checkComment(commentId);
        checkUser(userId);
        commentRepository.deleteById(commentId);
        log.info("comment with id {} removed", commentId);
    }

    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        checkComment(commentId);
        commentRepository.deleteById(commentId);
        log.info("comment with id {} removed", commentId);
    }

    public CommentDto getComment(Long commentId) {
        Comment comment = checkComment(commentId);
        return CommentMapper.COMMENT_MAPPER.commentToCommentDto(comment);
    }

    public List<ShortCommentDto> getEventComments(Long eventId, Pageable pageable) {
        checkEvent(eventId);

        return commentRepository.findCommentsByEventId(eventId, pageable).stream()
                .map(CommentMapper.COMMENT_MAPPER::commentToShortCommentDto)
                .collect(Collectors.toList());
    }


    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("User with Id %x not found", userId)));
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Event with id %x not found", eventId)));
    }

    private Comment checkComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Comment with id %x not fouund", commentId)));
    }
}

package ru.practicum.controller.pub;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.ShortCommentDto;
import ru.practicum.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<ShortCommentDto> getComments(@RequestParam long eventId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(defaultValue = "10") @Positive Integer size) {
        PageRequest pageable = PageRequest.of(from / size, size);
        return commentService.getEventComments(eventId, pageable);
    }

    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable long commentId) {
        return commentService.getComment(commentId);
    }
}

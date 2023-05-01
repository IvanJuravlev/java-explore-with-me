package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CreateCommentDto;
import ru.practicum.service.CommentService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class AdminCommentController {
    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long commentId,
                                    @Valid @RequestBody CreateCommentDto commentDto) {
        return commentService.updateCommentByAdmin(commentId, commentDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByAdmin(@PathVariable Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}

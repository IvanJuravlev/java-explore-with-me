package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CreateCommentDto;
import ru.practicum.dto.comment.ShortCommentDto;
import ru.practicum.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentMapper COMMENT_MAPPER = Mappers.getMapper(CommentMapper.class);

    Comment createCommentDtoToComment(CreateCommentDto createCommentDto);

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "author", source = "author.id")
    CommentDto commentToCommentDto(Comment comment);

    ShortCommentDto commentToShortCommentDto(Comment comment);
}

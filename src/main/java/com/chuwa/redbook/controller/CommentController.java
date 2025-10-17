package com.chuwa.redbook.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chuwa.redbook.payload.CommentDto;
import com.chuwa.redbook.service.CommentService;

@RestController
@RequestMapping("/api/v1")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    /**
     * @param id
     * @param commentDto
     * @return
     */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable(value = "postId") long id,
                                                    @Valid  @RequestBody CommentDto commentDto) {
        logger.info("Received request to create comment for postId={} payload={}", id, commentDto);
        CommentDto created = commentService.createComment(id, commentDto);
        logger.info("Created comment for postId={} response={}", id, created);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/posts/{postId}/comments")
    public List<CommentDto> getCommentsByPostId(@PathVariable(value = "postId") Long postId) {
        logger.info("Fetching comments for postId={}", postId);
        List<CommentDto> comments = commentService.getCommentsByPostId(postId);
        logger.info("Found {} comments for postId={}", comments != null ? comments.size() : 0, postId);
        return comments;
    }

    @GetMapping("/posts/{postId}/comments/{id}")
    public ResponseEntity<CommentDto> getCommentsById(
            @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "id") Long commentId) {

        logger.info("Fetching comment id={} for postId={}", commentId, postId);
        CommentDto commentDto = commentService.getCommentById(postId, commentId);
        logger.info("Fetched comment id={} for postId={} response={}", commentId, postId, commentDto);
        return new ResponseEntity<>(commentDto, HttpStatus.OK);
    }

    @PutMapping("/posts/{postId}/comments/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable(value = "postId") Long postId,
                                                    @PathVariable(value = "id") Long commentId,
                                                    @RequestBody CommentDto commentDto) {
        logger.info("Updating comment id={} for postId={} payload={}", commentId, postId, commentDto);
        CommentDto updateComment = commentService.updateComment(postId, commentId, commentDto);
        logger.info("Updated comment id={} for postId={} response={}", commentId, postId, updateComment);
        return new ResponseEntity<>(updateComment, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}/comments/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable(value = "postId") Long postId,
                                                @PathVariable(value = "id") Long commentId) {
        logger.info("Deleting comment id={} for postId={}", commentId, postId);
        commentService.deleteComment(postId, commentId);
        logger.info("Deleted comment id={} for postId={}", commentId, postId);

        return new ResponseEntity<>("Comment deleted Successfully", HttpStatus.OK);
    }
}

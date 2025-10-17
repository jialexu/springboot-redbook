package com.chuwa.redbook.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.chuwa.redbook.dao.CommentRepository;
import com.chuwa.redbook.dao.PostRepository;
import com.chuwa.redbook.entity.Comment;
import com.chuwa.redbook.entity.Post;
import com.chuwa.redbook.exception.BlogAPIException;
import com.chuwa.redbook.exception.ResourceNotFoundException;
import com.chuwa.redbook.payload.CommentDto;
import com.chuwa.redbook.service.CommentService;

/**
 * @author b1go
 * @date 6/23/22 11:14 PM
 */
@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    /**
     * use this modelMapper to replace the mapToDto, mapToEntity methods.
     */
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CommentDto createComment(long postId, CommentDto commentDto) {
        logger.info("Creating comment for postId={}", postId);
        Comment comment = modelMapper.map(commentDto, Comment.class);
        // retrieve post entity by id
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with id={}", postId);
                    return new ResourceNotFoundException("Post", "id", postId);
                });
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
        logger.debug("Comment created: id={}, postId={}", savedComment.getId(), postId);

        return modelMapper.map(savedComment, CommentDto.class);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(long postId) {
        logger.info("Retrieving comments for postId={}", postId);
        // retrieve comments by postId
        List<Comment> comments = commentRepository.findByPostId(postId);

        // convert list of comment entities to list of comment dto's
        List<CommentDto> dtos = comments.stream()
                .map(comment -> modelMapper.map(comment, CommentDto.class))
                .collect(Collectors.toList());
        logger.debug("Found {} comments for postId={}", dtos.size(), postId);
        return dtos;
    }

    @Override
    public CommentDto getCommentById(Long postId, Long commentId) {
        logger.info("Retrieving commentId={} for postId={}", commentId, postId);
        // retrieve post entity by id
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with id={}", postId);
                    return new ResourceNotFoundException("Post", "id", postId);
                });

        // retrieve comment by id
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.error("Comment not found with id={}", commentId);
                    return new ResourceNotFoundException("Comment", "id", commentId);
                });

        // business logic
        if (!comment.getPost().getId().equals(post.getId())) {
            logger.error("Comment id={} does not belong to post id={}", commentId, postId);
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
        }

        logger.debug("Found comment id={} for postId={}", commentId, postId);
        return modelMapper.map(comment, CommentDto.class);
    }

    @Override
    public CommentDto updateComment(Long postId, Long commentId, CommentDto commentDtoRequest) {
        logger.info("Updating commentId={} for postId={}", commentId, postId);
        // retrieve post entity by id
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with id={}", postId);
                    return new ResourceNotFoundException("Post", "id", postId);
                });

        // retrieve comment by id
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.error("Comment not found with id={}", commentId);
                    return new ResourceNotFoundException("Comment", "id", commentId);
                });

        // business logic
        if (!comment.getPost().getId().equals(post.getId())) {
            logger.error("Comment id={} does not belong to post id={}", commentId, postId);
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
        }

        comment.setName(commentDtoRequest.getName());
        comment.setEmail(commentDtoRequest.getEmail());
        comment.setBody(commentDtoRequest.getBody());

        Comment updatedComment = commentRepository.save(comment);
        logger.debug("Updated comment id={} for postId={}", updatedComment.getId(), postId);

        return modelMapper.map(updatedComment, CommentDto.class);
    }

    @Override
    public void deleteComment(Long postId, Long commentId) {
        logger.info("Deleting commentId={} for postId={}", commentId, postId);
        // retrieve post entity by id
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with id={}", postId);
                    return new ResourceNotFoundException("Post", "id", postId);
                });

        // retrieve comment by id
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.error("Comment not found with id={}", commentId);
                    return new ResourceNotFoundException("Comment", "id", commentId);
                });

        if (!comment.getPost().getId().equals(post.getId())) {
            logger.error("Comment id={} does not belong to post id={}", commentId, postId);
            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Comment does not belong to post");
        }

        commentRepository.delete(comment);
        logger.debug("Deleted comment id={} for postId={}", commentId, postId);
    }
}

package com.chuwa.redbook.controller;

import com.chuwa.redbook.payload.PostDto;
import com.chuwa.redbook.payload.PostResponse;
import com.chuwa.redbook.service.PostService;
import com.chuwa.redbook.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author b1go
 * @date 8/22/22 7:14 PM
 */
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    @Autowired
    private PostService postService;

    @PostMapping()
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {
        logger.info("Received request to create a post: {}", postDto.getTitle());
        PostDto postResponse = postService.createPost(postDto);
        logger.info("Post created successfully with ID: {}", postResponse.getId());
        return new ResponseEntity<>(postResponse, HttpStatus.CREATED);
    }

    @GetMapping()
    public PostResponse getAllPosts(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIR, required = false) String sortDir
    ) {
        logger.debug("Fetching all posts with pagination params...");
        return postService.getAllPost(pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable(name = "id") long id) {
        logger.info("Fetching post with ID: {}", id);
        PostDto post = postService.getPostById(id);
        logger.debug("Fetched post details: {}", post);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePostById(@RequestBody PostDto postDto, @PathVariable(name = "id") long id) {
        logger.info("Updating post with ID: {}", id);
        logger.debug("Incoming update data: {}", postDto);
        PostDto postResponse = postService.updatePost(postDto, id);
        logger.info("Successfully updated post with ID: {}", id);
        return new ResponseEntity<>(postResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable(name = "id") long id) {
        logger.warn("Deleting post with ID: {}", id);
        postService.deletePostById(id);
        logger.info("Post with ID {} deleted successfully.", id);
        return new ResponseEntity<>("Post entity deleted successfully.", HttpStatus.OK);
    }

}

package com.project.backend.comment;

import com.project.backend.post.Post;
import com.project.backend.user.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
	List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User user);
    
}

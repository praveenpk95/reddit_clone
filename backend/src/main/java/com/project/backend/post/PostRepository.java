package com.project.backend.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.backend.subreddit.Subreddit;
import com.project.backend.user.User;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	
    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findByUser(User user);
    
}

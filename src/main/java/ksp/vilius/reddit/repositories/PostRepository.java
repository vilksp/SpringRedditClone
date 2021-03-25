package ksp.vilius.reddit.repositories;

import ksp.vilius.reddit.model.Post;
import ksp.vilius.reddit.model.Subreddit;
import ksp.vilius.reddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);
    List<Post> findByUser(User user);
}

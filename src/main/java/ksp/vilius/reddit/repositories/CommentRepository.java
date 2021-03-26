package ksp.vilius.reddit.repositories;

import ksp.vilius.reddit.dto.CommentsDto;
import ksp.vilius.reddit.model.Comment;
import ksp.vilius.reddit.model.Post;
import ksp.vilius.reddit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findAllByUser(User userToFind);
}

package ksp.vilius.reddit.repositories;

import ksp.vilius.reddit.model.Post;
import ksp.vilius.reddit.model.User;
import ksp.vilius.reddit.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}

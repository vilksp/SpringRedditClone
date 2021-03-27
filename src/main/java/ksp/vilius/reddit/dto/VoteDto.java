package ksp.vilius.reddit.dto;

import ksp.vilius.reddit.model.VoteType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class VoteDto {
    private VoteType voteType;
    private Long postId;
}

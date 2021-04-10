package ch.xavier.tags.repositories;

import ch.xavier.common.tags.Tag;
import lombok.Getter;

import java.util.HashSet;

@Getter
public class VersionedTag extends Tag {

    private final Long seqNumber;
    private final Long primaryTerm;

    private static final Long DEFAULT_SEQ_NUMBER = 0L;
    public static final Long DEFAULT_PRIMARY_TERM = 0L;

    VersionedTag(Tag tag, Long seqNumber, Long primaryTerm) {
        super(tag.getTagName(), tag.getMovieIds());
        this.seqNumber = seqNumber;
        this.primaryTerm = primaryTerm;
    }

    VersionedTag(String tagName) {
        super(tagName);
        this.seqNumber = DEFAULT_SEQ_NUMBER;
        this.primaryTerm = DEFAULT_PRIMARY_TERM;
    }

    VersionedTag withNewMovieId(long movieId) {
        if (getMovieIds().contains(movieId)) {
            return this;
        } else {
            HashSet<Long> movieIds = new HashSet<>(getMovieIds());
            movieIds.add(movieId);

            return new VersionedTag(
                    new Tag(getTag().getTagName(), movieIds),
                    DEFAULT_SEQ_NUMBER,
                    DEFAULT_PRIMARY_TERM
            );
        }
    }

    public Tag getTag() {
        return new Tag(this.getTagName(), this.getMovieIds());
    }

    @Override
    public String toString() {
        return "VersionedTag{" +
                "tag=" + this.getTag() +
                "seqNumber=" + seqNumber +
                ", primaryTerm=" + primaryTerm +
                '}';
    }
}

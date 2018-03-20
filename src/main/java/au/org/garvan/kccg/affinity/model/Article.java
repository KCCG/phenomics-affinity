

package au.org.garvan.kccg.affinity.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@ApiModel
public class Article {

    @JsonProperty(required = true)
    private String articleID;

    @JsonProperty(required = true)
    private String articleAbstract;

    @JsonProperty(required = true)
    private String language;

    @JsonCreator
    public static Article factory(@JsonProperty("articleID") String mediaItemId,
                                  @JsonProperty("articleAbstract") String matchableText,
                                  @JsonProperty("language") String language){

        return new Article(mediaItemId, matchableText, language);
    }

}

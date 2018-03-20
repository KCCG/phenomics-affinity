package au.org.garvan.kccg.affinity.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnnotationTerm {

    Integer tokenStartIndex;
    Integer tokenEndIndex;
    Integer startOffset;
    Integer endOffset;
}

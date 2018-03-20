package au.org.garvan.kccg.affinity.model;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AnnotationHit {

    String annotationID;
    List<AnnotationTerm> hits = new ArrayList<>();


}

package au.org.garvan.kccg.affinity.engine;

import au.org.garvan.kccg.affinity.engine.OntologyQueriesLoader.OntologyQueriesLoader;
import au.org.garvan.kccg.affinity.model.AnnotationHit;
import au.org.garvan.kccg.affinity.model.AnnotationTerm;
import au.org.garvan.kccg.affinity.model.Article;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.flax.luwak.InputDocument;
import uk.co.flax.luwak.Monitor;
import uk.co.flax.luwak.QueryError;
import uk.co.flax.luwak.QueryIndexConfiguration;
import uk.co.flax.luwak.matchers.*;
import uk.co.flax.luwak.presearcher.MatchAllPresearcher;
import uk.co.flax.luwak.queryparsers.LuceneQueryParser;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;

import static java.lang.String.format;

@Service
public class AffinityLuwakSearchEngine {

    protected static final Logger log = LoggerFactory.getLogger(AffinityLuwakSearchEngine.class);
    public static final String DEFAULT_FIELD = "article_abstract";
    public static final String QUERY_BANK_FILTER = "query_bank";

    private Monitor masterLuwakMonitor;

    private PartitionMatcher.PartitionMatcherFactory<HighlightsMatch> defaultHighlighterMatcher;
    private StandardAnalyzer standardAnalyzer;


    @Autowired
    OntologyQueriesLoader ontologyQueriesLoader;

    public AffinityLuwakSearchEngine() {
        log.info("Affinity Search Engine initializing");
        standardAnalyzer = new StandardAnalyzer();
        masterLuwakMonitor = configuredMonitor();
        defaultHighlighterMatcher  =
                PartitionMatcher.factory(
                        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()),
                        HighlightingMatcher.FACTORY,
                        Runtime.getRuntime().availableProcessors());
        log.info("Affinity search engine is successfully initialized.");


    }

    protected Monitor configuredMonitor() {
        QueryIndexConfiguration monitorConfiguration = new QueryIndexConfiguration();
        try {
            return new Monitor(
                    new LuceneQueryParser(DEFAULT_FIELD, standardAnalyzer),
                    new MatchAllPresearcher(),
                    new RAMDirectory(),
                    monitorConfiguration);
        } catch (IOException e) {
            log.error("Error creating master monitor ", e);
            throw new RuntimeException(e);
        }
    }


    public List<AnnotationHit> matchDocument(Article anArticle) {
        log.info("Processing individual article:" + anArticle.getArticleID());
        InputDocument document = InputDocument.builder(anArticle.getArticleID())
                .addField(DEFAULT_FIELD, anArticle.getArticleAbstract(), standardAnalyzer)
                .addField(QUERY_BANK_FILTER, "MONDO", standardAnalyzer)
                .build();


        try {
            List<AnnotationHit> annotationHits = new ArrayList<>();

            masterLuwakMonitor.match(document, defaultHighlighterMatcher)
                    .forEach(queryMatch -> {
                        if (!queryMatch.getMatches().isEmpty()) {
                            queryMatch.getMatches()
                                    .stream()
                                    .forEach(match -> {
                                        try {
                                            AnnotationHit tempHit = new AnnotationHit();
                                            tempHit.setAnnotationID(match.getQueryId());
                                            Set hits  =   match.getHits().get(DEFAULT_FIELD);
                                            for (Object hitObj : hits) {
                                                HighlightsMatch.Hit hit = (HighlightsMatch.Hit) hitObj;
                                                AnnotationTerm tempTerm = new AnnotationTerm(hit.startPosition,hit.endPosition, hit.startOffset, hit.endOffset);
                                                tempHit.getHits().add(tempTerm);
                                            }
                                            annotationHits.add(tempHit);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    });
                        }
                    });

            log.info("Matched Article ID:" + anArticle.getArticleID() + ", having language:"
                    + anArticle.getLanguage()
                    + " with Annotations Count:" + String.valueOf(annotationHits.size())
                    + Integer.toString(masterLuwakMonitor.getQueryCount()));

            return annotationHits;

        } catch (RuntimeException | IOException ex) {
            log.error("Error matching", ex);
            throw new RuntimeException(ex);
        }
    }


    public List<QueryError> loadQueries(OntologyQueriesLoader queryLoader) {
        List<QueryError> errors = queryLoader.loadQueries(masterLuwakMonitor);
        errors.forEach(error -> log.error(format("Annotation Queries %s could not be loaded", error.query)));
        return errors;
    }


}
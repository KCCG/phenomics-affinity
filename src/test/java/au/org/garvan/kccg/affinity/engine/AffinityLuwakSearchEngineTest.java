package au.org.garvan.kccg.affinity.engine;

import au.org.garvan.kccg.affinity.engine.OntologyQueriesLoader.OntologyQueriesLoaderTest;
import au.org.garvan.kccg.affinity.model.AnnotationHit;
import au.org.garvan.kccg.affinity.model.Article;
import com.google.common.base.Strings;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;


public class AffinityLuwakSearchEngineTest {

    private AffinityLuwakSearchEngine engine;
    private OntologyQueriesLoaderTest ontologyQueriesLoader;
    private String defaultLanguage;

    @Before
    public void setUp() {
        engine = new AffinityLuwakSearchEngine();
        ontologyQueriesLoader = new OntologyQueriesLoaderTest();
        defaultLanguage = "en";
    }

//    @After
//    public void tearDown() throws IOException {
//        engine.close();
//    }

    @Test
    public void matchSimplePhrase() {
        String queryId = dispatchQuery("Test1", "ICPH");
        Article anArticle = new Article("PMID:1", "I am feeling pain in icph chest.", "en");

        List<AnnotationHit> results = engine.matchDocument(anArticle);
        System.out.println("");
//        Assert.assertEquals(1,results.size());

    }
    @Test
    public void matchCompositePhrase() {
        String queryId = dispatchQuery("Test1", "\"chest\" OR \"Pain in Chest\"");
        Article anArticle = new Article("PMID:1", "I am feeling pain in chest.", "en");
        List<AnnotationHit> results = engine.matchDocument(anArticle);

    }

    private String dispatchQuery(String queryID, String queryString) {
        if (Strings.isNullOrEmpty(queryID)) {
            queryID = UUID.randomUUID().toString();
        }
        ontologyQueriesLoader.addQuery(queryID, queryString);
        engine.loadQueries(ontologyQueriesLoader);
        return queryID;
    }


}
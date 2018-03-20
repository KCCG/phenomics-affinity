package au.org.garvan.kccg.affinity.engine.OntologyQueriesLoader;

import uk.co.flax.luwak.Monitor;
import uk.co.flax.luwak.QueryError;

import java.util.List;

public interface OntologyQueriesLoader {
    List<QueryError> loadQueries(Monitor monitor);
}

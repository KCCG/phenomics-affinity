package au.org.garvan.kccg.affinity.engine.OntologyQueriesLoader;

import com.google.common.collect.Lists;
import lombok.NoArgsConstructor;
import uk.co.flax.luwak.*;

import java.io.IOException;
import java.util.*;

@NoArgsConstructor
public class OntologyQueriesLoaderTest implements  OntologyQueriesLoader{
    List<MonitorQuery> monitorQueries = new ArrayList<>();

    public String addQuery(String query) {
        String queryId = UUID.randomUUID().toString();
        monitorQueries.add(new MonitorQuery(queryId, query));
        return queryId;
    }

    public void addQuery(String id, String query) {
        monitorQueries.add(new MonitorQuery(id, query));
    }

    @Override
    public List<QueryError> loadQueries(Monitor monitor) {
        List<QueryError> errorList = Collections.synchronizedList(Lists.newArrayList());
        try {
            for(MonitorQuery mq: monitorQueries){
                monitor.update(mq);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UpdateException e) {
            e.printStackTrace();
        }
        return errorList;
    }


}
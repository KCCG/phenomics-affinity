package au.org.garvan.kccg.affinity.engine.OntologyQueriesLoader;

import com.google.common.collect.Lists;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.co.flax.luwak.ConcurrentQueryLoader;
import uk.co.flax.luwak.Monitor;
import uk.co.flax.luwak.MonitorQuery;
import uk.co.flax.luwak.QueryError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Component
public class FileOntologyQueriesLoader implements OntologyQueriesLoader {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    private String fileName;

    @Autowired
    public FileOntologyQueriesLoader(@Value("${spring.datasources.diseases.filename}") String fName){
        fileName = fName;
    }

    @Override
    public List<QueryError> loadQueries(Monitor monitor) {

        log.debug("loading diseases queries");

        List<QueryError> errorList = Collections.synchronizedList(Lists.newArrayList());
        ConcurrentQueryLoader loader = new ConcurrentQueryLoader(monitor, errorList);
//        String fileName2 = "mondoAffinityIndex.json";

        log.info(String.format("Reading index file. Filename:%s", fileName));
        String path = fileName;
        InputStream input = getClass().getResourceAsStream("resources/" + path);
        if (input == null) {
            // this is how we load file within editor (eg eclipse)
            input = FileOntologyQueriesLoader.class.getClassLoader().getResourceAsStream(path);
        }

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObject = (JSONObject) JSONValue.parse(stringBuilder.toString());

            if(jsonObject.containsKey("diseases")){
                JSONArray jsonDiseases = (JSONArray) jsonObject.get("diseases");

                for(Object obj: jsonDiseases){
                    JSONObject jsonDisease = (JSONObject) obj;
                    String query = jsonDisease.get("queryString").toString();
                    String id = jsonDisease.get("queryId").toString();

                    Map<String, String> metaData = new HashMap<>();
                    metaData.put("type",jsonDisease.get("queryType").toString());
                    log.debug(String.format("Adding query with id:%s and queryString:%s", id, query));
                    loader.add(new MonitorQuery(id, query, metaData));

                }
            }



            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            loader.close();
            log.info(String.format("Queries have been successfully loaded. Total count:%s", String.valueOf(monitor.getQueryCount())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!errorList.isEmpty())
            log.error("ERRORS!", errorList.get(0));

        return errorList;

    }

}

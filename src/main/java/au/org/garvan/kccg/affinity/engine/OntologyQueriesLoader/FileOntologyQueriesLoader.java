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
    private String diseaseIndexFileName;
    private String drugIndexFileName;

    @Autowired
    public FileOntologyQueriesLoader(@Value("${spring.datasources.diseases.filename}") String diseasefName,
                                     @Value("${spring.datasources.drugs.filename}") String drugfName){
        diseaseIndexFileName = diseasefName;
        drugIndexFileName = drugfName;
    }

    @Override
    public List<QueryError> loadQueries(Monitor monitor) {

        List<QueryError> errorList = Collections.synchronizedList(Lists.newArrayList());
        errorList.addAll(loadMondoIndexIntoMonitor(monitor));
        errorList.addAll(loadDrugBankIndexIntoMonitor(monitor));
        return errorList;

    }


    List<QueryError> loadMondoIndexIntoMonitor(Monitor monitor) {
        List<QueryError> errorList = Collections.synchronizedList(Lists.newArrayList());
        ConcurrentQueryLoader loader = new ConcurrentQueryLoader(monitor, errorList);
        log.debug("loading diseases queries");
        log.info(String.format("Reading index file. Filename:%s", diseaseIndexFileName));
        String path = diseaseIndexFileName;
        InputStream input = getClass().getResourceAsStream("resources/" + path);
        if (input == null) {
            // this is how we load file within editor (eg eclipse)
            input = FileOntologyQueriesLoader.class.getClassLoader().getResourceAsStream(path);
        }

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObject = (JSONObject) JSONValue.parse(stringBuilder.toString());

            if (jsonObject.containsKey("diseases")) {
                JSONArray jsonDiseases = (JSONArray) jsonObject.get("diseases");

                for (Object obj : jsonDiseases) {
                    JSONObject jsonDisease = (JSONObject) obj;
                    String query = jsonDisease.get("queryString").toString();
                    String id = jsonDisease.get("queryId").toString();

//                    Map<String, String> metaData = new HashMap<>();
//                    metaData.put("type", jsonDisease.get("queryType").toString());
                    log.debug(String.format("Adding query with id:%s and queryString:%s", id, query));
                    loader.add(new MonitorQuery(id, query));

                }
            }

            reader.close();
        } catch (IOException e) {
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

        if (!errorList.isEmpty())
            log.error("ERRORS!", errorList.get(0));

        return errorList;

    }


    List<QueryError> loadDrugBankIndexIntoMonitor(Monitor monitor) {
        List<QueryError> errorList = Collections.synchronizedList(Lists.newArrayList());
        ConcurrentQueryLoader loader = new ConcurrentQueryLoader(monitor, errorList);
        log.debug("loading drug queries");
        log.info(String.format("Reading index file. Filename:%s", drugIndexFileName));
        String path = drugIndexFileName;
        InputStream input = getClass().getResourceAsStream("resources/" + path);
        if (input == null) {
            // this is how we load file within editor (eg eclipse)
            input = FileOntologyQueriesLoader.class.getClassLoader().getResourceAsStream(path);
        }

        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            String line;
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            JSONObject jsonObject = (JSONObject) JSONValue.parse(stringBuilder.toString());

            if (jsonObject.containsKey("drugs")) {
                JSONArray jsonDrugs = (JSONArray) jsonObject.get("drugs");
                for (Object obj : jsonDrugs) {
                    JSONObject jsonDrug = (JSONObject) obj;
                    String query = jsonDrug.get("queryString").toString();
                    String id = jsonDrug.get("queryId").toString();

//                    Map<String, String> metaData = new HashMap<>();
//                    metaData.put("type", jsonDrug.get("queryType").toString());
                    log.debug(String.format("Adding query with id:%s and queryString:%s", id, query));
                    loader.add(new MonitorQuery(id, query));
                }
            }

            reader.close();
        } catch (IOException e) {
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

        if (!errorList.isEmpty())
            log.error("ERRORS!", errorList.get(0));

        return errorList;

    }


}

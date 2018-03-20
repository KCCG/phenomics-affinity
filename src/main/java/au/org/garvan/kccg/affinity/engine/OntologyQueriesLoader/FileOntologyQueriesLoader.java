package au.org.garvan.kccg.affinity.engine.OntologyQueriesLoader;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.co.flax.luwak.ConcurrentQueryLoader;
import uk.co.flax.luwak.Monitor;
import uk.co.flax.luwak.MonitorQuery;
import uk.co.flax.luwak.QueryError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class FileOntologyQueriesLoader implements OntologyQueriesLoader {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<QueryError> loadQueries(Monitor monitor) {

        log.debug("loading diseases queries");

        List<QueryError> errorList = Collections.synchronizedList(Lists.newArrayList());
        ConcurrentQueryLoader loader = new ConcurrentQueryLoader(monitor, errorList);
        String fileName = "mondo.txt";
        List<String> fileHeader;

        log.info(String.format("Reading ontology file. Filename:%s", fileName));
        String path = fileName;
        InputStream input = getClass().getResourceAsStream("resources/" + path);
        if (input == null) {
            // this is how we load file within editor (eg eclipse)
            input = FileOntologyQueriesLoader.class.getClassLoader().getResourceAsStream(path);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String headerLine = null;
        try {
            headerLine = reader.readLine();
            fileHeader = Arrays.asList(headerLine.split(","));
            String line;
            Integer qid = 1;
            while ((line = reader.readLine()) != null) {
                String splits [] = line.split(":::");
                if(splits.length>1) {
                    loader.add(new MonitorQuery(splits[0], splits[1]));
                }
                else{
                    log.debug(String.format("Error in query Loading for string %s",line));
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
            log.debug(String.valueOf(monitor.getQueryCount()) + " queries loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!errorList.isEmpty())
            log.error("ERRORS!", errorList.get(0));

        return errorList;

    }

}

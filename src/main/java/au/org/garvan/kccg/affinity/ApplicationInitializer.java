package au.org.garvan.kccg.affinity;

import au.org.garvan.kccg.affinity.engine.AffinityLuwakSearchEngine;
import au.org.garvan.kccg.affinity.engine.OntologyQueriesLoader.OntologyQueriesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Created by ahmed on 27/11/17.
 */
@Component
public class ApplicationInitializer {
    protected static final Logger log = LoggerFactory.getLogger(ApplicationInitializer.class);

    @Autowired
    private AffinityLuwakSearchEngine affinityLuwakSearchEngine;

    @Autowired
    OntologyQueriesLoader queryLoader;


    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        log.info("Initializing Application");

        try {
            affinityLuwakSearchEngine.loadQueries(queryLoader);
            log.info("Initialized Processing Engine successfully");

        } catch(RuntimeException ex) {
            log.error("Failed to initialize Processing engine.", ex);
            System.exit(1);
        }


//        try {
//            queryManager.init();
//            log.info("Initialized Query Engine successfully");
//
//        } catch(RuntimeException ex) {
//            log.error("Failed to initialize Query engine.", ex);
//            System.exit(1);
//        }

    }
}


package au.org.garvan.kccg.affinity.engine.cache;

import au.org.garvan.kccg.affinity.model.AnnotationHit;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class AnnotationHitCache {

    private static LoadingCache<String, List<AnnotationHit>> hitsCache;
    private static LoadingCache<String, Boolean> emptyHitsCache;
    private static final Logger slf4jLogger = LoggerFactory.getLogger(AnnotationHitCache.class);

    static {
        slf4jLogger.info("Initializing Annotation Hit Response Cache.");
        hitsCache = CacheBuilder.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build(
                        new CacheLoader<String, List<AnnotationHit>>(){
                            @Override
                            public List<AnnotationHit> load(String key) throws Exception {
                                return new ArrayList<>();
                            }
                        }
                );

        emptyHitsCache = CacheBuilder.newBuilder()
                .maximumSize(200)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build(
                        new CacheLoader<String, Boolean>(){
                            @Override
                            public Boolean load(String key) throws Exception {
                                return false;
                            }
                        }
                );



    }

    private static void put(String key, List<AnnotationHit> articles){
        hitsCache.put(key, articles);
        slf4jLogger.info(String.format("Approximate size of Annotation Hit cache:%d", hitsCache.size()));

    }




    private static List<AnnotationHit> get(String key){
        List<AnnotationHit> cacheHit = null;
        try {
            slf4jLogger.info(String.format("Get call for annotation hits with key:%s", key));
            cacheHit = hitsCache.get(key);
              if(cacheHit.size()==0){
                  slf4jLogger.info(String.format("Cache miss for key:%s", key));
                  return null;
              }
        } catch (Exception e) {
            e.printStackTrace();
        }
        slf4jLogger.info(String.format("Cache hit for key:%s", key));
        return cacheHit;
    }


    public static List<AnnotationHit> getArticles(String key){
        try {
            if(emptyHitsCache.get(key)){
                slf4jLogger.info(String.format("Empty Cache hit for key:%s", key));
                return new ArrayList<>();
            }
            else
                return get(key);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void putArticles(String key, List<AnnotationHit> annotationHits){
        slf4jLogger.info(String.format("Put call annotation hits with key:%s and size:%d", key, annotationHits.size()));
        if(annotationHits.size()<1) {
            slf4jLogger.info(String.format("Empty storage key:%s", key));
            emptyHitsCache.put(key, true);
        }
        else
            put(key, annotationHits);
    }


    public static void clearCache(){
        slf4jLogger.info("Flushing all hits from cache.");
        hitsCache.invalidateAll();
    }


}

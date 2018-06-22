package au.org.garvan.kccg.affinity.web;

import au.org.garvan.kccg.affinity.engine.AffinityLuwakSearchEngine;
import au.org.garvan.kccg.affinity.engine.cache.AnnotationHitCache;
import au.org.garvan.kccg.affinity.model.AnnotationHit;
import au.org.garvan.kccg.affinity.model.Article;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AnnotationController {
    protected static final Logger log = LoggerFactory.getLogger(AnnotationController.class);

    @Autowired
    private AffinityLuwakSearchEngine engine;

    @ApiOperation(value = "postAnnotation", nickname = "postAnnotation")
    @RequestMapping(value = "/annotation", method = RequestMethod.POST, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    public List<AnnotationHit> searchAnnotation(@ApiParam("mediaItemDocument") @RequestBody Article article) {
        return engine.matchDocument(article);
    }

    @ApiOperation(value = "postAnnotation", nickname = "postAnnotation")
    @RequestMapping(value = "/annotationAsync", method = RequestMethod.POST, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = boolean.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    public boolean searchAnnotationAsync(@ApiParam("mediaItemDocument") @RequestBody Article article) {
         engine.matchDocumentAsync(article);
         return true;
    }


    @ApiOperation(value = "postAnnotation", nickname = "postAnnotation")
    @RequestMapping(value = "/annotationResults", method = RequestMethod.POST, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success", response = String.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Failure")})
    public List<AnnotationHit> searchAnnotationResults(@ApiParam("mediaItemDocument") @RequestBody Article article) {
        List<AnnotationHit> results = AnnotationHitCache.getArticles(article.getArticleID());
        if(results ==null) {
            try {
                log.info(String.format("Getresult Attempt 1: Result is not ready in time for article ID:%s", article.getArticleID()));
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            results = AnnotationHitCache.getArticles(article.getArticleID());
            if(results ==null) {
                log.info(String.format("Getresult Attempt 2: Result is not ready in time for article ID:%s", article.getArticleID()));
                results = engine.matchDocument(article);
            }
        }
        return results;

    }



}


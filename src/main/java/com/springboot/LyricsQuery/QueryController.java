package com.springboot.LyricsQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.springboot.LyricsQuery.retrieval.ProximitySearch;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.text.html.parser.Entity;

@RestController
public class QueryController {
    @GetMapping(path="/lyrics/{query}", produces= MediaType.APPLICATION_JSON_VALUE)
    public List<Song> retrieveCoursesForStudent(@PathVariable String query) throws Exception {
        ProximitySearch proximitySearch = new ProximitySearch();
        List<String> resStr = proximitySearch.retrieveQuery(query);
        List<Song> res = new ArrayList<>();
        for (String result: resStr) {
            String[] songInfo = result.split(",:");
            Song song = new Song(songInfo[0], songInfo[1]);
            res.add(song);
        }
        System.out.println("[query lyrics]: "+query);
        return res;
    }

//    @GetMapping("/students/{studentId}/courses/{courseId}")
//    public Course retrieveDetailsForCourse(@PathVariable String studentId,
//                                           @PathVariable String courseId) {
//        return studentService.retrieveCourse(studentId, courseId);
//    }

}

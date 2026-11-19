package lab.feed;
import org.springframework.web.bind.annotation.*;
@RestController
public class ScenarioController {
    final ScenarioService service;
    public ScenarioController(ScenarioService service) {
        this.service=service;
    }
    @GetMapping("/api/scenario") public String description() {
        return "feed: 朋友圈：推拉结合及读取权限";
    }
    @PostMapping("/api/posts") public void publish(@RequestParam long id,@RequestParam long author,@RequestParam long at,@RequestParam(defaultValue="false") boolean celebrity) {
        service.publish(id,author,at,celebrity);
    }
    @GetMapping("/api/feed") public java.util.List<Long> feed(@RequestParam long reader) {
        return service.timeline(reader,Long.MAX_VALUE,Long.MAX_VALUE);
    }
}

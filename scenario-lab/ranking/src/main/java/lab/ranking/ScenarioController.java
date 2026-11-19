package lab.ranking;
import org.springframework.web.bind.annotation.*;
@RestController
public class ScenarioController {
    final ScenarioService service;
    public ScenarioController(ScenarioService service) {
        this.service=service;
    }
    @GetMapping("/api/scenario") public String description() {
        return "ranking: 排行榜：事件去重与乱序版本保护";
    }
    @PostMapping("/api/score") public boolean add(@RequestParam String event,@RequestParam long delta) {
        return service.add(event,delta);
    }
}

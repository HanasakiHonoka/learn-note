package lab.scheduler;
import org.springframework.web.bind.annotation.*;
@RestController
public class ScenarioController {
    final ScenarioService service;
    public ScenarioController(ScenarioService service) {
        this.service=service;
    }
    @GetMapping("/api/scenario") public String description() {
        return "scheduler: 任务调度：租约、重领与 fencing token";
    }
    @PostMapping("/api/acquire") public long acquire(@RequestParam String owner,@RequestParam long now,@RequestParam long until) {
        return service.acquire(owner,now,until);
    }
}

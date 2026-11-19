package lab.redpacket;
import org.springframework.web.bind.annotation.*;
@RestController
public class ScenarioController {
    final ScenarioService service;
    public ScenarioController(ScenarioService service) {
        this.service=service;
    }
    @GetMapping("/api/scenario") public String description() {
        return "redpacket: 红包：并发领取与金额守恒";
    }
    @PostMapping("/api/claim") public boolean claim(@RequestParam long user) {
        return service.claim(user);
    }
}

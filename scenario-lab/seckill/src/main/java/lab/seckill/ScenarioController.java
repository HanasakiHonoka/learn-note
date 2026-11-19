package lab.seckill;
import org.springframework.web.bind.annotation.*;
@RestController
public class ScenarioController {
    final ScenarioService service;
    public ScenarioController(ScenarioService service) {
        this.service=service;
    }
    @GetMapping("/api/scenario") public String description() {
        return "seckill: 秒杀：条件扣库、一人一单与事务回滚";
    }
    @PostMapping("/api/buy") public boolean buy(@RequestParam long user) {
        return service.buy(user,false);
    }
}

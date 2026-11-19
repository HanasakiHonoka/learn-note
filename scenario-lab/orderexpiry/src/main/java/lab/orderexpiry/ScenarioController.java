package lab.orderexpiry;
import org.springframework.web.bind.annotation.*;
@RestController
public class ScenarioController {
    final ScenarioService service;
    public ScenarioController(ScenarioService service) {
        this.service=service;
    }
    @GetMapping("/api/scenario") public String description() {
        return "orderexpiry: 超时订单：支付关单竞争及晚到成功补偿";
    }
    @PostMapping("/api/close") public boolean close(@RequestParam long now) {
        return service.close(now);
    }
}

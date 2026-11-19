package lab.payment;
import org.springframework.web.bind.annotation.*;
@RestController
public class ScenarioController {
    final ScenarioService service;
    public ScenarioController(ScenarioService service) {
        this.service=service;
    }
    @GetMapping("/api/scenario") public String description() {
        return "payment: 支付：重复通知、outbox 与退款额度";
    }
    @PostMapping("/api/confirm") public boolean confirm(@RequestParam long amount) {
        return service.confirm(amount,false);
    }
}

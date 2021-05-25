package greencity.webcontroller;

import greencity.service.GraphService;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/management")
@RequiredArgsConstructor
public class GraphController {
    private final GraphService graphService;
    private final List<String> months =
        Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");

    /**
     * Draw graphs with statistics.
     *
     * @param model model
     * @return {@link String} path to the template part.
     * @author Vasyl Zhovnir
     */
    @GetMapping("/displayGraph")
    public String displayGraph(Model model) {
        Map<String, Integer> usersByCities = graphService.getGeneralStatisticsForAllUsersByCities();
        Map<Integer, Long> generalRegistrationStatistics = graphService.getRegistrationStatistics();
        model.addAttribute("months", months);
        model.addAttribute("usersByCities", usersByCities);
        model.addAttribute("generalRegistrationStatistics", generalRegistrationStatistics);
        return "core/management_general_statistics_for_users";
    }
}

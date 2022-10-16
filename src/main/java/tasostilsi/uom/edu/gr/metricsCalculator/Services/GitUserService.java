package tasostilsi.uom.edu.gr.metricsCalculator.Services;

import ch.qos.logback.classic.Logger;
import nonapi.io.github.classgraph.json.JSONSerializer;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tasostilsi.uom.edu.gr.metricsCalculator.Models.GitUser;
import tasostilsi.uom.edu.gr.metricsCalculator.Services.Interfaces.IGitUserService;

@Service
public class GitUserService implements IGitUserService {

    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(GitUserService.class);

    private GitUser user;


    @Override
    public GitUser getUser(String id) {
        LOGGER.info("Reply: " + JSONSerializer.serializeObject(this.user));
        return this.user;
    }

    @Override
    public GitUser createUser(GitUser user) {
        this.user = new GitUser();
        this.user.setUsername(user.getUsername());
        this.user.setEmail(user.getEmail());
        this.user.setAccessToken(user.getAccessToken());

        LOGGER.info("Reply: " + JSONSerializer.serializeObject(this.user));

        return this.user;
    }
}

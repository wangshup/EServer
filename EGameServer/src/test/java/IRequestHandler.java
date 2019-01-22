import com.dd.server.request.Request;
import io.netty.channel.Channel;

public interface IRequestHandler {
    void handle(Channel ch, Request request) throws Exception;
}

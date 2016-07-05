package app.loader.scheduled.com.aol.micro.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;

import com.aol.cyclops.control.Maybe;
import com.aol.cyclops.data.collections.extensions.persistent.PStackX;
import com.aol.micro.server.async.data.loader.DataLoader;
import com.aol.micro.server.auto.discovery.Rest;
import com.aol.micro.server.couchbase.DistributedMapClient;
import com.aol.micro.server.events.SystemData;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

@Path("/couchbase")
@Rest
public class CouchbaseResource {

	private final DistributedMapClient client;
	private volatile PStackX<SystemData> dataLoads = PStackX.empty();

	@Autowired
	public CouchbaseResource(DistributedMapClient client, EventBus bus) {
		this.client = client;
		bus.register(this);
	}

	@Subscribe
	public synchronized void events(SystemData event) {
		dataLoads = dataLoads.plus(event);

	}

	@GET
	@Path("/loading-events")
	@Produces("application/json")
	public synchronized PStackX<SystemData> loadingEvents() {
		return dataLoads;
	}

	@GET
	@Path("/maybe")
	@Produces("application/json")
	public Maybe<String> maybe() {
		return Maybe.just("hello-world");
	}

	@GET
	@Path("/get")
	public String bucket() {
		return client	.get("hello")
						.toString();
	}

	@GET
	@Path("/put")
	public String put() {
		client.put(	"hello",
					"world");
		return "added";
	}
}
